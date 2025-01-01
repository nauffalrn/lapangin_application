package com.lapangin.web.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lapangin.web.model.Booking;
import com.lapangin.web.model.Customer;
import com.lapangin.web.model.Promo;
import com.lapangin.web.repository.CustomerRepository;
import com.lapangin.web.repository.PromoRepository;

@Service
public class PromoService {

    private static final Logger logger = LoggerFactory.getLogger(PromoService.class);
    private final PromoRepository promoRepository;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    @Autowired
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
        return customer.getClaimedPromos().contains(promo);
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

    @Transactional(readOnly = true)
    public List<Promo> getAvailablePromosForCustomer(Customer customer) {
        LocalDate today = LocalDate.now();
        return promoRepository.findActivePromosNotClaimedByCustomer(customer.getId(), today);
    }

    public List<Promo> getPromosByCustomer(Customer customer) {
        return promoRepository.findPromosByCustomer(customer.getId());
    }

    public double calculateTotalPrice(Booking booking) {
        double basePrice = booking.getLapangan().getPrice() * (booking.getJamSelesai() - booking.getJamMulai());
        Promo promo = booking.getPromo();
        return promo != null ? basePrice * (1 - promo.getDiskonPersen() / 100) : basePrice;
    }

    public boolean shouldShowPromo(Customer customer) {
        List<Promo> activePromos = getActivePromosByCustomer(customer);
        return !activePromos.isEmpty() && !isPromoClaimedByCustomer(customer, activePromos.get(0));
    }
}