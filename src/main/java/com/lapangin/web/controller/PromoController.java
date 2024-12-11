package com.lapangin.web.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lapangin.web.model.Promo;
import com.lapangin.web.service.PromoService;

@RestController
public class PromoController {

    private final PromoService promoService;

    public PromoController(PromoService promoService) {
        this.promoService = promoService;
    }

    @GetMapping("/active-promos")
    public List<Promo> getActivePromos(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return promoService.getActivePromos(startDate, endDate);  // Mengambil promo yang aktif
    }
}
