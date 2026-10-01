package com.example.templatejava.order.infrastructure.web.request;

import java.math.BigDecimal;

public record CreateOrderRequest(String customerId, BigDecimal amount) {}
