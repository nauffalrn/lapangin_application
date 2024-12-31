package com.lapangin.web.dto;

import java.time.LocalDateTime;

public class BookingDTO {
    private Long id;
    private LapanganDTO lapangan;
    private LocalDateTime bookingDate;
    private int jamMulai;
    private int jamSelesai;
    private double totalPrice;
    private String paymentProofFilename;
    private ReviewDTO review; // Tambahkan field review

    // Getters dan Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LapanganDTO getLapangan() {
        return lapangan;
    }
    public void setLapangan(LapanganDTO lapangan) {
        this.lapangan = lapangan;
    }
    public LocalDateTime getBookingDate() {
        return bookingDate;
    }
    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }
    public int getJamMulai() {
        return jamMulai;
    }
    public void setJamMulai(int jamMulai) {
        this.jamMulai = jamMulai;
    }
    public int getJamSelesai() {
        return jamSelesai;
    }
    public void setJamSelesai(int jamSelesai) {
        this.jamSelesai = jamSelesai;
    }
    public double getTotalPrice() {
        return totalPrice;
    }   
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public String getPaymentProofFilename() {
        return paymentProofFilename;
    }
    public void setPaymentProofFilename(String paymentProofFilename) {
        this.paymentProofFilename = paymentProofFilename;
    }   

    public ReviewDTO getReview() {
        return review;
    }

    public void setReview(ReviewDTO review) {
        this.review = review;
    }

    // Tambahkan metode untuk mendapatkan format waktu
    public String getJamMulaiFormatted() {
        return String.format("%02d:00", jamMulai);
    }

    public String getJamSelesaiFormatted() {
        return String.format("%02d:00", jamSelesai);
    }
}