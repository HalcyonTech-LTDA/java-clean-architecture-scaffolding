package com.example.templatejava.customer.application.usecase.impl;

import com.example.templatejava.customer.application.usecase.FindCustomerByIdUseCase;
import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.domain.repository.CustomerRepository;
import java.util.Objects;
import java.util.Optional;

public class FindCustomerByIdUseCaseImpl implements FindCustomerByIdUseCase {

    private final CustomerRepository customerRepository;

    public FindCustomerByIdUseCaseImpl(CustomerRepository customerRepository) {
        this.customerRepository =
                Objects.requireNonNull(customerRepository, "customerRepository must not be null");
    }

    @Override
    public Optional<Customer> execute(String customerId) {
        Objects.requireNonNull(customerId, "customerId must not be null");
        return customerRepository.findById(customerId);
    }
}
