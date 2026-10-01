package com.example.templatejava.order.infrastructure.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.order.application.usecase.CreateOrderUseCase.CreateOrderCommand;
import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.infrastructure.web.request.CreateOrderRequest;
import com.example.templatejava.order.infrastructure.web.response.OrderResponse;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderWebMapperTest {

    private OrderWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderWebMapper();
    }

    @Test
    @DisplayName("should map request to command")
    void shouldMapRequestToCommand() {
        var request = new CreateOrderRequest("c-1", new BigDecimal("100.00"));
        CreateOrderCommand command = mapper.toCommand(request);

        assertThat(command.customerId()).isEqualTo("c-1");
        assertThat(command.amount()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("should map domain order to response")
    void shouldMapDomainToResponse() {
        Instant now = Instant.now();
        Order order = Order.create("ord-1", "c-1", new BigDecimal("100.00"), now);
        OrderResponse response = mapper.toResponse(order);

        assertThat(response.id()).isEqualTo("ord-1");
        assertThat(response.customerId()).isEqualTo("c-1");
        assertThat(response.amount()).isEqualByComparingTo("100.00");
        assertThat(response.status()).isEqualTo("CREATED");
        assertThat(response.createdAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("should throw NullPointerException when argument is null")
    void shouldThrowWhenNull() {
        assertThatThrownBy(() -> mapper.toCommand(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("request must not be null");

        assertThatThrownBy(() -> mapper.toResponse(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("order must not be null");
    }
}
