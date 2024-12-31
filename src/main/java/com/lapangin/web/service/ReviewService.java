package com.lapangin.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lapangin.web.model.Review;
import com.lapangin.web.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Menyimpan review baru.
     *
     * @param review Objek Review
     * @return Review yang disimpan
     */
    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    /**
     * Mengambil semua review berdasarkan ID Lapangan.
     *
     * @param lapanganID ID Lapangan
     * @return List of Review
     */
    public List<Review> getReviewsByLapangan(Long lapanganId) {
        return reviewRepository.findByBooking_Lapangan_Id(lapanganId);
    }

    /**
     * Mengambil semua review berdasarkan ID Booking.
     *
     * @param bookingId ID Booking
     * @return List of Review
     */
    public List<Review> getReviewsByBooking(Long bookingId) {
        return reviewRepository.findByBooking_Id(bookingId);
    }
}
