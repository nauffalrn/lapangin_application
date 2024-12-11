package com.lapangin.web.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lapangin.web.model.Booking;
import com.lapangin.web.repository.BookingRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking getBookingById(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.orElse(null); // Return null jika tidak ditemukan
    }

    public void saveBooking(Booking booking) {
        bookingRepository.save(booking);
    }
}
