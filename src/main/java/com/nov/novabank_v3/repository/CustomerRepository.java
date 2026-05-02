package com.nov.novabank_v3.repository;

import com.nov.novabank_v3.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{

    Optional<Customer> findByDni(String dni);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
