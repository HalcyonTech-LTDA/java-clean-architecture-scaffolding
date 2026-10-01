package com.example.templatejava.customer.infrastructure.webclient.adapter;

import com.example.templatejava.customer.application.gateway.CustomerBureauProviderGateway;
import com.example.templatejava.customer.infrastructure.webclient.bureau.CustomerBureauFeignClient;
import com.example.templatejava.customer.infrastructure.webclient.bureau.response.BureauClientResponse;
import java.util.Objects;

public class CustomerBureauAdapter implements CustomerBureauProviderGateway {

    private final CustomerBureauFeignClient feignClient;

    public CustomerBureauAdapter(CustomerBureauFeignClient feignClient) {
        this.feignClient = Objects.requireNonNull(feignClient, "feignClient must not be null");
    }

    @Override
    public BureauData fetchBureauData(String customerId, String email) {
        Objects.requireNonNull(customerId, "customerId must not be null");
        BureauClientResponse response = feignClient.getCustomerReport(customerId);
        return new BureauData(response.customerId(), response.score(), response.status());
    }
}
