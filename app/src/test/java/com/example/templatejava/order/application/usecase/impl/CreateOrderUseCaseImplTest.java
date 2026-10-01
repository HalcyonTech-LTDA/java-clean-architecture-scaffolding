package com.example.templatejava.order.application.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.order.application.gateway.CustomerVerificationProviderGateway;
import com.example.templatejava.order.application.usecase.CreateOrderUseCase.CreateOrderCommand;
import com.example.templatejava.order.domain.exception.CustomerNotEligibleException;
import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.domain.model.Order.OrderStatus;
import com.example.templatejava.order.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateOrderUseCaseImplTest {

    private InMemoryOrderRepository orderRepository;
    private FakeCustomerVerificationGateway customerGateway;
    private Clock fixedClock;
    private CreateOrderUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        customerGateway = new FakeCustomerVerificationGateway();
        fixedClock = Clock.fixed(Instant.parse("2026-03-30T12:00:00Z"), ZoneId.of("UTC"));
        useCase = new CreateOrderUseCaseImpl(orderRepository, customerGateway, fixedClock);
    }

    @Test
    @DisplayName("should create order successfully when customer is eligible")
    void shouldCreateOrderSuccessfully() {
        customerGateway.eligible = true;
        var command = new CreateOrderCommand("cust-100", new BigDecimal("150.00"));

        Order order = useCase.execute(command);

        assertThat(order).isNotNull();
        assertThat(order.getId()).isNotBlank();
        assertThat(order.getCustomerId()).isEqualTo("cust-100");
        assertThat(order.getAmount()).isEqualByComparingTo("150.00");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getCreatedAt()).isEqualTo(fixedClock.instant());

        assertThat(orderRepository.findById(order.getId())).isPresent();
    }

    @Test
    @DisplayName("should throw CustomerNotEligibleException when customer is not eligible")
    void shouldThrowWhenCustomerNotEligible() {
        customerGateway.eligible = false;
        var command = new CreateOrderCommand("cust-suspended", new BigDecimal("150.00"));

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CustomerNotEligibleException.class)
                .hasMessage("Customer 'cust-suspended' is not eligible to place orders");
    }

    @Test
    @DisplayName("should throw NullPointerException when command is null")
    void shouldThrowWhenCommandIsNull() {
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("command must not be null");
    }

    @Test
    @DisplayName("should throw NullPointerException when constructor dependencies are null")
    void shouldThrowWhenDependenciesAreNull() {
        assertThatThrownBy(() -> new CreateOrderUseCaseImpl(null, customerGateway, fixedClock))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("orderRepository must not be null");

        assertThatThrownBy(() -> new CreateOrderUseCaseImpl(orderRepository, null, fixedClock))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customerVerificationProviderGateway must not be null");

        assertThatThrownBy(() -> new CreateOrderUseCaseImpl(orderRepository, customerGateway, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("clock must not be null");
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

    private static class FakeCustomerVerificationGateway
            implements CustomerVerificationProviderGateway {
        private boolean eligible = true;

        @Override
        public boolean isCustomerEligible(String customerId) {
            return eligible;
        }
    }
}
