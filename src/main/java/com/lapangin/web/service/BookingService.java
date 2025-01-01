package com.lapangin.web.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lapangin.web.dto.BookingDTO;
import com.lapangin.web.dto.JadwalRequest;
import com.lapangin.web.dto.JadwalResponse;
import com.lapangin.web.dto.LapanganDTO;
import com.lapangin.web.dto.ReviewDTO;
import com.lapangin.web.model.Booking;
import com.lapangin.web.model.Customer;
import com.lapangin.web.model.Lapangan;
import com.lapangin.web.model.Promo;
import com.lapangin.web.model.Review;
import com.lapangin.web.repository.BookingRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PromoService promoService;
    private final LapanganService lapanganService;
    private final CustomerService customerService;
    private final ReviewService reviewService;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          LapanganService lapanganService,
                          PromoService promoService,
                          CustomerService customerService,
                          ReviewService reviewService) {
        this.bookingRepository = bookingRepository;
        this.lapanganService = lapanganService;
        this.promoService = promoService;
        this.customerService = customerService;
        this.reviewService = reviewService;
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan."));
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

        // Validasi jadwal dan hitung total harga
        double totalPrice = 0.0;
        for (JadwalRequest jadwal : jadwalList) {
            // Validasi jam operasional
            if (jadwal.getJam() < lapangan.getJamBuka().getHour() || jadwal.getJam() >= lapangan.getJamTutup().getHour()) {
                throw new RuntimeException("Jam " + jadwal.getJam() + " tidak dalam jam operasional lapangan.");
            }
            totalPrice += jadwal.getHarga();
        }

        // Set bookingDate berdasarkan waktu saat customer melakukan booking
        LocalDateTime bookingDateTime = LocalDateTime.now();
        booking.setBookingDate(bookingDateTime);

        // Set jam mulai dan jam selesai
        int jamMulai = jadwalList.get(0).getJam();
        booking.setJamMulai(jamMulai);
        booking.setJamSelesai(jamMulai + jadwalList.size()); // Sesuaikan dengan logika bisnis Anda

        // Terapkan promo jika tersedia
        if (kodePromo != null && !kodePromo.trim().isEmpty()) {
            Promo promo = promoService.findByKodePromo(kodePromo);
            if (promo != null && promoService.isPromoValid(promo) && !promoService.isPromoClaimedByCustomer(customer, promo)) {
                promoService.claimPromo(customer, promo);
                booking.setPromo(promo);
                // Terapkan diskon
                double discount = (totalPrice * promo.getDiskonPersen()) / 100.0;
                double discountedPrice = totalPrice - discount;
                booking.setTotalPrice(discountedPrice);
            } else {
                throw new RuntimeException("Promo tidak valid atau sudah digunakan.");
            }
        } else {
            booking.setTotalPrice(totalPrice);
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
        LocalDateTime endOfDay = tanggal.plusDays(1).atStartOfDay();

        List<Booking> bookings = bookingRepository.findByLapanganIdAndBookingDateBetween(lapanganId, startOfDay, endOfDay);

        // Asumsikan jam operasional lapangan adalah 08:00 - 22:00
        List<Integer> jamOperasional = IntStream.rangeClosed(8, 22).boxed().collect(Collectors.toList());

        // Tentukan jam yang sudah dibooking
        Set<Integer> jamBooked = bookings.stream()
                                         .flatMap(booking -> IntStream.range(booking.getJamMulai(), booking.getJamSelesai()).boxed())
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

    public List<BookingDTO> getBookingsByCustomerAndDate(Customer customer, LocalDate tanggal) {
        LocalDateTime startOfDay = tanggal.atStartOfDay();
        LocalDateTime endOfDay = tanggal.plusDays(1).atStartOfDay();
        List<Booking> bookings = bookingRepository.findByCustomerAndBookingDateBetween(customer, startOfDay, endOfDay);
        return bookings.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<Booking> getUpcomingBookings(Customer customer) {
        return bookingRepository.findByCustomerAndBookingDateAfter(customer, LocalDateTime.now());
    }

    @Transactional
    public List<BookingDTO> getBookingHistoryDTO(Customer customer) {
        List<Booking> bookings = bookingRepository.findByCustomerAndBookingDateBefore(customer, LocalDateTime.now());
        return bookings.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
    
        // Inisialisasi LapanganDTO
        LapanganDTO lapanganDTO = new LapanganDTO();
        lapanganDTO.setId(booking.getLapangan().getId());
        lapanganDTO.setNamaLapangan(booking.getLapangan().getNamaLapangan());
        lapanganDTO.setAlamatLapangan(booking.getLapangan().getAlamatLapangan());
        dto.setLapangan(lapanganDTO);
    
        dto.setBookingDate(booking.getBookingDate());
        dto.setJamMulai(booking.getJamMulai());
        dto.setJamSelesai(booking.getJamSelesai());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setPaymentProofFilename(booking.getPaymentProofFilename());
    
        // Inisialisasi ReviewDTO jika review tidak null
        if (booking.getReview() != null) {
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setRating(booking.getReview().getRating());
            reviewDTO.setKomentar(booking.getReview().getKomentar());
            reviewDTO.setUsername(booking.getCustomer().getUsername());
            reviewDTO.setTanggalReview(booking.getReview().getTanggalReview());
            dto.setReview(reviewDTO);
        }
    
        return dto;
    }

    public List<Booking> getBookingHistory(Customer customer) {
        return bookingRepository.findByCustomerAndBookingDateBefore(customer, LocalDateTime.now());
    }

    @Transactional
    public void addReview(Long bookingId, int rating, String komentar) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan."));

        if (booking.getBookingDate().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Booking masih berlangsung.");
        }

        if (booking.getReview() != null) {
            throw new RuntimeException("Review sudah ada untuk booking ini.");
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setRating(rating);
        review.setKomentar(komentar);
        review.setTanggalReview(LocalDateTime.now());
        review.setLapangan(booking.getLapangan()); // Set Lapangan

        reviewService.addReview(review);

        booking.setReview(review);
        bookingRepository.save(booking);
    }
}
