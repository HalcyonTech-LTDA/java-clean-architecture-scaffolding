package com.example.templatejava.order.domain.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String orderId) {
        super("Order with ID '" + orderId + "' was not found");
    }
}
