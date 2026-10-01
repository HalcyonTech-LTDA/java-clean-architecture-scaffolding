package com.example.templatejava.order.infrastructure.job;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.templatejava.common.AbstractIntegrationTest;
import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.domain.model.Order.OrderStatus;
import com.example.templatejava.order.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("order-expiration-job")
@Import(OrderExpirationJob.class)
class OrderExpirationJobIntegrationTest extends AbstractIntegrationTest {

    @Autowired private OrderExpirationJob orderExpirationJob;

    @Autowired private OrderRepository orderRepository;

    @Test
    @DisplayName("should execute order expiration job and expire stale orders in MongoDB")
    void shouldExpireStaleOrdersSuccessfully() {
        Instant past = Instant.now().minus(Duration.ofMinutes(30));
        Order staleOrder = Order.create("ord-stale-1", "cust-1", new BigDecimal("120.00"), past);
        orderRepository.save(staleOrder);

        Order freshOrder =
                Order.create("ord-fresh-1", "cust-1", new BigDecimal("45.00"), Instant.now());
        orderRepository.save(freshOrder);

        orderExpirationJob.expireStaleOrders();

        Order updatedStale = orderRepository.findById("ord-stale-1").orElseThrow();
        assertThat(updatedStale.getStatus()).isEqualTo(OrderStatus.EXPIRED);

        Order updatedFresh = orderRepository.findById("ord-fresh-1").orElseThrow();
        assertThat(updatedFresh.getStatus()).isEqualTo(OrderStatus.CREATED);
    }
}
