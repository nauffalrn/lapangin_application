package com.lapangin.web.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "promo") // Sesuaikan dengan nama tabel di database Anda
public class Promo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID promo sebagai kunci utama

    @Column(name = "kode_promo", nullable = false)
    private String kodePromo; // Kode promo unik

    @Column(name = "diskon_persen", nullable = false)
    private double diskonPersen; // Persen diskon yang diberikan

    @Column(name = "tanggal_mulai", nullable = false)
    private LocalDate tanggalMulai; // Tanggal mulai promo

    @Column(name = "tanggal_selesai", nullable = false)
    private LocalDate tanggalSelesai; // Tanggal selesai promo

    // Getter dan Setter untuk Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter dan Setter untuk KodePromo
    public String getKodePromo() {
        return kodePromo;
    }

    public void setKodePromo(String kodePromo) {
        this.kodePromo = kodePromo;
    }

    // Getter dan Setter untuk DiskonPersen
    public double getDiskonPersen() {
        return diskonPersen;
    }

    public void setDiskonPersen(double diskonPersen) {
        this.diskonPersen = diskonPersen;
    }

    // Getter dan Setter untuk TanggalMulai
    public LocalDate getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(LocalDate tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    // Getter dan Setter untuk TanggalSelesai
    public LocalDate getTanggalSelesai() {
        return tanggalSelesai;
    }

    public void setTanggalSelesai(LocalDate tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }
}