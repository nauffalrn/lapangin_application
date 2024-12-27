package com.lapangin.web.dto;

public class JadwalResponse {
    private String waktu;
    private boolean available;
    private int harga;

    // Konstruktor
    public JadwalResponse() {}

    public JadwalResponse(String waktu, boolean available, int harga) {
        this.waktu = waktu;
        this.available = available;
        this.harga = harga;
    }

    // Getter dan Setter
    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }
}