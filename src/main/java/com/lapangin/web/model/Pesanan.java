package com.lapangin.web.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pesanan") // Sesuaikan dengan nama tabel di database Anda
public class Pesanan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID pesanan sebagai kunci utama

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer; // Relasi ke customer

    @ManyToOne
    @JoinColumn(name = "lapangan_id", referencedColumnName = "id", nullable = false)
    private Lapangan lapangan; // Relasi ke Lapangan

    @ManyToOne
    @JoinColumn(name = "promo_id", referencedColumnName = "id")
    private Promo promo; // Relasi ke Promo

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate; // Tanggal pemesanan

    @Column(name = "jam_mulai", nullable = false)
    private int jamMulai; // Jam mulai

    @Column(name = "jam_selesai", nullable = false)
    private int jamSelesai; // Jam selesai

    // Getters dan Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Lapangan getLapangan() {
        return lapangan;
    }

    public void setLapangan(Lapangan lapangan) {
        this.lapangan = lapangan;
    }

    public Promo getPromo() {
        return promo;
    }

    public void setPromo(Promo promo) {
        this.promo = promo;
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
}