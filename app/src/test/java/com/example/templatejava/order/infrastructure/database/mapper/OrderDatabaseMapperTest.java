package com.example.templatejava.order.infrastructure.database.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.domain.model.Order.OrderStatus;
import com.example.templatejava.order.infrastructure.database.entity.OrderMongoEntity;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderDatabaseMapperTest {

    private OrderDatabaseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderDatabaseMapper();
    }

    @Test
    @DisplayName("should map domain order to mongo entity")
    void shouldMapDomainToEntity() {
        Instant now = Instant.now();
        Order order = Order.create("ord-1", "cust-1", new BigDecimal("55.00"), now);

        OrderMongoEntity entity = mapper.toEntity(order);

        assertThat(entity.getId()).isEqualTo("ord-1");
        assertThat(entity.getCustomerId()).isEqualTo("cust-1");
        assertThat(entity.getAmount()).isEqualByComparingTo("55.00");
        assertThat(entity.getStatus()).isEqualTo("CREATED");
        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("should map mongo entity to domain order")
    void shouldMapEntityToDomain() {
        Instant now = Instant.now();
        OrderMongoEntity entity =
                new OrderMongoEntity("ord-1", "cust-1", new BigDecimal("55.00"), "CREATED", now);

        Order order = mapper.toDomain(entity);

        assertThat(order.getId()).isEqualTo("ord-1");
        assertThat(order.getCustomerId()).isEqualTo("cust-1");
        assertThat(order.getAmount()).isEqualByComparingTo("55.00");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("should throw NullPointerException when argument is null")
    void shouldThrowWhenNull() {
        assertThatThrownBy(() -> mapper.toEntity(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("order must not be null");

        assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("entity must not be null");
    }
}
