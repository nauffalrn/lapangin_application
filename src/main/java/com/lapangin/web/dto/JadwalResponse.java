package com.lapangin.web.dto;

public class JadwalResponse {
    private String waktu;
    private boolean tersedia;
    private int harga;

    // Konstruktor
    public JadwalResponse() {}

    public JadwalResponse(String waktu, boolean tersedia, int harga) {
        this.waktu = waktu;
        this.tersedia = tersedia;
        this.harga = harga;
    }

    // Getter dan Setter
    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }
}