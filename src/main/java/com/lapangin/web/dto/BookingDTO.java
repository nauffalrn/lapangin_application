package com.lapangin.web.dto;

import java.time.LocalDateTime;

public class BookingDTO {
    private Long id;
    private String lapangan;
    private LocalDateTime bookingDate;
    private int jamMulai;
    private int jamSelesai;
    private double totalPrice;
    private String paymentProofFilename;

    // Getters dan Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getLapangan() {
        return lapangan;
    }
    public void setLapangan(String lapangan) {
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

    // Tambahkan metode untuk mendapatkan format waktu
    public String getJamMulaiFormatted() {
        return String.format("%02d:00", jamMulai);
    }

    public String getJamSelesaiFormatted() {
        return String.format("%02d:00", jamSelesai);
    }
}