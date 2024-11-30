package com.lapangin.web.repository;

import com.lapangin.web.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    // Query to find reviews by field
    List<Review> findByLapanganId(int lapanganId);

    // Query to find reviews by order
    List<Review> findByPesananId(int pesananId);
}
