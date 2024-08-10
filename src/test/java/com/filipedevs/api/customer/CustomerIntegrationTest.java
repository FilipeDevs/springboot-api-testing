package com.filipedevs.api.customer;

import com.filipedevs.api.AbstractTestcontainersTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerIntegrationTest extends AbstractTestcontainersTest {

    public static final String API_CUSTOMERS_PATH = "/api/v1/customers";

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void shouldCreateCustomer() {
        // given
        CreateCustomerRequest request = new CreateCustomerRequest(
                "name",
                "email" + UUID.randomUUID() + "@test.com",
        "address"
        );
        // when
        ResponseEntity<Void> createCustomerResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH,
                HttpMethod.POST,
                new HttpEntity<>(request),
                Void.class
        );
        // then
        assertThat(createCustomerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // After the post request, we should be able to retrieve the customer we created
        ResponseEntity<List<Customer>> allCustomersResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(allCustomersResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        // find the customer we created
        Customer customerCreated = Objects.requireNonNull(allCustomersResponse.getBody())
                .stream()
                .filter(c -> c.getEmail().equals(request.getEmail()))
                .findFirst()
                .orElseThrow();
        // comparison of customer we created with create customer request
        assertThat(customerCreated.getName()).isEqualTo(request.getName());
        assertThat(customerCreated.getEmail()).isEqualTo(request.getEmail());
        assertThat(customerCreated.getAddress()).isEqualTo(request.getAddress());

    }

    @Test
    void shouldUpdateCustomer() {
        // given
        // First we create a customer
        CreateCustomerRequest request =
                new CreateCustomerRequest(
                        "name",
                        "email" + UUID.randomUUID() + "@gmail.com", //unique
                        "address"
                );
        ResponseEntity<Void> createCustomerResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH,
                HttpMethod.POST,
                new HttpEntity<>(request),
                Void.class);

        assertThat(createCustomerResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        // get all customers request
        ResponseEntity<List<Customer>> allCustomersResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(allCustomersResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        // find the customer we created and extract the id
        Long id = Objects.requireNonNull(allCustomersResponse.getBody()).stream()
                .filter(c -> c.getEmail().equals(request.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        String newEmail = "newEmail" + UUID.randomUUID() + "@gmail.com";
        // when
        // update the customer we created, using the id and new email
        testRestTemplate.exchange(
                        API_CUSTOMERS_PATH + "/" + id + "?email=" + newEmail,
                        HttpMethod.PUT,
                        null,
                        Void.class)
                .getStatusCode().is2xxSuccessful();
        // get the customer we updated, get the customer by id
        ResponseEntity<Customer> customerByIdResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH + "/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(customerByIdResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        // Do the comparison customer updated with new email we want to update
        Customer customerUpdated = Objects.requireNonNull(customerByIdResponse.getBody());

        assertThat(customerUpdated.getName()).isEqualTo(request.getName());
        assertThat(customerUpdated.getEmail()).isEqualTo(newEmail);
        assertThat(customerUpdated.getAddress()).isEqualTo(request.getAddress());


    }

    @Test
    void shouldDeleteCustomer() {
        // given, first we create a customer
        CreateCustomerRequest request =
                new CreateCustomerRequest(
                        "name",
                        "email" + UUID.randomUUID() + "@gmail.com", //unique
                        "address"
                );
        ResponseEntity<Void> createCustomerResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH,
                HttpMethod.POST,
                new HttpEntity<>(request),
                Void.class);
        assertThat(createCustomerResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        // get all customers request
        ResponseEntity<List<Customer>> allCustomersResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(allCustomersResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        // find the customer we created and extract the id
        Long id = Objects.requireNonNull(allCustomersResponse.getBody()).stream()
                .filter(c -> c.getEmail().equals(request.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // when, delete the customer we created
        testRestTemplate.exchange(
                API_CUSTOMERS_PATH + "/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        ).getStatusCode().is2xxSuccessful();
        // then
        // get the customer we deleted, get the customer by id
        ResponseEntity<Object> customerByIdResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH + "/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // assert that the customer we deleted is not found
        assertThat(customerByIdResponse.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

    }
}