package com.lapangin.web.service;

import org.springframework.stereotype.Service;

import com.lapangin.web.model.Pesanan;
import com.lapangin.web.model.Promo;
import com.lapangin.web.repository.PesananRepository;
import com.lapangin.web.repository.PromoRepository;

@Service
public class PesananService {

    private final PesananRepository pesananRepository;
    private final PromoRepository promoRepository;

    public PesananService(PesananRepository pesananRepository, PromoRepository promoRepository) {
        this.pesananRepository = pesananRepository;
        this.promoRepository = promoRepository;
    }

    /**
     * Menyimpan data pesanan setelah melakukan validasi.
     * @param pesanan Objek pesanan yang akan disimpan.
     * @return Pesanan yang disimpan.
     */
    public Pesanan bookLapangan(Pesanan pesanan) {
        // Cek apakah pesanan sudah ada di waktu yang sama untuk lapangan yang sama
        if (isPesananExists(pesanan)) {
            throw new RuntimeException("Lapangan sudah dipesan pada waktu yang dipilih.");
        }
        return pesananRepository.save(pesanan);
    }

    /**
     * Menghitung total harga pesanan, termasuk diskon jika ada promo.
     * @param pesanan Objek pesanan.
     * @return Total harga.
     */
    public double calculateTotalPrice(Pesanan pesanan) {
        double basePrice = pesanan.getLapangan().getPrice() * (pesanan.getJamSelesai() - pesanan.getJamMulai());
        Promo promo = pesanan.getPromo();
        return promo != null ? basePrice * (1 - promo.getDiskonPersen() / 100) : basePrice;
    }

    /**
     * Mendapatkan detail pesanan berdasarkan ID.
     * @param pesananID ID pesanan.
     * @return Objek pesanan.
     */
    public Pesanan getPesananById(Long pesananID) {
        return pesananRepository.findById(pesananID)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan dengan ID: " + pesananID));
    }

    /**
     * Memeriksa apakah pesanan sudah ada di waktu yang sama pada lapangan yang sama.
     * @param pesanan Objek pesanan.
     * @return True jika pesanan sudah ada, false jika tidak.
     */
    private boolean isPesananExists(Pesanan pesanan) {
        return pesananRepository.existsByLapanganAndBookingDateAndJamMulai(
                pesanan.getLapangan(),
                pesanan.getBookingDate(),
                pesanan.getJamMulai()
        );
    }

    // Tambahkan metode lain jika diperlukan
}
