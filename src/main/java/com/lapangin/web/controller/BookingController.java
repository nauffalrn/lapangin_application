package com.lapangin.web.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lapangin.web.model.Booking;
import com.lapangin.web.model.Customer;
import com.lapangin.web.model.Promo;
import com.lapangin.web.service.BookingService;
import com.lapangin.web.service.CustomerService;
import com.lapangin.web.service.PromoService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    private final CustomerService customerService;
    private final PromoService promoService;

    // Constructor Injection yang telah diperbarui
    public BookingController(BookingService bookingService,
                             CustomerService customerService,
                             PromoService promoService) {
        this.bookingService = bookingService;
        this.customerService = customerService;
        this.promoService = promoService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request, Principal principal) {
        Customer customer = customerService.findByUsername(principal.getName());
        Promo promo = null;

        if (request.getKodePromo() != null && !request.getKodePromo().isEmpty()) {
            promo = promoService.findByKodePromo(request.getKodePromo());

            if (promo == null) {
                return ResponseEntity.badRequest().body(new Response(false, "Kode promo tidak valid."));
            }

            // Memeriksa apakah promo sudah diklaim oleh customer
            if (!promoService.isPromoClaimedByCustomer(customer, promo)) {
                return ResponseEntity.badRequest().body(new Response(false, "Promo belum diklaim oleh customer."));
            }

            // Memeriksa apakah promo masih berlaku
            if (!promoService.isPromoValid(promo)) {
                return ResponseEntity.badRequest().body(new Response(false, "Promo sudah tidak berlaku."));
            }
        }

        Booking booking = bookingService.createBooking(customer, request.getLapanganId(), promo);
        return ResponseEntity.ok(new Response(true, promo != null ? "Booking berhasil dengan promo." : "Booking berhasil tanpa promo."));
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
}