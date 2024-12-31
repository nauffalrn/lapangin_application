package com.lapangin.web.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lapangin.web.dto.BookingDTO;
import com.lapangin.web.dto.BookingRequest;
import com.lapangin.web.dto.BookingResponse;
import com.lapangin.web.dto.JadwalResponse;
import com.lapangin.web.dto.LapanganResponse;
import com.lapangin.web.dto.PromoDTO;
import com.lapangin.web.dto.PromoRequest;
import com.lapangin.web.dto.Response;
import com.lapangin.web.dto.ReviewDTO;
import com.lapangin.web.model.Booking;
import com.lapangin.web.model.Customer;
import com.lapangin.web.model.Lapangan;
import com.lapangin.web.model.Promo;
import com.lapangin.web.model.Review;
import com.lapangin.web.service.BookingService;
import com.lapangin.web.service.CustomerService;
import com.lapangin.web.service.LapanganService;
import com.lapangin.web.service.PromoService;
import com.lapangin.web.service.ReviewService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    private final CustomerService customerService;
    private final PromoService promoService;
    private final LapanganService lapanganService;
    private final ReviewService reviewService;

    private static final String UPLOAD_DIR = "src/main/resources/static/payment-proofs/";

    @Autowired
    public BookingController(BookingService bookingService,
                             CustomerService customerService,
                             PromoService promoService,
                             LapanganService lapanganService,
                             ReviewService reviewService) {
        this.bookingService = bookingService;
        this.customerService = customerService;
        this.promoService = promoService;
        this.lapanganService = lapanganService;
        this.reviewService = reviewService;
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createBooking(@RequestBody BookingRequest bookingRequest, Principal principal) {
        try {
            // Validasi Principal
            if (principal == null || principal.getName() == null) {
                logger.warn("Unauthorized access attempt.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(false, "Unauthorized."));
            }

            // Cari Customer berdasarkan username
            Customer customer = customerService.findByUsername(principal.getName());
            if (customer == null) {
                logger.warn("Customer tidak ditemukan untuk username: {}", principal.getName());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(false, "Customer tidak ditemukan."));
            }

            String kodePromo = bookingRequest.getKodePromo();
            Promo promo = null;

            // Validasi Promo jika tersedia
            if (kodePromo != null && !kodePromo.trim().isEmpty()) {
                promo = promoService.findByKodePromo(kodePromo);
                if (promo == null) {
                    logger.warn("Promo tidak valid: {}", kodePromo);
                    return ResponseEntity.badRequest().body(new Response(false, "Promo tidak valid."));
                }
                if (promoService.isPromoClaimedByCustomer(customer, promo)) {
                    logger.warn("Promo sudah diklaim oleh customer: {}", customer.getUsername());
                    return ResponseEntity.badRequest().body(new Response(false, "Promo sudah diklaim."));
                }

                if (!promoService.isPromoValid(promo)) {
                    logger.warn("Promo sudah tidak berlaku: {}", promo.getKodePromo());
                    return ResponseEntity.badRequest().body(new Response(false, "Promo sudah tidak berlaku."));
                }
            }

            // Buat Booking
            Booking booking = bookingService.createBooking(
                customer,
                bookingRequest.getLapanganId(),
                bookingRequest.getTanggal(),
                bookingRequest.getJadwalList(),
                kodePromo
            );

            return ResponseEntity.ok(new BookingResponse(true, "Booking berhasil dibuat.", booking.getId()));
        } catch (RuntimeException e) {
            logger.error("Gagal membuat booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(false, e.getMessage()));
        } catch (Exception e) {
            logger.error("Gagal membuat booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Gagal membuat booking."));
        }
    }

    @PostMapping("/claim")
    public ResponseEntity<Response> claimPromo(@RequestBody PromoRequest request, Principal principal) {
        logger.info("Menerima permintaan klaim promo: {}", request.getKodePromo());

        if (principal == null || principal.getName() == null) {
            logger.warn("Pengguna tidak terautentikasi.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(false, "Unauthorized."));
        }

        Customer customer = customerService.findByUsername(principal.getName());
        if (customer == null) {
            logger.warn("Customer tidak ditemukan untuk username: {}", principal.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(false, "Customer tidak ditemukan."));
        }

        Promo promo = promoService.findByKodePromo(request.getKodePromo());
        if (promo == null) {
            logger.warn("Promo tidak valid: {}", request.getKodePromo());
            return ResponseEntity.badRequest().body(new Response(false, "Promo tidak valid."));
        }

        if (promoService.isPromoClaimedByCustomer(customer, promo)) {
            logger.warn("Promo sudah diklaim oleh customer: {}", customer.getUsername());
            return ResponseEntity.badRequest().body(new Response(false, "Promo sudah diklaim."));
        }

        if (!promoService.isPromoValid(promo)) {
            logger.warn("Promo sudah tidak berlaku: {}", promo.getKodePromo());
            return ResponseEntity.badRequest().body(new Response(false, "Promo sudah tidak berlaku."));
        }

        promoService.claimPromo(customer, promo);
        return ResponseEntity.ok(new Response(true, "Promo berhasil diklaim."));
    }

    @GetMapping("/lapangan/{id}")
    public ResponseEntity<LapanganResponse> getLapanganDetail(@PathVariable Long id) {
        Lapangan lapangan = lapanganService.getLapanganById(id);
        if (lapangan == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        LapanganResponse response = new LapanganResponse(
            lapangan.getId(),
            lapangan.getNamaLapangan(),
            lapangan.getJamBuka(),
            lapangan.getJamTutup(),
            lapangan.getPrice()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/jadwal")
    public ResponseEntity<List<JadwalResponse>> getJadwalLapangan(
        @RequestParam Long lapanganId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal
    ) {
        try {
            List<JadwalResponse> jadwal = bookingService.getJadwalLapangan(lapanganId, tanggal);
            return ResponseEntity.ok(jadwal);
        } catch (Exception e) {
            // Log error jika diperlukan
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/payment/upload")
    public ResponseEntity<Response> uploadPaymentProof(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        try {
            // Validasi Principal
            if (principal == null || principal.getName() == null) {
                logger.warn("Unauthorized access attempt for booking payment.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(false, "Unauthorized."));
            }

            // Cari Customer berdasarkan username
            Customer customer = customerService.findByUsername(principal.getName());
            Booking booking = bookingService.getBookingById(bookingId);
            if (booking == null || !booking.getCustomer().getId().equals(customer.getId())) {
                logger.warn("Customer {} tidak berhak mengupload bukti untuk booking ID: {}", customer.getUsername(), bookingId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(false, "Anda tidak berhak mengupload bukti untuk booking ini."));
            }

            // Update Payment Proof
            bookingService.updatePaymentProof(bookingId, file);

            return ResponseEntity.ok(new Response(true, "Bukti pembayaran berhasil diupload."));
        } catch (Exception e) {
            logger.error("Gagal mengupload bukti pembayaran: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Gagal mengupload bukti pembayaran."));
        }
    }

    @DeleteMapping("/cancel/{bookingId}")
    public ResponseEntity<Response> cancelBooking(@PathVariable Long bookingId, Principal principal) {
        try {
            Customer customer = customerService.findByUsername(principal.getName());
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(false, "Unauthorized."));
            }

            boolean isCancelled = bookingService.cancelBooking(bookingId, customer);
            if (isCancelled) {
                return ResponseEntity.ok(new Response(true, "Booking berhasil dibatalkan."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, "Gagal membatalkan booking."));
            }
        } catch (Exception e) {
            logger.error("Gagal membatalkan booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Gagal membatalkan booking."));
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDTO>> getBookings(
            @RequestParam("tanggal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null);
            }

            Customer customer = customerService.findByUsername(principal.getName());
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null);
            }

            List<BookingDTO> bookings = bookingService.getBookingsByCustomerAndDate(customer, tanggal);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            logger.error("Gagal mengambil bookings:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Endpoint untuk mendapatkan history booking.
     *
     * @param principal Principal user yang sedang login
     * @return ResponseEntity dengan list booking atau pesan tidak ditemukan
     */
    @GetMapping("/history")
    public ResponseEntity<?> getBookingHistory(Principal principal) {
        try {
            Customer customer = customerService.findByUsername(principal.getName());
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer tidak ditemukan.");
            }
            List<BookingDTO> bookingDTOs = bookingService.getBookingHistoryDTO(customer);
            return ResponseEntity.ok(bookingDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan.");
        }
    }

    /**
     * Endpoint untuk submit review.
     *
     * @param bookingId ID Booking
     * @param rating Rating yang diberikan
     * @param komentar Komentar review
     * @param principal Principal user yang sedang login
     * @return ResponseEntity dengan pesan sukses atau error
     */
    @PostMapping("/review")
    public ResponseEntity<?> submitReview(
            @RequestParam Long bookingId,
            @RequestParam int rating,
            @RequestParam String komentar,
            Principal principal) {
        try {
            Customer customer = customerService.findByUsername(principal.getName());
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer tidak ditemukan.");
            }
            bookingService.addReview(bookingId, rating, komentar);
            return ResponseEntity.ok("Review berhasil ditambahkan.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan.");
        }
    }

    /**
     * Endpoint untuk mengambil semua review berdasarkan ID Lapangan.
     *
     * @param lapanganId ID Lapangan
     * @return ResponseEntity dengan list ReviewDTO atau pesan error
     */
    @GetMapping("/reviews/{lapanganId}")
    public ResponseEntity<?> getReviewsByLapangan(@PathVariable Long lapanganId) {
        try {
            List<Review> reviews = reviewService.getReviewsByLapangan(lapanganId);
            List<ReviewDTO> reviewDTOs = reviews.stream().map(review -> {
                ReviewDTO dto = new ReviewDTO();
                dto.setRating(review.getRating());
                dto.setKomentar(review.getKomentar());
                dto.setUsername(review.getBooking().getCustomer().getUsername()); // Sesuaikan dengan relasi Anda
                dto.setTanggalReview(review.getTanggalReview());
                return dto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(reviewDTOs);
        } catch (Exception e) {
            // Pastikan logger sudah diinisialisasi
            // logger.error("Gagal mengambil reviews untuk Lapangan ID {}: {}", lapanganId, e.getMessage());
            return ResponseEntity.status(500).body("Terjadi kesalahan.");
        }
    }

    /**
     * Endpoint untuk mengambil promo yang tersedia bagi customer yang sedang login.
     *
     * @param principal Principal pengguna yang sedang login
     * @return ResponseEntity dengan list PromoDTO atau pesan error
     */
    @GetMapping("/promos/available")
    public ResponseEntity<?> getAvailablePromos(Principal principal) {
        if (principal == null || principal.getName() == null) {
            logger.warn("Pengguna tidak terautentikasi.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
        }

        Customer customer = customerService.findByUsername(principal.getName());
        if (customer == null) {
            logger.warn("Customer tidak ditemukan untuk username: {}", principal.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Customer tidak ditemukan.");
        }

        List<Promo> availablePromos = promoService.getAvailablePromosForCustomer(customer);
        List<PromoDTO> promoDTOs = availablePromos.stream().map(promo -> {
            PromoDTO dto = new PromoDTO();
            dto.setId(promo.getId());
            dto.setKodePromo(promo.getKodePromo());
            dto.setDiskonPersen(promo.getDiskonPersen());
            dto.setTanggalMulai(promo.getTanggalMulai());
            dto.setTanggalSelesai(promo.getTanggalSelesai());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(promoDTOs);
    }

}