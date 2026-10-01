package com.example.templatejava.customer.application.usecase.impl;

import com.example.templatejava.customer.application.usecase.CreateCustomerUseCase;
import com.example.templatejava.customer.domain.exception.CustomerAlreadyExistsException;
import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.domain.repository.CustomerRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class CreateCustomerUseCaseImpl implements CreateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final Clock clock;

    public CreateCustomerUseCaseImpl(CustomerRepository customerRepository, Clock clock) {
        this.customerRepository =
                Objects.requireNonNull(customerRepository, "customerRepository must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public Customer execute(CreateCustomerCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        customerRepository
                .findByEmail(command.email())
                .ifPresent(
                        existing -> {
                            throw new CustomerAlreadyExistsException(command.email());
                        });

        String customerId = UUID.randomUUID().toString();
        Instant now = clock.instant();

        Customer customer = Customer.create(customerId, command.name(), command.email(), now);
        return customerRepository.save(customer);
    }
}
