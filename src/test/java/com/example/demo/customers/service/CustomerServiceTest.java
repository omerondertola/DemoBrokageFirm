package com.example.demo.customers.service;

import com.example.demo.customers.CustomerRepo;
import com.example.demo.customers.model.AppSecurityRoles;
import com.example.demo.customers.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepo customerRepo;

    @Mock
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        System.out.println("opening mocks..");
        MockitoAnnotations.openMocks(this);
        System.out.println("mocks opened..");
    }

    @Test
    public void testShallSaveCustomer() {
        // Given
        var customer = Customer.builder()
                .email("omerondertola@gmail.com")
                .firstName("Ömer Önder")
                .lastName("TOLA")
                .password("onder")
                .role(AppSecurityRoles.ADMIN)
                .build();

        var expected = Customer.builder()
                .id(1)
                .email("omerondertola@gmail.com")
                .firstName("Ömer Önder")
                .lastName("TOLA")
                .password("encoded-password")
                .role(AppSecurityRoles.ADMIN)
                .build();

        // Mock the Calls
        Mockito.when(passwordEncoder.encode(customer.getPassword()))
                .thenReturn("encoded-password");

        Mockito.when(customerRepo.save(customer))
                .thenReturn(expected);

        // When
        Customer savedCustomer = customerService.create(customer);

        // Then
        assertEquals(expected.getFirstName(), savedCustomer.getFirstName());
        assertEquals(expected.getLastName(), savedCustomer.getLastName());
        assertEquals(expected.getEmail(), savedCustomer.getEmail());
        assertEquals(expected.getRole(), savedCustomer.getRole());
        assertEquals("encoded-password" , savedCustomer.getPassword());
        assertEquals(expected.getId(), savedCustomer.getId());
    }

    @Test
    public void testShallGetCustomer() {
        // Given
        var expected = Customer.builder()
                .id(1)
                .email("omerondertola@gmail.com")
                .firstName("Ömer Önder")
                .lastName("TOLA")
                .password("encoded-password")
                .role(AppSecurityRoles.ADMIN)
                .build();

        // Mock the calls
        Mockito.when(customerService.getCustomer(1))
                .thenReturn(Optional.of(expected));

        // When
        Optional<Customer> returnValue = customerService.getCustomer(1);

        // Then
        assertEquals(expected.getUsername(), returnValue.get().getUsername());
        assertEquals(expected.getFirstName(), returnValue.get().getFirstName());
        assertEquals(expected.getLastName(), returnValue.get().getLastName());
        assertEquals(expected.getPassword(), returnValue.get().getPassword());
        assertEquals(expected.getRole(), returnValue.get().getRole());
    }

    @Test
    public void testShallGetAllCustomers() {
        // Given
        var c1 = Customer.builder()
                .id(1)
                .email("omerondertola@gmail.com")
                .firstName("Ömer Önder")
                .lastName("TOLA")
                .password("encoded-password")
                .role(AppSecurityRoles.ADMIN)
                .build();

        var c2 = Customer.builder()
                .id(1)
                .email("defnetola@gmail.com")
                .firstName("Defne")
                .lastName("TOLA")
                .password("defne-encoded-password")
                .role(AppSecurityRoles.CUSTOMER)
                .build();

        var c3 = Customer.builder()
                .id(1)
                .email("iremtola@gmail.com")
                .firstName("İrem")
                .lastName("TOLA")
                .password("irem-encoded-password")
                .role(AppSecurityRoles.CUSTOMER)
                .build();

        var expected = List.of(c1, c2, c3);

        // Mock the Calls
        Mockito.when(customerRepo.findAll())
                .thenReturn(expected);

        // When
        List<Customer> returnValue = customerService.getAllCustomers();

        // Then
        assertEquals(3, returnValue.size());

        assertEquals(c1.getFirstName(), returnValue.get(0).getFirstName());
        assertEquals(c1.getLastName(), returnValue.get(0).getLastName());
        assertEquals(c1.getEmail(), returnValue.get(0).getEmail());
        assertEquals(c1.getPassword(), returnValue.get(0).getPassword());
        assertEquals(c1.getRole(), returnValue.get(0).getRole());

        assertEquals(c2.getFirstName(), returnValue.get(1).getFirstName());
        assertEquals(c2.getLastName(), returnValue.get(1).getLastName());
        assertEquals(c2.getEmail(), returnValue.get(1).getEmail());
        assertEquals(c2.getPassword(), returnValue.get(1).getPassword());
        assertEquals(c2.getRole(), returnValue.get(1).getRole());

        assertEquals(c3.getFirstName(), returnValue.get(2).getFirstName());
        assertEquals(c3.getLastName(), returnValue.get(2).getLastName());
        assertEquals(c3.getEmail(), returnValue.get(2).getEmail());
        assertEquals(c3.getPassword(), returnValue.get(2).getPassword());
        assertEquals(c3.getRole(), returnValue.get(2).getRole());
    }
}