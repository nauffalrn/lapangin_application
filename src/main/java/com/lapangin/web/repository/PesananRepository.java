package com.lapangin.web.repository;

import com.lapangin.web.model.Pesanan;
import com.lapangin.web.model.Lapangan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PesananRepository extends JpaRepository<Pesanan, Long> {
    boolean existsByLapanganAndJamMulaiLessThanEqualAndJamSelesaiGreaterThanEqual(
            Lapangan lapangan, int jamMulai, int jamSelesai);
}
