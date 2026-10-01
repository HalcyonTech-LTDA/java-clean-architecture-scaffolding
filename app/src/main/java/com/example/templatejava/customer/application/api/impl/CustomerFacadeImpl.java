package com.example.templatejava.customer.application.api.impl;

import com.example.templatejava.customer.application.api.CustomerFacade;
import com.example.templatejava.customer.domain.model.Customer.CustomerStatus;
import com.example.templatejava.customer.domain.repository.CustomerRepository;
import java.util.Objects;

public class CustomerFacadeImpl implements CustomerFacade {

    private final CustomerRepository customerRepository;

    public CustomerFacadeImpl(CustomerRepository customerRepository) {
        this.customerRepository =
                Objects.requireNonNull(customerRepository, "customerRepository must not be null");
    }

    @Override
    public boolean isCustomerEligible(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return false;
        }
        return customerRepository
                .findById(customerId)
                .map(customer -> customer.getStatus() != CustomerStatus.SUSPENDED)
                .orElse(false);
    }
}
