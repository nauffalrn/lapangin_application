package com.lapangin.web.dto;

import java.time.LocalDateTime;

public class ReviewDTO {
    private int rating;
    private String komentar;
    private String username;
    private LocalDateTime tanggalReview;

    // Getters dan Setters
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getTanggalReview() {
        return tanggalReview;
    }

    public void setTanggalReview(LocalDateTime tanggalReview) {
        this.tanggalReview = tanggalReview;
    }
}