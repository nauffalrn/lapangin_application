package com.lapangin.web.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lapangin.web.model.Booking;
import com.lapangin.web.model.Promo;
import com.lapangin.web.model.Customer;
import com.lapangin.web.model.Lapangan;
import com.lapangin.web.repository.BookingRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final LapanganService lapanganService; // Tambahkan ini

    public BookingService(BookingRepository bookingRepository, LapanganService lapanganService) {
        this.bookingRepository = bookingRepository;
        this.lapanganService = lapanganService;
    }

    public Booking getBookingById(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.orElse(null); // Tetap
    }

    public void saveBooking(Booking booking) {
        bookingRepository.save(booking);
    }

    @Transactional
    public Booking createBooking(Customer customer, Long lapanganId, Promo promo) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        
        // Ambil objek Lapangan berdasarkan lapanganId
        Lapangan lapangan = lapanganService.getLapanganById(lapanganId);
        if (lapangan == null) {
            throw new RuntimeException("Lapangan tidak ditemukan dengan ID: " + lapanganId);
        }
        booking.setLapangan(lapangan); // Set objek Lapangan
        
        booking.setPromo(promo);
        booking.setBookingDate(LocalDateTime.now());
        booking.setDuration(1); // Sesuaikan dengan logika bisnis
        booking.setTotalPrice(calculateTotalPrice(promo));

        return bookingRepository.save(booking);
    }

    private double calculateTotalPrice(Promo promo) {
        double basePrice = 100000; // Contoh harga dasar, sesuaikan dengan logika bisnis
        if (promo != null) {
            return basePrice - (basePrice * promo.getDiskonPersen() / 100);
        }
        return basePrice;
    }
}