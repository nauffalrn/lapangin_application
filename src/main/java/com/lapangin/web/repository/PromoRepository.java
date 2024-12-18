package com.lapangin.web.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lapangin.web.model.Promo;

public interface PromoRepository extends JpaRepository<Promo, Long> {
    Optional<Promo> findByKodePromo(String kodePromo);

    @Query("SELECT p FROM Promo p WHERE p.tanggalMulai BETWEEN :startDate AND :endDate")
    List<Promo> findByTanggalMulaiBetween(LocalDate startDate, LocalDate endDate);
}