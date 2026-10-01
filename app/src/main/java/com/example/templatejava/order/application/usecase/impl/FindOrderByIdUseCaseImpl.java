package com.example.templatejava.order.application.usecase.impl;

import com.example.templatejava.order.application.usecase.FindOrderByIdUseCase;
import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.domain.repository.OrderRepository;
import java.util.Objects;
import java.util.Optional;

public class FindOrderByIdUseCaseImpl implements FindOrderByIdUseCase {

    private final OrderRepository orderRepository;

    public FindOrderByIdUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository =
                Objects.requireNonNull(orderRepository, "orderRepository must not be null");
    }

    @Override
    public Optional<Order> execute(String id) {
        Objects.requireNonNull(id, "id must not be null");
        return orderRepository.findById(id);
    }
}
