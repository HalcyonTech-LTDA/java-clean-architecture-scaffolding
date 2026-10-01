package com.example.templatejava.order.infrastructure.acl;

import com.example.templatejava.customer.application.api.CustomerFacade;
import com.example.templatejava.order.application.gateway.CustomerVerificationProviderGateway;
import java.util.Objects;

public class CustomerVerificationAdapter implements CustomerVerificationProviderGateway {

    private final CustomerFacade customerFacade;

    public CustomerVerificationAdapter(CustomerFacade customerFacade) {
        this.customerFacade =
                Objects.requireNonNull(customerFacade, "customerFacade must not be null");
    }

    @Override
    public boolean isCustomerEligible(String customerId) {
        return customerFacade.isCustomerEligible(customerId);
    }
}
