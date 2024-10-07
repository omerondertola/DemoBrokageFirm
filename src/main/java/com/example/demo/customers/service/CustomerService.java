package com.example.demo.customers.service;

import com.example.demo.customers.CustomerRepo;
import com.example.demo.customers.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepo customerRepo;
    private final PasswordEncoder passwordEncoder;

    public Customer create(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepo.save(customer);
    }

    public Optional<Customer> getCustomer(long customerId) {
        return customerRepo.findById(customerId);
    }

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }
}
