package com.example.templatejava.order.application.gateway;

public interface CustomerVerificationProviderGateway {

    boolean isCustomerEligible(String customerId);
}
