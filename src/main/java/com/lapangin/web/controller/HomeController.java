package com.lapangin.web.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lapangin.web.model.Lapangan;
import com.lapangin.web.service.LapanganService;

@Controller
public class HomeController {

    private final LapanganService lapanganService;

    // Constructor Injection
    public HomeController(LapanganService lapanganService) {
        this.lapanganService = lapanganService;
    }

    // Mapping untuk dashboard
    @GetMapping("/dashboard")
    public String showHomePage(Model model) {
        List<Lapangan> listLapangan = lapanganService.getAllLapangan();
        model.addAttribute("listLapangan", listLapangan);
        return "dashboard";
    }

    // Halaman pemesanan lapangan
    @GetMapping("/booking")
    public String showBookingPage(Model model) {
        List<Lapangan> listLapangan = lapanganService.getAllLapangan();
        model.addAttribute("listLapangan", listLapangan);
        return "booking";
    }

    // Proses pemesanan lapangan
    @PostMapping("/booking")
    public String processBooking(@RequestParam String lapangan, @RequestParam String waktu) {
        // Implementasi logika pemesanan menggunakan Lapangan
        // ...
        return "redirect:/dashboard";
    }

    // Halaman riwayat pemesanan
    @GetMapping("/history")
    public String showHistory(Model model) {
        // Implementasi logika untuk menampilkan riwayat pemesanan
        // ...
        return "history";
    }
}
