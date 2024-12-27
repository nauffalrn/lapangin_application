package com.lapangin.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class JadwalRequest {

    @NotBlank(message = "Waktu tidak boleh kosong.")
    private String waktu;

    @NotNull(message = "Harga tidak boleh kosong.")
    private int harga;

    // Getters and Setters
    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }
}