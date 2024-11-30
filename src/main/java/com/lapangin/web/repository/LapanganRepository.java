package com.lapangin.web.repository;

import com.lapangin.web.model.Lapangan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface LapanganRepository extends JpaRepository<Lapangan, Integer> {
    List<Lapangan> findByNameContainingIgnoreCase(String name);
}