package com.filipedevs.api.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer
            = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.2"));

    @Autowired
    CustomerRepository underTest;

    @Test
    void canEstablishConnection() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

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