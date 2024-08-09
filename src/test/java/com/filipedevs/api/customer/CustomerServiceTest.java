package com.filipedevs.api.customer;

import com.filipedevs.api.exception.CustomerEmailUnavailableException;
import com.filipedevs.api.exception.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    void shouldCreateCustomer() {
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
    void shouldNotCreateCustomerAndThrowNotFoundExceptionWhenEmailIsTaken() {
        // given
        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest(
                "Filipe",
                "filipe@gmail.com",
                "BE");

        // when
        // mock the findByEmail method on the customerRepository mock to return an Optional of a Customer
        // this will simulate that a customer with the same email already exists and trows an exception
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(new Customer()));
        // then
        // check that the outcome throws a CustomerEmailUnavailableException when the email is already taken
        assertThatThrownBy(() -> underTest.createCustomer(createCustomerRequest))
                .isInstanceOf(CustomerEmailUnavailableException.class)
                .hasMessageContaining("The email " + createCustomerRequest.getEmail() + " is already taken.");
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGivenInvalidIdWhileUpdateCustomer() {
        // given
        Long id = 1L;
        String name = "Filipe";
        String email = "filipe@gmail.com";
        String address = "BE";
        // when
        // mock the findById method on the customerRepository mock to return an empty Optional
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThatThrownBy(() ->
                underTest.updateCustomer(id, name, email, address))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer with id " + id + " does not exist");
        // verify that customerRepository never saved an invalid customer
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldOnlyUpdateCustomerName() {
        // given
        long id = 5L;
        Customer customer = new Customer("Filipe", "filipe@gmail.com", "BE");
        String newName = "Filipe Dev";
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        // when
        underTest.updateCustomer(id, newName, null, null);
        // then
        // verify that the save method on the customerRepository mock was called
        // Capture the Customer object passed to the save method and then check if the name was updated
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer customerCaptured = customerArgumentCaptor.getValue();

        // this will check if the name was updated
        assertThat(customerCaptured.getName()).isEqualTo(newName);

        // this will check if the email and address were not updated
        assertThat(customerCaptured.getEmail()).isEqualTo(customer.getEmail());
        assertThat(customerCaptured.getAddress()).isEqualTo(customer.getAddress());
    }

    @Test
    void shouldThrowEmailUnavailableExceptionWhenEmailIsTakenWhileUpdatingCustomer() {
        // given
        long id = 5L;
        Customer customer = new Customer("Filipe", "filipe@gmail.com", "BE");
        String newEmail = "filipe.devs@gmail.com";
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        // simulate that the new email is already taken
        when(customerRepository.findByEmail(newEmail)).thenReturn(Optional.of(new Customer()));
        // then
        // check that the outcome throws a CustomerEmailUnavailableException when the email is already taken
        assertThatThrownBy(() -> underTest.updateCustomer(id, null, newEmail, null))
                .isInstanceOf(CustomerEmailUnavailableException.class)
                .hasMessageContaining("The email " + newEmail + " is already taken.");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldUpdateOnlyCustomerEmail() {
        // given
        long id = 5L;
        Customer customer = new Customer("Filipe", "filipe@gmail.com", "BE");
        String newEmail = "filipe.devs@gmail.com";
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        // when
        underTest.updateCustomer(id, null, newEmail, null);
        // then
        // verify that the save method on the customerRepository mock was called
        // Capture the Customer object passed to the save method and then check if the email was updated
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer customerCaptured = customerArgumentCaptor.getValue();

        // this will check if the email was updated
        assertThat(customerCaptured.getEmail()).isEqualTo(newEmail);

        // this will check if the name and address were not updated
        assertThat(customerCaptured.getName()).isEqualTo(customer.getName());
        assertThat(customerCaptured.getAddress()).isEqualTo(customer.getAddress());
    }

    @Test
    void shouldUpdateOnlyCustomerAddress() {
        /// given
        long id = 5L;
        Customer customer = new Customer("Filipe", "filipe@gmail.com", "BE");
        String newAddress = "US";
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        // when
        underTest.updateCustomer(id, null, null, newAddress);
        // then
        // verify that the save method on the customerRepository mock was called
        // Capture the Customer object passed to the save method and then check if the address was updated
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer customerCaptured = customerArgumentCaptor.getValue();

        // this will check if the address was updated
        assertThat(customerCaptured.getAddress()).isEqualTo(newAddress);

        // this will check if the name and email were not updated
        assertThat(customerCaptured.getName()).isEqualTo(customer.getName());
        assertThat(customerCaptured.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void shouldUpdateAllAttributesWhenUpdateCustomer() {
        /// given
        long id = 5L;
        Customer customer = new Customer("Filipe", "filipe@gmail.com", "BE");

        String newAddress = "US";
        String newName = "Filipe Dev";
        String newEmail = "filipe.devs@gmail.com";

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        // when
        underTest.updateCustomer(id, newName, newEmail, newAddress);
        // then
        // verify that the save method on the customerRepository mock was called
        // Capture the Customer object passed to the save method and then check if the object was updated
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer customerCaptured = customerArgumentCaptor.getValue();

        // this will check if the address was updated
        assertThat(customerCaptured.getAddress()).isEqualTo(newAddress);
        // this will check if the name was updated
        assertThat(customerCaptured.getName()).isEqualTo(newName);
        // this will check if the email was updated
        assertThat(customerCaptured.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void shouldThrowNotFoundWhenGivenIdDoesNotExistWhileDeleteCustomer() {
        // given
        long id = 5L;
        when(customerRepository.existsById(id))
                .thenReturn(false);
        // when
        // then
        assertThatThrownBy(() ->
                underTest.deleteCustomer(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id " + id + " does not exist");
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void shouldDeleteCustomer() {
        //given
        long id = 5L;
        when(customerRepository.existsById(id))
                .thenReturn(true);
        //when
        underTest.deleteCustomer(id);
        //then
        verify(customerRepository).deleteById(id);

    }
}
