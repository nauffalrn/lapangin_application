package com.lapangin.web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lapangin.web.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUsername(String username);
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    @Query("SELECT COUNT(p) > 0 FROM Customer c JOIN c.claimedPromos p WHERE c.id = :customerId AND p.id = :promoId")
    boolean hasClaimedPromo(@Param("customerId") Long customerId, @Param("promoId") Long promoId);
}