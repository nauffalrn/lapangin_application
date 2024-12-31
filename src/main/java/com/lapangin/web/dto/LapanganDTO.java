package com.lapangin.web.dto;

public class LapanganDTO {
    private Long id;
    private String namaLapangan;
    private String alamatLapangan;

    // Getters dan Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getNamaLapangan(){
        return namaLapangan;
    }

    public void setNamaLapangan(String namaLapangan){
        this.namaLapangan = namaLapangan;
    }

    public String getAlamatLapangan(){
        return alamatLapangan;
    }

    public void setAlamatLapangan(String alamatLapangan){
        this.alamatLapangan = alamatLapangan;
    }
}