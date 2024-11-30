package com.lapangin.web.controller;

import com.lapangin.web.service.PromoService;
import com.lapangin.web.model.Promo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class PromoController {

    @Autowired
    private PromoService promoService;

    @GetMapping("/active-promos")
    public List<Promo> getActivePromos(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return promoService.getActivePromos(startDate, endDate);  // Mengambil promo yang aktif
    }
}
