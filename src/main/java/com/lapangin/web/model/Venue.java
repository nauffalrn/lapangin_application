package com.lapangin.web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Objects;

@Entity
@Table(name = "venue") // Sesuaikan dengan nama tabel di database Anda
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID venue sebagai kunci utama

    @NotNull
    @Column(nullable = false)
    private String name; // Nama venue

    @NotNull
    @Column(nullable = false)
    private String city; // Kota tempat venue berada

    @NotNull
    @Column(nullable = false)
    private String image; // Nama file gambar

    @Positive
    @Column(nullable = false)
    private double price; // Harga per jam

    @Positive
    @Column(nullable = false)
    private double rating; // Rating venue

    @Positive
    @Column(nullable = false)
    private int reviews; // Jumlah ulasan

    // Getter dan Setter untuk ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter dan Setter untuk Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter dan Setter untuk City
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Getter dan Setter untuk Image
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // Getter dan Setter untuk Price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getter dan Setter untuk Rating
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    // Getter dan Setter untuk Reviews
    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venue)) return false;
        Venue venue = (Venue) o;
        return Objects.equals(id, venue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Venue{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", rating=" + rating +
                ", reviews=" + reviews +
                '}';
    }
}