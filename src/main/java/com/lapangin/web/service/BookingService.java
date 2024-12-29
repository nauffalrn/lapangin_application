package com.lapangin.web.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lapangin.web.dto.BookingDTO;
import com.lapangin.web.dto.JadwalRequest;
import com.lapangin.web.dto.JadwalResponse;
import com.lapangin.web.model.Booking;
import com.lapangin.web.model.Customer;
import com.lapangin.web.model.Lapangan;
import com.lapangin.web.model.Promo;
import com.lapangin.web.repository.BookingRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final LapanganService lapanganService;
    private final PromoService promoService;
    private final CustomerService customerService;

    // Constructor Injection
    public BookingService(BookingRepository bookingRepository, 
                          LapanganService lapanganService,
                          PromoService promoService,
                          CustomerService customerService) {
        this.bookingRepository = bookingRepository;
        this.lapanganService = lapanganService;
        this.promoService = promoService;
        this.customerService = customerService;
    }

    public Booking getBookingById(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.orElse(null);
    }

    public void saveBooking(Booking booking) {
        bookingRepository.save(booking);
    }

    @Transactional
    public Booking createBooking(Customer customer, Long lapanganId, LocalDate tanggal, List<JadwalRequest> jadwalList, String kodePromo) {
        Booking booking = new Booking();

        // Set customer
        booking.setCustomer(customer);

        // Set lapangan
        Lapangan lapangan = lapanganService.getLapanganById(lapanganId);
        if (lapangan == null) {
            throw new RuntimeException("Lapangan tidak ditemukan.");
        }
        booking.setLapangan(lapangan);

        // Inisialisasi jamMulai dan jamSelesai
        int jamMulai = Integer.MAX_VALUE;
        int jamSelesai = Integer.MIN_VALUE;
        double totalPrice = 0.0;

        // Validasi jadwal dan hitung jam mulai serta jam selesai
        for (JadwalRequest jadwal : jadwalList) {
            String waktu = jadwal.getWaktu();
            LocalTime jamMulaiTime;
            try {
                jamMulaiTime = LocalTime.parse(waktu);
            } catch (Exception e) {
                throw new RuntimeException("Format waktu salah: " + waktu);
            }

            LocalDateTime jadwalDateTime = LocalDateTime.of(tanggal, jamMulaiTime);
            if (jadwalDateTime.isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Tidak bisa memesan jadwal yang sudah lewat.");
            }

            // Cek ketersediaan
            boolean isAvailable = !bookingRepository.existsByLapanganIdAndBookingDateBetweenAndJamMulai(
                lapanganId, 
                tanggal.atStartOfDay(),
                tanggal.atTime(23, 59),
                jamMulaiTime.getHour()
            );

            if (!isAvailable) {
                throw new RuntimeException("Jadwal " + waktu + " sudah dibooking.");
            }

            // Update jamMulai dan jamSelesai
            int currentJam = jamMulaiTime.getHour();
            if (currentJam < jamMulai) {
                jamMulai = currentJam;
            }
            if (currentJam + 1 > jamSelesai) {
                jamSelesai = currentJam + 1;
            }

            // Jumlahkan total harga
            totalPrice += jadwal.getHarga();
        }

        // Set booking date setelah jamMulai dihitung
        LocalDateTime scheduledDateTime = tanggal.atTime(jamMulai, 0);
        booking.setBookingDate(scheduledDateTime);

        booking.setJamMulai(jamMulai);
        booking.setJamSelesai(jamSelesai);
        booking.setTotalPrice(totalPrice);

        // Terapkan promo jika tersedia
        if (kodePromo != null && !kodePromo.trim().isEmpty()) {
            Promo promo = promoService.findByKodePromo(kodePromo);
            if (promo != null && promoService.isPromoValid(promo) && !promoService.isPromoClaimedByCustomer(customer, promo)) {
                promoService.claimPromo(customer, promo);
                booking.setPromo(promo);
                // Terapkan diskon
                double discount = (booking.getTotalPrice() * promo.getDiskonPersen()) / 100;
                booking.setTotalPrice(booking.getTotalPrice() - discount);
            } else {
                throw new RuntimeException("Promo tidak valid atau sudah digunakan.");
            }
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public void updatePaymentStatus(Long bookingId, String fileName) {
        Booking booking = getBookingById(bookingId);
        if (booking != null) {
            booking.setPaymentProofFilename(fileName);
            bookingRepository.save(booking);
        }
    }

    @Transactional
    public void updatePaymentProof(Long bookingId, MultipartFile file) {
        Booking booking = getBookingById(bookingId);
        if (booking != null) {
            try {
                booking.setPaymentProof(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Gagal mengunggah bukti pembayaran.");
            }
            bookingRepository.save(booking);
        } else {
            throw new RuntimeException("Booking tidak ditemukan.");
        }
    }

    @Transactional
    public boolean cancelBooking(Long bookingId, Customer customer) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan."));

        // Verifikasi bahwa booking milik customer yang sama
        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Anda tidak berhak membatalkan booking ini.");
        }

        // Hapus booking
        bookingRepository.delete(booking);

        // Jika ada entitas terkait atau pembaruan ketersediaan jadwal, tangani di sini

        return true;
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan."));
        bookingRepository.delete(booking);
        // Jika ada entitas terkait atau pembaruan ketersediaan jadwal, tangani di sini
    }

    public List<JadwalResponse> getJadwalLapangan(Long lapanganId, LocalDate tanggal) {
        LocalDateTime startOfDay = tanggal.atStartOfDay();
        LocalDateTime endOfDay = tanggal.atTime(23, 59, 59);

        List<Booking> bookings = bookingRepository.findByLapanganIdAndBookingDateBetween(lapanganId, startOfDay, endOfDay);

        // Asumsikan jam operasional lapangan adalah 08:00 - 22:00
        List<Integer> jamOperasional = IntStream.rangeClosed(8, 22).boxed().collect(Collectors.toList());

        // Tentukan jam yang sudah dibooking
        Set<Integer> jamBooked = bookings.stream()
                                         .map(Booking::getJamMulai)
                                         .collect(Collectors.toSet());

        // Ambil data lapangan
        Lapangan lapangan = lapanganService.getLapanganById(lapanganId);
        if (lapangan == null) {
            throw new RuntimeException("Lapangan tidak ditemukan");
        }

        int hargaLapangan = lapangan.getPrice(); // Pastikan tipe data sesuai

        List<JadwalResponse> jadwalResponses = jamOperasional.stream()
            .map(jam -> new JadwalResponse(
                    String.format("%02d:00", jam), // Konversi jam ke format String
                    !jamBooked.contains(jam),      // available
                    hargaLapangan    // harga dari lapangan
                ))
            .collect(Collectors.toList());

        return jadwalResponses;
    }

    public List<JadwalResponse> getJadwalTersedia(Long lapanganId, LocalDate tanggal) {
        // Panggil melalui lapanganService
        Lapangan lapangan = lapanganService.getLapanganById(lapanganId);
        if (lapangan == null) {
            throw new RuntimeException("Lapangan tidak ditemukan");
        }

        List<JadwalResponse> jadwalList = new ArrayList<>();

        LocalTime jamBuka = lapangan.getJamBuka();
        LocalTime jamTutup = lapangan.getJamTutup();

        // Generate slot per jam
        LocalTime currentTime = jamBuka;
        while (currentTime.isBefore(jamTutup)) {
            boolean tersedia = !bookingRepository.existsByLapanganIdAndBookingDateBetweenAndJamMulai(
                lapanganId,
                tanggal.atStartOfDay(),
                tanggal.plusDays(1).atStartOfDay(),
                currentTime.getHour()
            );
            jadwalList.add(new JadwalResponse(
                currentTime.toString(),
                tersedia,
                lapangan.getPrice() // Konversi tipe data di sini
            ));

            currentTime = currentTime.plusHours(1);
        }

        return jadwalList;
    }

    // Metode untuk mengambil booking berdasarkan pelanggan dan tanggal
    public List<BookingDTO> getBookingsByCustomerAndDate(Customer customer, LocalDate tanggal) {
        LocalDateTime startOfDay = tanggal.atStartOfDay();
        LocalDateTime endOfDay = tanggal.plusDays(1).atStartOfDay();

        List<Booking> bookings = bookingRepository.findByCustomerAndBookingDateBetween(customer, startOfDay, endOfDay);

        return bookings.stream()
                       .map(this::convertToDTO)
                       .collect(Collectors.toList());
    }

    // Metode untuk mengambil booking mendatang
    public List<Booking> getUpcomingBookings(Customer customer) {
        LocalDateTime now = LocalDateTime.now();
        // Gunakan repository method dengan pengurutan
        return bookingRepository.findByCustomerAndBookingDateGreaterThanOrderByBookingDateAsc(
            customer, 
            now
        );
    }

    // Metode untuk mengonversi Booking ke BookingDTO
    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setLapangan(booking.getLapangan().getNamaLapangan());
        dto.setBookingDate(booking.getBookingDate());
        dto.setJamMulai(booking.getJamMulai());
        dto.setJamSelesai(booking.getJamSelesai());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setPaymentProofFilename(booking.getPaymentProofFilename());
        return dto;
    }
}