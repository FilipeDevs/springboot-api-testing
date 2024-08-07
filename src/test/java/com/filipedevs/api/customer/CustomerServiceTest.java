package com.filipedevs.api.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    CustomerService underTest;

    @Mock
    CustomerRepository customerRepository;

    // This tells Mockito to capture arguments of type Customer that are passed to mock methods
    @Captor
    ArgumentCaptor<Customer> customerArgumentCaptor;

    // Initialize the CustomerService with the mocked CustomerRepository before each test
    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerRepository);
    }

    @Test
    void shouldGetAllCustomers() {
        // when: call the getCustomers method on the CustomerService instance
        underTest.getCustomers();
        // then: verify that the findAll method on the customerRepository mock was called
        verify(customerRepository).findAll();
    }

    @Test
    void createCustomer() {
        // given
        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest(
                "Filipe",
                "filipe@gmail.com",
                "BE");
        // when
        // verify that the save method on the customerRepository mock was called
        underTest.createCustomer(createCustomerRequest);
        // then
        verify(customerRepository).save(customerArgumentCaptor.capture());

        // capture the Customer object passed to the save method
        Customer customerCaptured = customerArgumentCaptor.getValue();

        // assert that the captured Customer object has the same details as the CreateCustomerRequest
        assertThat(customerCaptured.getName()).isEqualTo(createCustomerRequest.getName());
        assertThat(customerCaptured.getEmail()).isEqualTo(createCustomerRequest.getEmail());
        assertThat(customerCaptured.getAddress()).isEqualTo(createCustomerRequest.getAddress());

    }

    @Test
    @Disabled
    void updateCustomer() {
        // Test for updating a customer is disabled and not implemented yet
    }

    @Test
    @Disabled
    void deleteCustomer() {
        // Test for deleting a customer is disabled and not implemented yet
    }
}
