package com.lapangin.web.model;

import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lapangan")
public class Lapangan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama_lapangan", nullable = false)
    private String namaLapangan;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "image")
    private String image;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "rating", nullable = false)
    private double rating = 0.0;

    @Column(name = "reviews", nullable = false)
    private int reviews = 0;

    @Column(name = "cabang_olahraga", nullable = false)
    private String cabangOlahraga;

    @Column(name = "alamat_lapangan", nullable = false)
    private String alamatLapangan;

    @Column(name = "facilities", nullable = false)
    private String facilities;

    @Column(name = "jam_buka", nullable = false)
    private LocalTime jamBuka;

    @Column(name = "jam_tutup", nullable = false)
    private LocalTime jamTutup;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamaLapangan() {
        return namaLapangan;
    }

    public void setNamaLapangan(String namaLapangan) {
        this.namaLapangan = namaLapangan;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public String getCabangOlahraga() {
        return cabangOlahraga;
    }

    public void setCabangOlahraga(String cabangOlahraga) {
        this.cabangOlahraga = cabangOlahraga;
    }

    public String getAlamatLapangan() {
        return alamatLapangan;
    }

    public void setAlamatLapangan(String alamatLapangan) {
        this.alamatLapangan = alamatLapangan;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    // Metode untuk mengubah string fasilitas menjadi list
    public List<String> getFieldFacilitiesAsList() {
        if (this.facilities != null && !this.facilities.isEmpty()) {
            return Arrays.asList(this.facilities.split(",\\s*"));
        }
        return Collections.emptyList(); // Mengembalikan daftar kosong alih-alih null
    }

    public String getFieldFacilitiesAsString() {
        return String.join(", ", getFieldFacilitiesAsList());
    }

    public LocalTime getJamBuka() {
        return jamBuka;
    }

    public void setJamBuka(LocalTime jamBuka) {
        this.jamBuka = jamBuka;
    }

    public LocalTime getJamTutup() {
        return jamTutup;
    }

    public void setJamTutup(LocalTime jamTutup) {
        this.jamTutup = jamTutup;
    }

    public String getFormattedPrice() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
        return "Rp. " + formatter.format(this.price);
    }

    public String getFormattedJamBuka() {
    if (this.jamBuka != null) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return this.jamBuka.format(formatter);
    }
    return "-";
    }

    public String getFormattedJamTutup() {
        if (this.jamTutup != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return this.jamTutup.format(formatter);
        }
        return "-";
    }

    public String getFormattedJamOperasional() {
        String buka = getFormattedJamBuka();
        String tutup = getFormattedJamTutup();
        if (!buka.equals("-") && !tutup.equals("-")) {
            return buka + " - " + tutup;
        }
        return "-";
    }

    public void updateRating(double newRating) {
        this.rating = Math.round(((this.rating * this.reviews) + newRating) / (this.reviews + 1) * 10.0) / 10.0;
        this.reviews += 1;
    }
}