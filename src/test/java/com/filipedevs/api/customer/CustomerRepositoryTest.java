package com.filipedevs.api.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository underTest;

    @Test
    void shouldReturnCustomerWhenFindByEmail() {
        // given
        String email = "filipe@gmail.com";
        Customer customer = new Customer("Filipe", "filipe@gmail.com", "BE");
        underTest.save(customer);
        // when
        Optional<Customer> customerByEmail = underTest.findByEmail(email);
        // then
        assertTrue(customerByEmail.isPresent());
    }
}