package com.filipedevs.api.customer;

import com.filipedevs.api.exception.CustomerEmailUnavailableException;
import com.filipedevs.api.exception.CustomerNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerService {


    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public void createCustomer(CreateCustomerRequest createCustomerRequest) {

        Optional<Customer> customerByEmail = customerRepository.findByEmail(createCustomerRequest.getEmail());

        if (customerByEmail.isPresent()) {
            throw new CustomerEmailUnavailableException("The email " + createCustomerRequest.getEmail() + " is already taken.");
        }

        Customer customer = new Customer(
                createCustomerRequest.getName(),
                createCustomerRequest.getEmail(),
                createCustomerRequest.getAddress()
        );

        customerRepository.save(customer);
    }

    public void updateCustomer(Long id, String name, String email, String address) {
        Optional<Customer> customerById = customerRepository.findById(id);
        if (customerById.isEmpty()) {
            throw new CustomerNotFoundException("Customer with id " + id + " does not exist");
        }
        Customer customer = customerById.get();

        if(Objects.nonNull(name) && !name.isEmpty() && !Objects.equals(customer.getName(), name)){
            customer.setName(name);
        }

        if(Objects.nonNull(email) && !email.isEmpty() && !Objects.equals(customer.getEmail(), email)){
            Optional<Customer> customerByEmail = customerRepository.findByEmail(email);

            if (customerByEmail.isPresent()) {
                throw new CustomerEmailUnavailableException("The email " + customerByEmail.get().getEmail() + " is already taken.");
            }
            customer.setEmail(email);
        }

        if(Objects.nonNull(address) && !address.isEmpty() && !Objects.equals(customer.getAddress(), address)){
            customer.setAddress(address);
        }

        customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        boolean exists = customerRepository.existsById(id);
        if (!exists) {
            throw new CustomerNotFoundException("Customer with id " + id + " does not exist");
        }
        customerRepository.deleteById(id);
    }
}
