package com.lapangin.web.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username; // Menyimpan username yang melakukan pemesanan

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "lapangan_id", nullable = false)
    private Lapangan lapangan;

    @ManyToOne
    @JoinColumn(name = "promo_id", referencedColumnName = "id")
    private Promo promo;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "jam_mulai", nullable = false)
    private int jamMulai; 

    @Column(name = "jam_selesai", nullable = false)
    private int jamSelesai; 

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    @Lob
    @Column(name = "payment_proof", nullable = true, columnDefinition = "MEDIUMBLOB")
    private byte[] paymentProof;

    @Column(name = "payment_proof_filename", nullable = true)
    private String paymentProofFilename;

    // Constructor default
    public Booking() {}

    // Getters dan Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            this.username = customer.getUsername(); // Otomatis set username dari Customer
        }
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public byte[] getPaymentProof() {
        return paymentProof;
    }

    public void setPaymentProof(byte[] paymentProof) {
        this.paymentProof = paymentProof;
    }

    public String getPaymentProofFilename() {
        return paymentProofFilename;
    }

    public void setPaymentProofFilename(String paymentProofFilename) {
        this.paymentProofFilename = paymentProofFilename;
    }
}
