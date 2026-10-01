package com.example.templatejava.order.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.order.application.usecase.CreateOrderUseCase;
import com.example.templatejava.order.application.usecase.FindOrderByIdUseCase;
import com.example.templatejava.order.domain.exception.OrderNotFoundException;
import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.infrastructure.web.mapper.OrderWebMapper;
import com.example.templatejava.order.infrastructure.web.request.CreateOrderRequest;
import com.example.templatejava.order.infrastructure.web.response.OrderResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class OrderControllerTest {

    private FakeCreateOrderUseCase createOrderUseCase;
    private FakeFindOrderByIdUseCase findOrderByIdUseCase;
    private OrderWebMapper orderWebMapper;
    private OrderController controller;

    @BeforeEach
    void setUp() {
        createOrderUseCase = new FakeCreateOrderUseCase();
        findOrderByIdUseCase = new FakeFindOrderByIdUseCase();
        orderWebMapper = new OrderWebMapper();
        controller = new OrderController(createOrderUseCase, findOrderByIdUseCase, orderWebMapper);
    }

    @Test
    @DisplayName("should create order and return 201 Created")
    void shouldCreateOrder() {
        var request = new CreateOrderRequest("c-1", new BigDecimal("100.00"));

        ResponseEntity<OrderResponse> response = controller.createOrder(request);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().customerId()).isEqualTo("c-1");
        assertThat(response.getBody().amount()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("should get order by id and return 200 OK")
    void shouldGetOrderById() {
        Order order = Order.create("ord-1", "c-1", new BigDecimal("100.00"), Instant.now());
        findOrderByIdUseCase.orderToReturn = order;

        ResponseEntity<OrderResponse> response = controller.getOrderById("ord-1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo("ord-1");
    }

    @Test
    @DisplayName("should throw OrderNotFoundException when order is not found")
    void shouldThrowWhenNotFound() {
        findOrderByIdUseCase.orderToReturn = null;

        assertThatThrownBy(() -> controller.getOrderById("unknown"))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order with ID 'unknown' was not found");
    }

    private static class FakeCreateOrderUseCase implements CreateOrderUseCase {
        @Override
        public Order execute(CreateOrderCommand command) {
            return Order.create("gen-ord-1", command.customerId(), command.amount(), Instant.now());
        }
    }

    private static class FakeFindOrderByIdUseCase implements FindOrderByIdUseCase {
        private Order orderToReturn;

        @Override
        public Optional<Order> execute(String id) {
            return Optional.ofNullable(orderToReturn);
        }
    }
}
