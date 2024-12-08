package com.lapangin.web.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id") // Sesuaikan dengan nama kolom di database
    private int reviewID;

    @ManyToOne
    @JoinColumn(name = "lapangan_id", nullable = false) // Sesuaikan dengan nama kolom di database
    private Lapangan lapangan;

    @ManyToOne
    @JoinColumn(name = "pesanan_id", nullable = false) // Perbaiki nama kolom
    private Pesanan pesanan;

    @Column(nullable = false)
    private int rating;

    @Column
    private String komentar;

    @Temporal(TemporalType.DATE)
    @Column(name = "tanggal_review", nullable = false) // Sesuaikan dengan nama kolom di database
    private Date tanggalReview;


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