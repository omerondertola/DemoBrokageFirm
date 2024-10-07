package com.example.demo.customers;

import com.example.demo.customers.model.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {

    @Transactional
    Optional<Customer> findByEmail(String email);
}
