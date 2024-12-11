package com.lapangin.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lapangin.web.model.Review;
import com.lapangin.web.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByLapangan(int lapanganID) {
        return reviewRepository.findByLapanganId(lapanganID);
    }
}
