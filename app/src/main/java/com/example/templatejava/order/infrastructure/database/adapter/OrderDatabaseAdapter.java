package com.example.templatejava.order.infrastructure.database.adapter;

import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.domain.model.Order.OrderStatus;
import com.example.templatejava.order.domain.repository.OrderRepository;
import com.example.templatejava.order.infrastructure.database.entity.OrderMongoEntity;
import com.example.templatejava.order.infrastructure.database.mapper.OrderDatabaseMapper;
import com.example.templatejava.order.infrastructure.database.repository.SpringDataOrderRepository;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OrderDatabaseAdapter implements OrderRepository {

    private final SpringDataOrderRepository springDataOrderRepository;
    private final OrderDatabaseMapper orderDatabaseMapper;

    public OrderDatabaseAdapter(
            SpringDataOrderRepository springDataOrderRepository,
            OrderDatabaseMapper orderDatabaseMapper) {
        this.springDataOrderRepository =
                Objects.requireNonNull(
                        springDataOrderRepository, "springDataOrderRepository must not be null");
        this.orderDatabaseMapper =
                Objects.requireNonNull(orderDatabaseMapper, "orderDatabaseMapper must not be null");
    }

    @Override
    public Order save(Order order) {
        Objects.requireNonNull(order, "order must not be null");
        OrderMongoEntity entity = orderDatabaseMapper.toEntity(order);
        OrderMongoEntity savedEntity = springDataOrderRepository.save(entity);
        return orderDatabaseMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(String id) {
        Objects.requireNonNull(id, "id must not be null");
        return springDataOrderRepository.findById(id).map(orderDatabaseMapper::toDomain);
    }

    @Override
    public List<Order> findCreatedBefore(OrderStatus status, Instant threshold) {
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(threshold, "threshold must not be null");
        return springDataOrderRepository
                .findByStatusAndCreatedAtBefore(status.name(), threshold)
                .stream()
                .map(orderDatabaseMapper::toDomain)
                .toList();
    }
}
