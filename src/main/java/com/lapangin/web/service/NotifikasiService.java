package com.lapangin.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lapangin.web.model.Notifikasi;
import com.lapangin.web.repository.NotifikasiRepository;

@Service
public class NotifikasiService {

    private final NotifikasiRepository notifikasiRepository;

    public NotifikasiService(NotifikasiRepository notifikasiRepository) {
        this.notifikasiRepository = notifikasiRepository;
    }

    public Notifikasi sendNotification(Notifikasi notifikasi) {
        notifikasi.setStatusTerkirim(true);
        return notifikasiRepository.save(notifikasi);
    }

    public List<Notifikasi> getPendingNotifications() {
        return notifikasiRepository.findByStatusTerkirim(false);
    }
}
