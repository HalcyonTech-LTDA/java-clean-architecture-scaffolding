package com.example.templatejava.order.infrastructure.web.mapper;

import com.example.templatejava.order.application.usecase.CreateOrderUseCase.CreateOrderCommand;
import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.infrastructure.web.request.CreateOrderRequest;
import com.example.templatejava.order.infrastructure.web.response.OrderResponse;
import java.util.Objects;

public class OrderWebMapper {

    public CreateOrderCommand toCommand(CreateOrderRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return new CreateOrderCommand(request.customerId(), request.amount());
    }

    public OrderResponse toResponse(Order order) {
        Objects.requireNonNull(order, "order must not be null");
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getAmount(),
                order.getStatus().name(),
                order.getCreatedAt());
    }
}
