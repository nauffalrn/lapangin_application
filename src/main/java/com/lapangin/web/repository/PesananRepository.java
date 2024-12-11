package com.lapangin.web.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lapangin.web.model.Lapangan;
import com.lapangin.web.model.Pesanan;

@Repository
public interface PesananRepository extends JpaRepository<Pesanan, Long> {
    
    /**
     * Memeriksa apakah sudah ada pesanan untuk lapangan tertentu pada tanggal dan jam tertentu.
     *
     * @param lapangan The Lapangan yang ingin dicek.
     * @param bookingDate The tanggal pemesanan.
     * @param jamMulai The jam mulai pemesanan.
     * @return True jika pesanan sudah ada, false jika tidak.
     */
    boolean existsByLapanganAndBookingDateAndJamMulai(Lapangan lapangan, LocalDateTime bookingDate, int jamMulai);
}
