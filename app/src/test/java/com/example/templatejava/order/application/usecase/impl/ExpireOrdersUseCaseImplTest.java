package com.example.templatejava.order.application.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.domain.model.Order.OrderStatus;
import com.example.templatejava.order.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExpireOrdersUseCaseImplTest {

    private InMemoryOrderRepository orderRepository;
    private Clock fixedClock;
    private ExpireOrdersUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        fixedClock = Clock.fixed(Instant.parse("2026-03-30T12:00:00Z"), ZoneId.of("UTC"));
        useCase = new ExpireOrdersUseCaseImpl(orderRepository, fixedClock, Duration.ofMinutes(15));
    }

    @Test
    @DisplayName("should expire stale orders created before threshold")
    void shouldExpireStaleOrders() {
        Instant thirtyMinsAgo = fixedClock.instant().minus(Duration.ofMinutes(30));
        Instant fiveMinsAgo = fixedClock.instant().minus(Duration.ofMinutes(5));

        Order staleOrder =
                Order.create("ord-stale", "c-1", new BigDecimal("100.00"), thirtyMinsAgo);
        Order freshOrder = Order.create("ord-fresh", "c-2", new BigDecimal("200.00"), fiveMinsAgo);

        orderRepository.save(staleOrder);
        orderRepository.save(freshOrder);

        int expiredCount = useCase.execute();

        assertThat(expiredCount).isEqualTo(1);
        assertThat(orderRepository.findById("ord-stale").orElseThrow().getStatus())
                .isEqualTo(OrderStatus.EXPIRED);
        assertThat(orderRepository.findById("ord-fresh").orElseThrow().getStatus())
                .isEqualTo(OrderStatus.CREATED);
    }

    @Test
    @DisplayName("should return 0 when there are no stale orders")
    void shouldReturnZeroWhenNoStaleOrders() {
        int expiredCount = useCase.execute();
        assertThat(expiredCount).isEqualTo(0);
    }

    @Test
    @DisplayName("should throw NullPointerException when constructor dependencies are null")
    void shouldThrowWhenDependenciesAreNull() {
        assertThatThrownBy(
                        () -> new ExpireOrdersUseCaseImpl(null, fixedClock, Duration.ofMinutes(15)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("orderRepository must not be null");

        assertThatThrownBy(
                        () ->
                                new ExpireOrdersUseCaseImpl(
                                        orderRepository, null, Duration.ofMinutes(15)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("clock must not be null");

        assertThatThrownBy(() -> new ExpireOrdersUseCaseImpl(orderRepository, fixedClock, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("expirationDuration must not be null");
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
