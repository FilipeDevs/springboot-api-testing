package com.filipedevs.api.customer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    public List<Customer> getCustomers() {
        return List.of(new Customer(
                1L,
                "Filipe",
                "Chaussée de Louvain 475",
                "Rua 1"));
    }

}
