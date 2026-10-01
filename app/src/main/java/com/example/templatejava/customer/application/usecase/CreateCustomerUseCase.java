package com.example.templatejava.customer.application.usecase;

import com.example.templatejava.customer.domain.model.Customer;
import java.util.Objects;

public interface CreateCustomerUseCase {

    Customer execute(CreateCustomerCommand command);

    record CreateCustomerCommand(String name, String email) {
        public CreateCustomerCommand {
            Objects.requireNonNull(name, "name must not be null");
            Objects.requireNonNull(email, "email must not be null");
        }
    }
}
