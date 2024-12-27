package com.lapangin.web.dto;

import java.time.LocalTime;

public class LapanganResponse {
    private Long id;
    private String namaLapangan;
    private LocalTime jamBuka;
    private LocalTime jamTutup;
    private int price;
    
    public LapanganResponse(Long id, String namaLapangan, LocalTime jamBuka, LocalTime jamTutup, int price) {
        this.id = id;
        this.namaLapangan = namaLapangan;
        this.jamBuka = jamBuka;
        this.jamTutup = jamTutup;
        this.price = price;
    }

    // Getters dan Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNamaLapangan() { return namaLapangan; }
    public void setNamaLapangan(String namaLapangan) { this.namaLapangan = namaLapangan; }

    public LocalTime getJamBuka() { return jamBuka; }
    public void setJamBuka(LocalTime jamBuka) { this.jamBuka = jamBuka; }

    public LocalTime getJamTutup() { return jamTutup; }
    public void setJamTutup(LocalTime jamTutup) { this.jamTutup = jamTutup; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}