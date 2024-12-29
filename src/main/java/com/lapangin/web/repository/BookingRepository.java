package com.lapangin.web.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lapangin.web.model.Booking;
import com.lapangin.web.model.Customer;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Mencari booking berdasarkan lapangan ID, rentang tanggal, dan jam mulai.
     * 
     * @param lapanganId ID lapangan
     * @param start Rentang awal tanggal (inclusive)
     * @param end Rentang akhir tanggal (exclusive)
     * @param jamMulai Jam mulai booking
     * @return List of Booking
     */
    List<Booking> findByLapanganIdAndBookingDateBetweenAndJamMulai(
        Long lapanganId, 
        LocalDateTime start, 
        LocalDateTime end, 
        int jamMulai
    );

    List<Booking> findByLapanganIdAndBookingDateBetween(
        Long lapanganId, 
        LocalDateTime start, 
        LocalDateTime end
    );

    boolean existsByLapanganIdAndBookingDateBetweenAndJamMulai(
        Long lapanganId, 
        LocalDateTime start, 
        LocalDateTime end, 
        int jamMulai
    );

    // Tambahkan metode berikut:
    
    /**
     * Mencari booking berdasarkan customer dan rentang tanggal.
     * 
     * @param customer Objek Customer
     * @param start Rentang awal tanggal (inclusive)
     * @param end Rentang akhir tanggal (exclusive)
     * @return List of Booking
     */
    List<Booking> findByCustomerAndBookingDateBetween(
        Customer customer, 
        LocalDateTime start, 
        LocalDateTime end
    );

    /**
     * Mencari booking mendatang berdasarkan customer.
     * 
     * @param customer Objek Customer
     * @param now Waktu saat ini
     * @return List of Booking
     */
    List<Booking> findByCustomerAndBookingDateAfter(
        Customer customer, 
        LocalDateTime now
    );

    List<Booking> findByBookingDateBetween(Date start, Date end);

    List<Booking> findByCustomerAndBookingDateGreaterThanOrderByBookingDateAsc(
        Customer customer, 
        LocalDateTime now
    );
}
