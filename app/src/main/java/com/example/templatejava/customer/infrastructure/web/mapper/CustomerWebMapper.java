package com.example.templatejava.customer.infrastructure.web.mapper;

import com.example.templatejava.customer.application.usecase.CreateCustomerUseCase.CreateCustomerCommand;
import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.infrastructure.web.request.CreateCustomerRequest;
import com.example.templatejava.customer.infrastructure.web.response.CustomerResponse;
import java.util.Objects;

public class CustomerWebMapper {

    public CreateCustomerCommand toCommand(CreateCustomerRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return new CreateCustomerCommand(request.name(), request.email());
    }

    public CustomerResponse toResponse(Customer customer) {
        Objects.requireNonNull(customer, "customer must not be null");
        return new CustomerResponse(
                customer.getId(), customer.getName(), customer.getEmail(), customer.getCreatedAt());
    }
}
