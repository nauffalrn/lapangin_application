package com.lapangin.web.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.lapangin.web.model.Booking;
import com.lapangin.web.model.Customer;
import com.lapangin.web.model.Lapangan;
import com.lapangin.web.model.Promo;
import com.lapangin.web.service.BookingService;
import com.lapangin.web.service.CustomerService;
import com.lapangin.web.service.LapanganService;
import com.lapangin.web.service.LapanganService.JadwalResponse;
import com.lapangin.web.service.PromoService;

@Controller // Change to @Controller
@RequestMapping("/booking")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    private final CustomerService customerService;
    private final PromoService promoService;
    private final LapanganService lapanganService;

    private static final String UPLOAD_DIR = "src/main/resources/static/payment-proofs/";

    // Constructor Injection yang telah diperbarui
    public BookingController(BookingService bookingService,
                             CustomerService customerService,
                             PromoService promoService,
                             LapanganService lapanganService) {
        this.bookingService = bookingService;
        this.customerService = customerService;
        this.promoService = promoService;
        this.lapanganService = lapanganService;
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request, Principal principal) {
        logger.info("Menerima request booking dari user: {}", principal != null ? principal.getName() : "unknown");
        logger.info("Data booking: {}", request);

        try {
            if (principal == null) {
                logger.error("User tidak terautentikasi");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Response(false, "Silakan login terlebih dahulu"));
            }

            Customer customer = customerService.findByUsername(principal.getName());
            if (customer == null) {
                logger.error("Customer tidak ditemukan untuk username: {}", principal.getName());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Response(false, "Data customer tidak ditemukan"));
            }

            // Buat booking
            Booking booking = bookingService.createBooking(customer, request.getLapanganId(), null);
            logger.info("Booking berhasil dibuat dengan ID: {}", booking.getId());
            
            return ResponseEntity.ok(new BookingResponse(true, "Booking berhasil", booking.getId()));

        } catch (Exception e) {
            logger.error("Error saat membuat booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response(false, "Gagal membuat booking: " + e.getMessage()));
        }
    }

// Add this class inside BookingController
public static class BookingResponse extends Response {
    private Long bookingId;
    
    public BookingResponse(boolean success, String message, Long bookingId) {
        super(success, message);
        this.bookingId = bookingId;
    }
    
    public Long getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
}

    @PostMapping("/claim")
    public ResponseEntity<Response> claimPromo(@RequestBody PromoRequest request, Principal principal) {
        logger.info("Menerima permintaan klaim promo: {}", request.getKodePromo());

        if (principal == null || principal.getName() == null) {
            logger.warn("Pengguna tidak terautentikasi.");
            return ResponseEntity.status(401).body(new Response(false, "Unauthorized."));
        }

        Customer customer = customerService.findByUsername(principal.getName());
        if (customer == null) {
            logger.warn("Customer tidak ditemukan untuk username: {}", principal.getName());
            return ResponseEntity.status(401).body(new Response(false, "Customer tidak ditemukan."));
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
            return ResponseEntity.badRequest().body(new Response(false, "Promo sudah tidak berlaku."));
        }

        try {
            promoService.claimPromo(customer, promo);
            logger.info("Promo '{}' berhasil diklaim oleh customer '{}'", promo.getKodePromo(), customer.getUsername());
            return ResponseEntity.ok(new Response(true, "Promo berhasil diklaim."));
        } catch (Exception e) {
            logger.error("Gagal mengklaim promo '{}': {}", promo.getKodePromo(), e.getMessage());
            return ResponseEntity.status(500).body(new Response(false, "Gagal mengklaim promo."));
        }
    }

    @GetMapping("/lapangan/{id}")
    public ResponseEntity<LapanganResponse> getLapanganDetail(@PathVariable Long id) {
        Lapangan lapangan = lapanganService.getLapanganById(id);
        if (lapangan == null) {
            return ResponseEntity.notFound().build();
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
        @RequestParam String tanggal
    ) {
        try {
            List<JadwalResponse> jadwalList = lapanganService.getJadwalTersedia(lapanganId, tanggal);
            return ResponseEntity.ok(jadwalList);
        } catch (RuntimeException e) {
            logger.error("Error getting jadwal: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/payment/{bookingId}")
    public String showPayment(@PathVariable Long bookingId, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            return "redirect:/error";
        }

        // Verify booking belongs to logged in user
        if (!booking.getCustomer().getUsername().equals(principal.getName())) {
            return "redirect:/error";
        }

        model.addAttribute("booking", booking);
        model.addAttribute("noRek", "BCA: 1234567890 a.n LAPANGIN");
        return "payment";
    }

    @PostMapping("/payment/upload")
    @ResponseBody // Add this to return JSON
    public ResponseEntity<?> uploadPaymentProof(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam("file") MultipartFile file) {
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            if (booking == null) {
                return ResponseEntity.notFound().build();
            }

            String fileName = bookingId + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            bookingService.updatePaymentStatus(bookingId, fileName);
            
            return ResponseEntity.ok(new Response(true, "Pembayaran berhasil dikonfirmasi"));
        } catch (Exception e) {
            logger.error("Error uploading payment proof: ", e);
            return ResponseEntity.status(500)
                .body(new Response(false, "Gagal mengupload bukti pembayaran"));
        }
    }

    // DTO Classes
    public static class BookingRequest {
        private Long lapanganId;
        private String kodePromo;

        // Getters dan Setters
        public Long getLapanganId() {
            return lapanganId;
        }

        public void setLapanganId(Long lapanganId) {
            this.lapanganId = lapanganId;
        }

        public String getKodePromo() {
            return kodePromo;
        }

        public void setKodePromo(String kodePromo) {
            this.kodePromo = kodePromo;
        }
    }

    public static class PromoRequest {
        private String kodePromo;

        // Getter
        public String getKodePromo() {
            return kodePromo;
        }

        // Setter
        public void setKodePromo(String kodePromo) {
            this.kodePromo = kodePromo;
        }
    }

    public static class Response {
        private boolean success;
        private String message;

        public Response(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        // Getters dan Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class LapanganResponse {
        private Long id;
        private String namaLapangan; 
        private LocalTime jamBuka;
        private LocalTime jamTutup;
        private int price;
        
        public LapanganResponse(Long id, String namaLapangan, LocalTime jamBuka, LocalTime jamTutup, int price) {
            this.id = id;
            this.namaLapangan = namaLapangan;
            this.jamBuka = jamBuka;
            this.jamTutup = jamTutup;
            this.price = price;
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNamaLapangan() { return namaLapangan; }
        public void setNamaLapangan(String namaLapangan) { this.namaLapangan = namaLapangan; }
        public LocalTime getJamBuka() { return jamBuka; }
        public void setJamBuka(LocalTime jamBuka) { this.jamBuka = jamBuka; }
        public LocalTime getJamTutup() { return jamTutup; }
        public void setJamTutup(LocalTime jamTutup) { this.jamTutup = jamTutup; }
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
    }
}