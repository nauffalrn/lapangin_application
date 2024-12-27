package com.lapangin.web.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.lapangin.web.dto.BookingRequest;
import com.lapangin.web.dto.BookingResponse;
import com.lapangin.web.dto.JadwalResponse;
import com.lapangin.web.dto.LapanganResponse;
import com.lapangin.web.dto.PromoRequest;
import com.lapangin.web.dto.Response;
import com.lapangin.web.model.Booking;
import com.lapangin.web.model.Customer;
import com.lapangin.web.model.Lapangan;
import com.lapangin.web.model.Promo;
import com.lapangin.web.service.BookingService;
import com.lapangin.web.service.CustomerService;
import com.lapangin.web.service.LapanganService;
import com.lapangin.web.service.PromoService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final LapanganService lapanganService;
    private final BookingService bookingService;
    private final CustomerService customerService;
    private final PromoService promoService;

    private static final String UPLOAD_DIR = "src/main/resources/static/payment-proofs/";

    public BookingController(LapanganService lapanganService,
                             BookingService bookingService,
                             CustomerService customerService,
                             PromoService promoService) {
        this.lapanganService = lapanganService;
        this.bookingService = bookingService;
        this.customerService = customerService;
        this.promoService = promoService;
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createBooking(@RequestBody BookingRequest bookingRequest, Principal principal) {
        try {
            Customer customer = customerService.findByUsername(principal.getName());
            if (customer == null) {
                logger.warn("Customer tidak ditemukan untuk username: {}", principal.getName());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(false, "Customer tidak ditemukan."));
            }

            String kodePromo = bookingRequest.getKodePromo();
            Promo promo = null;

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

            Booking booking = bookingService.createBooking(
                customer,
                bookingRequest.getLapanganId(),
                bookingRequest.getTanggal(),
                bookingRequest.getJadwalList(),
                kodePromo
            );

            return ResponseEntity.ok(new BookingResponse(true, "Booking berhasil dibuat.", booking.getId()));
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

        try {
            promoService.claimPromo(customer, promo);
            logger.info("Promo '{}' berhasil diklaim oleh customer '{}'", promo.getKodePromo(), customer.getUsername());
            return ResponseEntity.ok(new Response(true, "Promo berhasil diklaim."));
        } catch (Exception e) {
            logger.error("Gagal mengklaim promo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Gagal mengklaim promo."));
        }
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
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(new Response(false, "File tidak boleh kosong."));
            }

            // Memastikan pengguna memiliki hak akses untuk booking ini
            Customer customer = customerService.findByUsername(principal.getName());
            Booking booking = bookingService.getBookingById(bookingId);
            if (booking == null || !booking.getCustomer().getId().equals(customer.getId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(false, "Anda tidak berhak mengupload bukti untuk booking ini."));
            }

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
    public ResponseEntity<List<Booking>> getBookings(
            @RequestParam("tanggal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
            Principal principal) {
        try {
            Customer customer = customerService.findByUsername(principal.getName());
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.emptyList());
            }

            List<Booking> bookings = bookingService.getBookingsByCustomerAndDate(customer, tanggal);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            logger.error("Error fetching bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

}