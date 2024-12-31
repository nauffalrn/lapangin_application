package com.lapangin.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lapangin.web.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Mencari review berdasarkan ID Lapangan melalui Booking.
     *
     * @param lapanganId ID Lapangan
     * @return List of Review
     */
    List<Review> findByBooking_Lapangan_Id(Long lapanganId);

    /**
     * Mencari review berdasarkan ID Booking.
     *
     * @param bookingId ID Booking
     * @return List of Review
     */
    List<Review> findByBooking_Id(Long bookingId);
}
