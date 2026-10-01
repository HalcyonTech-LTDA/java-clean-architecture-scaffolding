package com.example.templatejava.order.infrastructure.database.mapper;

import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.domain.model.Order.OrderStatus;
import com.example.templatejava.order.infrastructure.database.entity.OrderMongoEntity;
import java.util.Objects;

public class OrderDatabaseMapper {

    public OrderMongoEntity toEntity(Order order) {
        Objects.requireNonNull(order, "order must not be null");
        return new OrderMongoEntity(
                order.getId(),
                order.getCustomerId(),
                order.getAmount(),
                order.getStatus().name(),
                order.getCreatedAt());
    }

    public Order toDomain(OrderMongoEntity entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                entity.getAmount(),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt());
    }
}
