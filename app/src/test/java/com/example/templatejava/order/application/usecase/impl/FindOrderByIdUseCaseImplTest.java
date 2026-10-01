package com.example.templatejava.order.application.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.domain.model.Order.OrderStatus;
import com.example.templatejava.order.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FindOrderByIdUseCaseImplTest {

    private InMemoryOrderRepository orderRepository;
    private FindOrderByIdUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        useCase = new FindOrderByIdUseCaseImpl(orderRepository);
    }

    @Test
    @DisplayName("should throw NullPointerException when repository is null")
    void shouldThrowWhenRepoIsNull() {
        assertThatThrownBy(() -> new FindOrderByIdUseCaseImpl(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("orderRepository must not be null");
    }

    @Test
    @DisplayName("should throw NullPointerException when id is null")
    void shouldThrowWhenIdIsNull() {
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("id must not be null");
    }

    @Test
    @DisplayName("should return order when found by id")
    void shouldReturnOrderWhenFound() {
        Order order = Order.create("ord-1", "cust-1", new BigDecimal("100.00"), Instant.now());
        orderRepository.save(order);

        Optional<Order> result = useCase.execute("ord-1");

        assertThat(result).isPresent();
        assertThat(result.get().getCustomerId()).isEqualTo("cust-1");
    }

    @Test
    @DisplayName("should return empty optional when order is not found")
    void shouldReturnEmptyWhenNotFound() {
        Optional<Order> result = useCase.execute("ord-unknown");
        assertThat(result).isEmpty();
    }

    private static class InMemoryOrderRepository implements OrderRepository {
        private final List<Order> orders = new ArrayList<>();

        @Override
        public Order save(Order order) {
            orders.removeIf(o -> o.getId().equals(order.getId()));
            orders.add(order);
            return order;
        }

        @Override
        public Optional<Order> findById(String id) {
            return orders.stream().filter(o -> o.getId().equals(id)).findFirst();
        }

        @Override
        public List<Order> findCreatedBefore(OrderStatus status, Instant threshold) {
            return orders.stream()
                    .filter(o -> o.getStatus() == status && o.getCreatedAt().isBefore(threshold))
                    .toList();
        }
    }
}
