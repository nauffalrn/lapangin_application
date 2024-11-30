package com.lapangin.web.repository;

import com.lapangin.web.model.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface PromoRepository extends JpaRepository<Promo, Long> {

    // Menambahkan query untuk mencari promo dalam rentang tanggal
    @Query("SELECT p FROM Promo p WHERE p.tanggalMulai BETWEEN :startDate AND :endDate")
    List<Promo> findByTanggalMulaiBetween(LocalDate startDate, LocalDate endDate);
}
