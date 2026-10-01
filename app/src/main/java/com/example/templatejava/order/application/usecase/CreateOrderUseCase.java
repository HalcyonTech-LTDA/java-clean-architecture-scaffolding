package com.example.templatejava.order.application.usecase;

import com.example.templatejava.order.domain.model.Order;
import java.math.BigDecimal;
import java.util.Objects;

public interface CreateOrderUseCase {

    Order execute(CreateOrderCommand command);

    record CreateOrderCommand(String customerId, BigDecimal amount) {
        public CreateOrderCommand {
            Objects.requireNonNull(customerId, "customerId must not be null");
            Objects.requireNonNull(amount, "amount must not be null");
        }
    }
}
