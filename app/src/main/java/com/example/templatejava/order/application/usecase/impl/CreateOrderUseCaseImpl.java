package com.example.templatejava.order.application.usecase.impl;

import com.example.templatejava.order.application.gateway.CustomerVerificationProviderGateway;
import com.example.templatejava.order.application.usecase.CreateOrderUseCase;
import com.example.templatejava.order.domain.exception.CustomerNotEligibleException;
import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.domain.repository.OrderRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final CustomerVerificationProviderGateway customerVerificationProviderGateway;
    private final Clock clock;

    public CreateOrderUseCaseImpl(
            OrderRepository orderRepository,
            CustomerVerificationProviderGateway customerVerificationProviderGateway,
            Clock clock) {
        this.orderRepository =
                Objects.requireNonNull(orderRepository, "orderRepository must not be null");
        this.customerVerificationProviderGateway =
                Objects.requireNonNull(
                        customerVerificationProviderGateway,
                        "customerVerificationProviderGateway must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public Order execute(CreateOrderCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        boolean eligible =
                customerVerificationProviderGateway.isCustomerEligible(command.customerId());
        if (!eligible) {
            throw new CustomerNotEligibleException(command.customerId());
        }

        String orderId = UUID.randomUUID().toString();
        Instant now = clock.instant();
        Order order = Order.create(orderId, command.customerId(), command.amount(), now);

        return orderRepository.save(order);
    }
}
