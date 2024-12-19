package com.lapangin.web.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lapangin.web.model.Lapangan;
import com.lapangin.web.repository.LapanganRepository;

@Service
public class LapanganService {

    private final LapanganRepository lapanganRepository;

    public LapanganService(LapanganRepository lapanganRepository) {
        this.lapanganRepository = lapanganRepository;
    }

    public List<Lapangan> searchLapangan(String keyword) {
        String trimmedKeyword = keyword == null ? "" : keyword.trim();
        if (trimmedKeyword.isEmpty()) {
            return Collections.emptyList();
        }
        return lapanganRepository.findByNamaLapanganContainingIgnoreCase(trimmedKeyword);
    }

    public Lapangan getLapanganById(Long lapanganID) {
        return lapanganRepository.findById(lapanganID).orElse(null);
    }

    public List<Lapangan> getAllLapangan() {
        return lapanganRepository.findAll();
    }

    public Lapangan saveLapangan(Lapangan lapangan) {
        return lapanganRepository.save(lapangan);
    }

    public void deleteLapanganById(Long id) {
        lapanganRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countLapangan() {
        return lapanganRepository.count();
    }
}