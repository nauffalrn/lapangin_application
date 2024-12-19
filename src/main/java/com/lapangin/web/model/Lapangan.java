package com.lapangin.web.model;
import java.util.Arrays;
import java.util.List;

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

    @Column(name = "rating")
    private double rating = 0.0;

    @Column(name = "reviews")
    private int reviews = 0;

    @Column(name = "cabang_olahraga", nullable = false)
    private String cabangOlahraga;

    @Column(name = "deskripsi_lapangan")
    private String deskripsiLapangan;

    @Column(name = "facilities")
    private String facilities;

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

    public String getDeskripsiLapangan() {
        return deskripsiLapangan;
    }

    public void setDeskripsiLapangan(String deskripsiLapangan) {
        this.deskripsiLapangan = deskripsiLapangan;
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
        return null;
    }
}