package com.lapangin.web.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lapangin.web.model.Lapangan;
import com.lapangin.web.repository.LapanganRepository;

@Service
public class LapanganService {

    @Autowired
    private LapanganRepository lapanganRepository;

    // Path direktori tempat gambar disimpan
    private final String imageDirectory = "src/main/resources/static/images/";

    public List<Lapangan> searchLapangan(String keyword) {
        return lapanganRepository.findByNamaLapanganContainingIgnoreCase(keyword);
    }

    public Lapangan getLapanganById(Long id) {
        return lapanganRepository.findById(id).orElse(null);
    }

    public List<Lapangan> getAllLapangan() {
        return lapanganRepository.findAll();
    }

    public void saveLapangan(Lapangan lapangan) {
        lapanganRepository.save(lapangan);
    }

    public void deleteLapangan(Long id) {
        Lapangan lapangan = lapanganRepository.findById(id).orElse(null);
        if (lapangan != null) {
            // Path lengkap ke file gambar
            String imagePath = imageDirectory + lapangan.getImage();
            Path path = Paths.get(imagePath);
            try {
                if (Files.exists(path)) {
                    Files.delete(path);
                }
            } catch (IOException e) {
                // Anda dapat menambahkan logging di sini
                throw new RuntimeException("Error deleting image file: " + e.getMessage());
            }

            // Hapus entitas Lapangan dari database
            lapanganRepository.deleteById(id);
        } else {
            throw new RuntimeException("Lapangan dengan ID " + id + " tidak ditemukan.");
        }
    }

    @Transactional(readOnly = true)
    public long countLapangan() {
        return lapanganRepository.count();
    }

    public static class JadwalResponse {
        private String jam;
        private boolean tersedia;
        private int harga;

        public JadwalResponse(String jam, boolean tersedia, int harga) {
            this.jam = jam;
            this.tersedia = tersedia;
            this.harga = harga;
        }

        // Getters & Setters
        public String getJam() {
            return jam;
        }

        public void setJam(String jam) {
            this.jam = jam;
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

    public List<JadwalResponse> getJadwalTersedia(Long lapanganId, String tanggal) {
        Lapangan lapangan = getLapanganById(lapanganId);
        if (lapangan == null) {
            throw new RuntimeException("Lapangan tidak ditemukan");
        }

        List<JadwalResponse> jadwalList = new ArrayList<>();
        
        LocalTime jamBuka = lapangan.getJamBuka();
        LocalTime jamTutup = lapangan.getJamTutup();
        
        // Generate slot per jam
        LocalTime currentTime = jamBuka;
        while (currentTime.isBefore(jamTutup)) {
            LocalTime jamSelesai = currentTime.plusHours(1);
            
            boolean tersedia = checkJadwalTersedia(lapanganId, tanggal, currentTime);
            
            jadwalList.add(new JadwalResponse(
                currentTime.toString(),
                tersedia,
                lapangan.getPrice()
            ));
            
            currentTime = jamSelesai;
        }
        
        return jadwalList;
    }

    private boolean checkJadwalTersedia(Long lapanganId, String tanggal, LocalTime jam) {
        // TODO: Implement actual booking check from database
        return true;
    }
}