package com.lapangin.web.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lapangan") // Sesuaikan dengan nama tabel di database Anda
public class Lapangan {

    @Id
    private Long ID; // ID lapangan sebagai kunci utama

    @Column(name = "name", nullable = false)
    private String name; // Nama lapangan

    @Column(name = "harga_per_jam", nullable = false)
    private double hargaPerJam; // Harga sewa per jam

    // Getter dan Setter untuk ID
    public Long getId() {
        return ID;
    }

    public void setId(Long ID) {
        this.ID = ID;
    }

    // Getter dan Setter untuk name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter dan Setter untuk hargaPerJam
    public double getHargaPerJam() {
        return hargaPerJam;
    }

    public void setHargaPerJam(double hargaPerJam) {
        this.hargaPerJam = hargaPerJam;
    }
}