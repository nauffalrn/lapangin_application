package com.lapangin.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lapangin.web.model.Pesanan;
import com.lapangin.web.model.Promo;
import com.lapangin.web.repository.PesananRepository;
import com.lapangin.web.repository.PromoRepository;

@Service
public class PesananService {

    @Autowired
    private PesananRepository pesananRepository;

    @Autowired
    private PromoRepository promoRepository;

    // Method untuk menyimpan data pesanan
    public Pesanan bookLapangan(Pesanan pesanan) {
        // Cek apakah pesanan sudah ada di waktu yang sama untuk lapangan yang sama
        if (isPesananExists(pesanan)) {
            throw new RuntimeException("Lapangan sudah dipesan pada waktu yang dipilih.");
        }
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
        return pesananRepository.findById((long) pesananID)
                .orElseThrow(() -> new RuntimeException("Pesanan dengan ID " + pesananID + " tidak ditemukan."));
    }

    // Method untuk memeriksa apakah pesanan sudah ada di waktu yang sama pada lapangan yang sama
    private boolean isPesananExists(Pesanan pesanan) {
        return pesananRepository.existsByLapanganAndJamMulaiLessThanEqualAndJamSelesaiGreaterThanEqual(
                pesanan.getLapangan(), pesanan.getJamMulai(), pesanan.getJamSelesai());
    }
}
