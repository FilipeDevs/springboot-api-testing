package com.filipedevs.api.customer;

import com.filipedevs.api.AbstractTestcontainersTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainersTest {


    @Autowired
    CustomerRepository underTest;

    @BeforeEach
    void setUp() {
        String email = "filipe@gmail.com";
        Customer customer = new Customer("Filipe", email, "BE");
        underTest.save(customer);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldReturnCustomerWhenFindByEmail() {
        // given
        // when
        Optional<Customer> customerByEmail = underTest.findByEmail("filipe@gmail.com");
        // then
        assertTrue(customerByEmail.isPresent());
    }

    @Test
    void shouldNotReturnCustomerWhenFindByEmailIsNotPresent() {
        // given
        // when
        Optional<Customer> customerByEmail = underTest.findByEmail("jason@gmail.com");
        // then
        assertThat(customerByEmail).isNotPresent();
    }
}