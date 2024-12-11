package com.lapangin.web.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lapangin.web.model.Promo;
import com.lapangin.web.repository.PromoRepository;

@Service
public class PromoService {

    private final PromoRepository promoRepository;

    public PromoService(PromoRepository promoRepository) {
        this.promoRepository = promoRepository;
    }

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
