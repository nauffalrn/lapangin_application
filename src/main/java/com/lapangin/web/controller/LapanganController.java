package com.lapangin.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lapangin.web.model.Lapangan;
import com.lapangin.web.service.LapanganService;



@RestController
@RequestMapping("/api/lapangan")
public class LapanganController {

    private final LapanganService lapanganService;

    public LapanganController(LapanganService lapanganService) {
        this.lapanganService = lapanganService;
    }

    // Mendapatkan semua Lapangan
    @GetMapping
    public ResponseEntity<List<Lapangan>> getAllLapangan() {
        List<Lapangan> lapanganList = lapanganService.getAllLapangan();
        return ResponseEntity.ok(lapanganList);
    }

    // Mendapatkan Lapangan berdasarkan ID
    @GetMapping("/{lapanganID}")
    public ResponseEntity<Lapangan> getLapanganById(@PathVariable Long lapanganID) {
        Lapangan lapangan = lapanganService.getLapanganById(lapanganID);
        if (lapangan != null) {
            return ResponseEntity.ok(lapangan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Membuat Lapangan baru
    @PostMapping
    public ResponseEntity<Lapangan> createLapangan(@RequestBody Lapangan lapangan) {
        Lapangan savedLapangan = lapanganService.saveLapangan(lapangan);
        return ResponseEntity.ok(savedLapangan);
    }

    // Memperbarui Lapangan yang ada
    @PutMapping("/{lapanganID}")
    public ResponseEntity<Lapangan> updateLapangan(@PathVariable Long lapanganID, @RequestBody Lapangan lapanganDetails) {
        Lapangan existingLapangan = lapanganService.getLapanganById(lapanganID);
        if (existingLapangan != null) {
            existingLapangan.setNamaLapangan(lapanganDetails.getNamaLapangan());
            existingLapangan.setCity(lapanganDetails.getCity());
            existingLapangan.setImage(lapanganDetails.getImage());
            existingLapangan.setPrice(lapanganDetails.getPrice());
            existingLapangan.setRating(lapanganDetails.getRating());
            existingLapangan.setReviews(lapanganDetails.getReviews());

            Lapangan updatedLapangan = lapanganService.saveLapangan(existingLapangan);
            return ResponseEntity.ok(updatedLapangan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Menghapus Lapangan berdasarkan ID
    @DeleteMapping("/{lapanganID}")
    public ResponseEntity<Void> deleteLapangan(@PathVariable Long lapanganID) {
        lapanganService.deleteLapanganById(lapanganID);
        return ResponseEntity.noContent().build();
    }
}
