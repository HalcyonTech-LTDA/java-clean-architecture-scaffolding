package com.example.templatejava.customer.application.usecase;

import com.example.templatejava.customer.domain.model.Customer;
import java.util.Optional;

public interface FindCustomerByIdUseCase {

    Optional<Customer> execute(String customerId);
}
