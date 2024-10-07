package com.example.demo.customers;

import com.example.demo.customers.model.AppSecurityRoles;
import com.example.demo.customers.model.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest {

    @Autowired
    private CustomerController customerController;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    public void testOnderCanGetOwnData() throws JsonProcessingException {
        String forObject = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com","onder")
                .getForObject("http://localhost:" + port +
                "/apis/v1/customers/1", String.class);

        Customer customer = objectMapper.readValue(forObject, Customer.class);
        assertEqualsCustomerOnder(customer);
    }

    @Test
    public void testOnderCannotGetOtherCustomerData() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com", "onder")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/customers/2", Customer.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testOnderCannotGetOtherCustomerData2() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com", "onder")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/customers/3", Customer.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testOnderCannotGetOtherCustomerData3() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com", "onder")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/customers/4", Customer.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testOnderCannotGetOtherCustomerData4() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com", "onder")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/customers/5", Customer.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testAdminCanAccessCustomerOnder() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("admin@gmail.com", "admin")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/customers/1", Customer.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Customer onder = response.getBody();
        assertEqualsCustomerOnder(onder);
    }

    @Test
    public void testAdminCanAccessCustomerDefne() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("admin@gmail.com", "admin")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/customers/2", Customer.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Customer defne = response.getBody();
        assertEqualsCustomerDefne(defne);
    }

    @Test
    public void testAdminCanAccessCustomerIrem() {
        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("admin@gmail.com", "admin")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/customers/3", Customer.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Customer irem = response.getBody();
        assertEqualsCustomerIrem(irem);
    }

    @Test
    public void testAdminCanAccessAllCustomers() {
        ResponseEntity<List> admin = this.restTemplate
                .withBasicAuth("admin@gmail.com", "admin")
                .getForEntity("http://localhost:" + port +
                        "/apis/v1/customers", List.class);

        assertEquals(HttpStatus.OK, admin.getStatusCode());
        assertNotNull(admin.getBody());
        assertEquals(4, admin.getBody().size());
    }

    @Test
    public void testAdminCreatesCustomer() {
        Customer newCustomer = Customer.builder()
                .firstName("Ece")
                .lastName("Tola")
                .email("ecetola@gmail.com")
                .password("ece")
                .role(AppSecurityRoles.CUSTOMER)
                .build();

        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("admin@gmail.com", "admin")
                .postForEntity("http://localhost:" +
                                port + "/apis/v1/customers",
                        newCustomer,
                        Customer.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Customer saved = response.getBody();
        assertNotNull(saved);
        assertEqualsCustomers(newCustomer, saved);
    }

    @Test
    public void testCustomerCannotCreateCustomer() {
        Customer newCustomer = Customer.builder()
                .firstName("Eylem")
                .lastName("Tola")
                .email("eylemtola@gmail.com")
                .password("eylem")
                .role(AppSecurityRoles.CUSTOMER)
                .build();

        ResponseEntity<Customer> response = this.restTemplate
                .withBasicAuth("omerondertola@gmail.com", "onder")
                .postForEntity("http://localhost:" +
                                port + "/apis/v1/customers",
                        newCustomer,
                        Customer.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    private void assertEqualsCustomers(Customer newCustomer, Customer saved) {
        assertEquals(newCustomer.getRole(), saved.getRole());
        assertEquals(newCustomer.getFirstName(), saved.getFirstName());
        assertEquals(newCustomer.getLastName(), saved.getLastName());
        assertEquals(newCustomer.getEmail(), saved.getEmail());
        assertTrue(passwordEncoder.matches(newCustomer.getPassword(), saved.getPassword()));
        assertEquals(newCustomer.getUsername(), saved.getEmail());
    }


    private void assertEqualsCustomerOnder(Customer customer) {
        assertEquals("Ömer Önder", customer.getFirstName());
        assertEquals("Tola", customer.getLastName());
        assertEquals("omerondertola@gmail.com", customer.getEmail());
        assertEquals(1, customer.getId());
        assertEquals(AppSecurityRoles.CUSTOMER, customer.getRole());
        assertTrue(customer.isAccountNonExpired());
        assertFalse(customer.isAdmin());
        assertTrue(customer.isEnabled());
        assertTrue(customer.isCredentialsNonExpired());
        assertTrue(customer.isAccountNonLocked());
        assertTrue(passwordEncoder.matches("onder",customer.getPassword()));
    }

    private void assertEqualsCustomerDefne(Customer customer) {
        assertEquals("Defne", customer.getFirstName());
        assertEquals("Tola", customer.getLastName());
        assertEquals("defnetola@gmail.com", customer.getEmail());
        assertEquals(2, customer.getId());
        assertEquals(AppSecurityRoles.CUSTOMER, customer.getRole());
        assertTrue(customer.isAccountNonExpired());
        assertFalse(customer.isAdmin());
        assertTrue(customer.isEnabled());
        assertTrue(customer.isCredentialsNonExpired());
        assertTrue(customer.isAccountNonLocked());
        assertTrue(passwordEncoder.matches("defne",customer.getPassword()));
    }

    private void assertEqualsCustomerIrem(Customer customer) {
        assertEquals("İrem", customer.getFirstName());
        assertEquals("Tola", customer.getLastName());
        assertEquals("iremtola@gmail.com", customer.getEmail());
        assertEquals(3, customer.getId());
        assertEquals(AppSecurityRoles.CUSTOMER, customer.getRole());
        assertTrue(customer.isAccountNonExpired());
        assertFalse(customer.isAdmin());
        assertTrue(customer.isEnabled());
        assertTrue(customer.isCredentialsNonExpired());
        assertTrue(customer.isAccountNonLocked());
        assertTrue(passwordEncoder.matches("irem",customer.getPassword()));
    }

}