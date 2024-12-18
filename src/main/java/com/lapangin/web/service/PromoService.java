package com.lapangin.web.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lapangin.web.model.Customer;
import com.lapangin.web.model.Promo;
import com.lapangin.web.repository.PromoRepository;

@Service
public class PromoService {

    private static final Logger logger = LoggerFactory.getLogger(PromoService.class);
    private final PromoRepository promoRepository;
    private final CustomerService customerService;

    public PromoService(PromoRepository promoRepository, CustomerService customerService) {
        this.promoRepository = promoRepository;
        this.customerService = customerService;
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
        logger.info("Mencoba mengklaim promo '{}' untuk customer '{}'", promo.getKodePromo(), customer.getUsername());
        customerService.claimPromo(customer, promo);
        logger.info("Promo '{}' diklaim oleh customer '{}'", promo.getKodePromo(), customer.getUsername());
    }
}