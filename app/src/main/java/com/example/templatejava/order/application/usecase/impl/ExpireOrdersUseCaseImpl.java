package com.example.templatejava.order.application.usecase.impl;

import com.example.templatejava.order.application.usecase.ExpireOrdersUseCase;
import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.domain.model.Order.OrderStatus;
import com.example.templatejava.order.domain.repository.OrderRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class ExpireOrdersUseCaseImpl implements ExpireOrdersUseCase {

    private final OrderRepository orderRepository;
    private final Clock clock;
    private final Duration expirationDuration;

    public ExpireOrdersUseCaseImpl(
            OrderRepository orderRepository, Clock clock, Duration expirationDuration) {
        this.orderRepository =
                Objects.requireNonNull(orderRepository, "orderRepository must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
        this.expirationDuration =
                Objects.requireNonNull(expirationDuration, "expirationDuration must not be null");
    }

    @Override
    public int execute() {
        Instant threshold = clock.instant().minus(expirationDuration);
        List<Order> staleOrders = orderRepository.findCreatedBefore(OrderStatus.CREATED, threshold);

        int count = 0;
        for (Order order : staleOrders) {
            order.expire();
            orderRepository.save(order);
            count++;
        }
        return count;
    }
}
