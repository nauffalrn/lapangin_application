package com.lapangin.web.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifikasi") // Pastikan nama tabel sesuai dengan di database Anda
public class Notifikasi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notifikasi_id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking; // Ganti dari Pesanan ke Booking

    @Column(name = "waktu_notifikasi")
    private Date waktuNotifikasi;

    @Column(name = "status_terkirim")
    private boolean statusTerkirim;

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    // Getters dan Setters

    public int getNotifikasiID() {
        return notifikasi_id;
    }

    public void setNotifikasiID(int notifikasiID) {
        this.notifikasi_id = notifikasiID;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Date getWaktuNotifikasi() {
        return waktuNotifikasi;
    }

    public void setWaktuNotifikasi(Date waktuNotifikasi) {
        this.waktuNotifikasi = waktuNotifikasi;
    }

    public boolean isStatusTerkirim() {
        return statusTerkirim;
    }

    public void setStatusTerkirim(boolean statusTerkirim) {
        this.statusTerkirim = statusTerkirim;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}