package com.lapangin.web.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookingRequest {

    @NotNull(message = "Tanggal tidak boleh kosong.")
    private LocalDate tanggal;

    @NotNull(message = "Lapangan ID tidak boleh kosong.")
    private Long lapanganId;

    @NotNull(message = "Jadwal list tidak boleh kosong.")
    @Size(min = 1, message = "Minimal satu jadwal harus dipilih.")
    private List<JadwalRequest> jadwalList;

    private String kodePromo;

    // Getters dan Setters

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public Long getLapanganId() {
        return lapanganId;
    }

    public void setLapanganId(Long lapanganId) {
        this.lapanganId = lapanganId;
    }

    public List<JadwalRequest> getJadwalList() {
        return jadwalList;
    }

    public void setJadwalList(List<JadwalRequest> jadwalList) {
        this.jadwalList = jadwalList;
    }

    public String getKodePromo() {
        return kodePromo;
    }

    public void setKodePromo(String kodePromo) {
        this.kodePromo = kodePromo;
    }
}
