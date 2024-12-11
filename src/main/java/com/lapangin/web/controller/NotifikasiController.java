package com.lapangin.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lapangin.web.model.Notifikasi;
import com.lapangin.web.service.NotifikasiService;

@RestController
@RequestMapping("/api/notifikasi")
public class NotifikasiController {

    private final NotifikasiService notifikasiService;

    public NotifikasiController(NotifikasiService notifikasiService) {
        this.notifikasiService = notifikasiService;
    }

    @PostMapping("/send")
    public Notifikasi sendNotification(@RequestBody Notifikasi notifikasi) {
        return notifikasiService.sendNotification(notifikasi);
    }

    @GetMapping("/pending")
    public List<Notifikasi> getPendingNotifications() {
        return notifikasiService.getPendingNotifications();
    }
}
