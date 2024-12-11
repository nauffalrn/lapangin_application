package com.lapangin.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lapangin.web.model.Lapangan;

@Repository
public interface LapanganRepository extends JpaRepository<Lapangan, Long> {
    List<Lapangan> findByNamaLapanganContainingIgnoreCase(String namaLapangan);
}