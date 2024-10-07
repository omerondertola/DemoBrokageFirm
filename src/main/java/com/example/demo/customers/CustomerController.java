package com.example.demo.customers;

import com.example.demo.utils.ControllerUtils;
import com.example.demo.customers.service.CustomerService;
import com.example.demo.customers.model.Customer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apis/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    // TODO - LIST ALL CUSTOMERS REQUIREMENT
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }


    // TODO - GET CUSTOMER BY ID REQUIREMENT
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomer(
            @PathVariable("customerId")
            long customerId
    ) {
        return customerService.getCustomer(customerId)
                .map(ResponseEntity::ok)
                .orElseGet(
                        () -> new ResponseEntity<>(HttpStatus.NOT_FOUND)
                );
    }


    // TODO - CREATE CUSTOMER REQUIREMENT
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Customer> createCustomer(
            @Valid @RequestBody
            Customer customer,

            BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors()) {
            ControllerUtils.logErrors(bindingResult);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(customerService.create(customer));
        }
    }



}
