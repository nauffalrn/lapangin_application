package com.lapangin.web.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "review") // Sesuaikan dengan nama tabel di database Anda
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewID; // ID review sebagai kunci utama

    @ManyToOne
    @JoinColumn(name = "lapangan_id", nullable = false)
    private Lapangan lapangan; // Relasi ke Lapangan

    @ManyToOne
    @JoinColumn(name = "pesananID", nullable = false)
    private Pesanan pesanan; // Relasi ke Pesanan

    @Column(nullable = false)
    private int rating; // Nilai rating

    @Column
    private String komentar; // Komentar dari pengguna

    @Temporal(TemporalType.DATE)
    @Column(name = "tanggal_review", nullable = false)
    private Date tanggalReview; // Tanggal review

    // Getter dan Setter untuk ReviewID
    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    // Getter dan Setter untuk Lapangan
    public Lapangan getLapangan() {
        return lapangan;
    }

    public void setLapangan(Lapangan lapangan) {
        this.lapangan = lapangan;
    }

    // Getter dan Setter untuk Pesanan
    public Pesanan getPesanan() {
        return pesanan;
    }

    public void setPesanan(Pesanan pesanan) {
        this.pesanan = pesanan;
    }

    // Getter dan Setter untuk Rating
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    // Getter dan Setter untuk Komentar
    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    // Getter dan Setter untuk TanggalReview
    public Date getTanggalReview() {
        return tanggalReview;
    }

    public void setTanggalReview(Date tanggalReview) {
        this.tanggalReview = tanggalReview;
    }
}