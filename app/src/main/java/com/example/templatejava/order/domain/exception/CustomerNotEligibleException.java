package com.example.templatejava.order.domain.exception;

public class CustomerNotEligibleException extends RuntimeException {

    public CustomerNotEligibleException(String customerId) {
        super("Customer '" + customerId + "' is not eligible to place orders");
    }
}
