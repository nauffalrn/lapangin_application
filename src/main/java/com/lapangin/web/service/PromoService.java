package com.lapangin.web.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lapangin.web.model.Customer;
import com.lapangin.web.model.Pesanan;
import com.lapangin.web.model.Promo;
import com.lapangin.web.repository.CustomerRepository;
import com.lapangin.web.repository.PromoRepository;

@Service
public class PromoService {

    private static final Logger logger = LoggerFactory.getLogger(PromoService.class);
    private final PromoRepository promoRepository;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    public PromoService(PromoRepository promoRepository, CustomerService customerService, CustomerRepository customerRepository) {
        this.promoRepository = promoRepository;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    public List<Promo> getActivePromos(LocalDate startDate, LocalDate endDate) {
        return promoRepository.findByTanggalMulaiBetween(startDate, endDate);
    }

    public Promo findByKodePromo(String kodePromo) {
        return promoRepository.findByKodePromo(kodePromo).orElse(null);
    }

    public boolean isPromoValid(Promo promo) {
        LocalDate today = LocalDate.now();
        return !(promo.getTanggalMulai().isAfter(today) || promo.getTanggalSelesai().isBefore(today));
    }

    public boolean isPromoClaimedByCustomer(Customer customer, Promo promo) {
        return customerService.isPromoClaimedByCustomer(customer, promo);
    }

    @Transactional
    public void claimPromo(Customer customer, Promo promo) {
        if (promo != null) {
            logger.info("Customer '{}' mengklaim promo '{}'", customer.getUsername(), promo.getKodePromo());
            customer.getClaimedPromos().add(promo);
            customerRepository.save(customer);
            logger.info("Promo '{}' berhasil diklaim oleh customer '{}'", promo.getKodePromo(), customer.getUsername());
        } else {
            logger.error("Promo tidak boleh null saat mengklaim promo.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo tidak boleh null");
        }
    }

    public List<Promo> getActivePromosByCustomer(Customer customer) {
        LocalDate today = LocalDate.now();
        return promoRepository.findActivePromosByCustomer(customer.getId(), today);
    }

    public double calculateTotalPrice(Pesanan pesanan) {
        double basePrice = pesanan.getLapangan().getPrice() * (pesanan.getJamSelesai() - pesanan.getJamMulai());
        Promo promo = pesanan.getPromo();
        return promo != null ? basePrice * (1 - promo.getDiskonPersen() / 100) : basePrice;
    }
}