package com.lapangin.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lapangin.web.model.Pesanan;
import com.lapangin.web.service.PesananService;

@RestController
@RequestMapping("/api/pesanan")
public class PesananController {

    private final PesananService pesananService;

    public PesananController(PesananService pesananService) {
        this.pesananService = pesananService;
    }

    @PostMapping("/book")
    public Pesanan bookLapangan(@RequestBody Pesanan pesanan) {
        return pesananService.bookLapangan(pesanan);
    }

    @GetMapping("/{pesananID}/totalPrice")
    public double calculateTotalPrice(@PathVariable Long pesananID) { // Ubah tipe parameter dari int menjadi Long
        Pesanan pesanan = pesananService.getPesananById(pesananID);
        return pesananService.calculateTotalPrice(pesanan);
    }
}
