package com.lapangin.web.service;

import com.lapangin.web.model.Lapangan;
import com.lapangin.web.repository.LapanganRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;

import java.util.List;

@Service
public class LapanganService {

    @Autowired
    private LapanganRepository lapanganRepository;

    public List<Lapangan> searchLapangan(String keyword) {
        String trimmedKeyword = keyword == null ? "" : keyword.trim();
        if (trimmedKeyword.isEmpty()) {
            return Collections.emptyList();
        }

        // Asumsikan Anda memiliki metode dalam LapanganRepository untuk pencarian ini
        return lapanganRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Lapangan getLapanganById(int lapanganID) {
        return lapanganRepository.findById(lapanganID).orElse(null);
    }
}
