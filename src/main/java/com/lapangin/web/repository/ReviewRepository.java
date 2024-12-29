package com.lapangin.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lapangin.web.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    // Query untuk mencari review berdasarkan lapangan
    List<Review> findByLapanganId(int lapanganId);

    List<Review> findByBookingId(int bookingId);
}
