package com.example.templatejava.customer.domain.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String customerId) {
        super("Customer with ID '" + customerId + "' was not found");
    }
}
