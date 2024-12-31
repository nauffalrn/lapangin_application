package com.lapangin.web.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lapangin.web.model.Promo;

public interface PromoRepository extends JpaRepository<Promo, Long> {
    Optional<Promo> findByKodePromo(String kodePromo);

    @Query("SELECT p FROM Promo p WHERE p.tanggalMulai BETWEEN :startDate AND :endDate")
    List<Promo> findByTanggalMulaiBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Promo p JOIN p.customersClaimed c WHERE c.id = :customerId AND p.tanggalMulai <= :today AND p.tanggalSelesai >= :today")
    List<Promo> findActivePromosByCustomer(@Param("customerId") Long customerId, @Param("today") LocalDate today);

    // Tambahkan metode berikut untuk mengambil promo aktif yang belum diklaim oleh customer
    @Query("SELECT p FROM Promo p WHERE p.tanggalMulai <= :today AND p.tanggalSelesai >= :today AND p NOT IN " +
           "(SELECT pr FROM Promo pr JOIN pr.customersClaimed cc WHERE cc.id = :customerId)")
    List<Promo> findActivePromosNotClaimedByCustomer(@Param("customerId") Long customerId, @Param("today") LocalDate today);
}