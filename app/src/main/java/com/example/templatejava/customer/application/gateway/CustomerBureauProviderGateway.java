package com.example.templatejava.customer.application.gateway;

public interface CustomerBureauProviderGateway {

    BureauData fetchBureauData(String customerId, String email);

    record BureauData(String customerId, int score, String status) {}
}
