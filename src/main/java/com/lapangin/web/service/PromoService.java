package com.lapangin.web.service;

import com.lapangin.web.model.Promo;
import com.lapangin.web.repository.PromoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PromoService {

    @Autowired
    private PromoRepository promoRepository;

    /**
     * Mengambil semua promo yang aktif di antara tanggal tertentu.
     * @param startDate tanggal mulai (awal range)
     * @param endDate   tanggal akhir (akhir range)
     * @return Daftar promo yang aktif
     */
    public List<Promo> getActivePromos(LocalDate startDate, LocalDate endDate) {
        return promoRepository.findByTanggalMulaiBetween(startDate, endDate);
    }
}
