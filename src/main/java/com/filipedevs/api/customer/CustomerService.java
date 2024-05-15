package com.filipedevs.api.customer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {


    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getCustomers() {
        return List.of(new Customer(
                1L,
                "Filipe",
                "Chaussée de Louvain 475",
                "Rua 1"));
    }

    public void createCustomer(CreateCustomerRequest createCustomerRequest) {
        Customer customer = new Customer(
                createCustomerRequest.getName(),
                createCustomerRequest.getEmail(),
                createCustomerRequest.getAddress()
        );

        customerRepository.save(customer);
    }
}
