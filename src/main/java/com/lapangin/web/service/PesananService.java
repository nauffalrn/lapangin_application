package com.lapangin.web.service;

import com.lapangin.web.model.Pesanan;
import com.lapangin.web.model.Promo;
import com.lapangin.web.repository.PesananRepository;
import com.lapangin.web.repository.PromoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PesananService {

    @Autowired
    private PesananRepository pesananRepository;

    @Autowired
    private PromoRepository promoRepository;

    // Method untuk menyimpan data pesanan
    public Pesanan bookLapangan(Pesanan pesanan) {
        return pesananRepository.save(pesanan);
    }

    // Method untuk menghitung total harga pesanan, termasuk diskon jika ada promo
    public double calculateTotalPrice(Pesanan pesanan) {
        double basePrice = pesanan.getLapangan().getHargaPerJam() * (pesanan.getJamSelesai() - pesanan.getJamMulai());
        Promo promo = pesanan.getPromo();
        return promo != null ? basePrice * (1 - promo.getDiskonPersen() / 100) : basePrice;
    }

    // Method untuk mendapatkan detail pesanan berdasarkan ID
    public Pesanan getPesananById(int pesananID) {
        Optional<Pesanan> pesanan = pesananRepository.findById(pesananID);
        if (pesanan.isPresent()) {
            return pesanan.get();
        } else {
            throw new RuntimeException("Pesanan dengan ID " + pesananID + " tidak ditemukan.");
        }
    }
}
