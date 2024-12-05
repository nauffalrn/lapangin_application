package com.lapangin.web.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notifikasi") // Pastikan nama tabel sesuai dengan di database Anda
public class Notifikasi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notifikasi_id; // ID notifikasi sebagai kunci utama

    @OneToOne
    @JoinColumn(name = "pesananID", nullable = false)
    private Pesanan pesanan; // Relasi satu-satu dengan Pesanan

    @Column(name = "waktu_notifikasi")
    private Date waktuNotifikasi; // Waktu notifikasi

    @Column(name = "status_terkirim")
    private boolean statusTerkirim; // Status pengiriman notifikasi

    // Metode Setter untuk statusTerkirim
    public void setStatusTerkirim(boolean statusTerkirim) {
        this.statusTerkirim = statusTerkirim;
    }

    // Getter dan Setter

    public int getNotifikasiID() {
        return notifikasi_id;
    }

    public void setNotifikasiID(int notifikasiID) {
        this.notifikasi_id = notifikasiID;
    }

    public Pesanan getPesanan() {
        return pesanan;
    }

    public void setPesanan(Pesanan pesanan) {
        this.pesanan = pesanan;
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
}