package com.example.templatejava.order.infrastructure.web.response;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
        String id, String customerId, BigDecimal amount, String status, Instant createdAt) {}
