package com.lapangin.web.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lapangin.web.dto.JadwalResponse;
import com.lapangin.web.model.Booking;
import com.lapangin.web.model.Lapangan;
import com.lapangin.web.repository.BookingRepository;
import com.lapangin.web.repository.LapanganRepository;

@Service
public class LapanganService {

    private static final Logger logger = LoggerFactory.getLogger(LapanganService.class);

    private final BookingRepository bookingRepository;
    private final LapanganRepository lapanganRepository;

    public LapanganService(BookingRepository bookingRepository, LapanganRepository lapanganRepository) {
        this.bookingRepository = bookingRepository;
        this.lapanganRepository = lapanganRepository;
    }

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

    public List<JadwalResponse> getJadwalTersedia(Long lapanganId, LocalDate tanggal) {
        logger.debug("Mengambil jadwal tersedia untuk lapanganId: {}, tanggal: {}", lapanganId, tanggal);
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
            boolean tersedia = checkJadwalTersedia(lapanganId, tanggal, currentTime);
            jadwalList.add(new JadwalResponse(
                currentTime.toString(),
                tersedia,
                lapangan.getPrice()
            ));

            currentTime = currentTime.plusHours(1);
        }

        return jadwalList;
    }

    private boolean checkJadwalTersedia(Long lapanganId, LocalDate tanggal, LocalTime jam) {
        logger.debug("Memeriksa ketersediaan untuk lapanganId: {}, tanggal: {}, jam: {}", lapanganId, tanggal, jam);
        int jamMulai = jam.getHour();
        LocalDateTime startOfDay = tanggal.atStartOfDay();
        LocalDateTime endOfDay = tanggal.plusDays(1).atStartOfDay();

        List<Booking> existingBookings = bookingRepository.findByLapanganIdAndBookingDateBetweenAndJamMulai(
            lapanganId, startOfDay, endOfDay, jamMulai
        );
        return existingBookings.isEmpty();
    }
}