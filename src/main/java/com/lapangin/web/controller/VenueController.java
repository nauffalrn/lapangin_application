package com.lapangin.web.controller;

import com.lapangin.web.model.Venue;
import com.lapangin.web.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class VenueController {

    @Autowired
    private VenueService venueService;

    @GetMapping("/venues")
    public String listVenues(Model model) {
        model.addAttribute("venues", venueService.getAllVenues());
        return "venues"; // Nama template Thymeleaf
    }

    @GetMapping("/venue/detail/{id}")
    public String venueDetail(@PathVariable Long id, Model model) {
        Optional<Venue> venue = venueService.getVenueById(id);
        if (venue.isPresent()) {
            model.addAttribute("venue", venue.get());
            return "venue-detail"; // Nama template detail
        } else {
            return "404"; // Halaman not found jika ID tidak ditemukan
        }
    }
}