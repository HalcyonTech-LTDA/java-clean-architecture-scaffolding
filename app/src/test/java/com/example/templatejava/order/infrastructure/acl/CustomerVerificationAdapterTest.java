package com.example.templatejava.order.infrastructure.acl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.customer.application.api.CustomerFacade;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerVerificationAdapterTest {

    private FakeCustomerFacade customerFacade;
    private CustomerVerificationAdapter adapter;

    @BeforeEach
    void setUp() {
        customerFacade = new FakeCustomerFacade();
        adapter = new CustomerVerificationAdapter(customerFacade);
    }

    @Test
    @DisplayName("should return true when customer is eligible via CustomerFacade")
    void shouldReturnTrueWhenCustomerIsEligible() {
        customerFacade.setEligibility("c-1", true);

        boolean eligible = adapter.isCustomerEligible("c-1");

        assertThat(eligible).isTrue();
    }

    @Test
    @DisplayName("should return false when customer is not eligible via CustomerFacade")
    void shouldReturnFalseWhenCustomerIsNotEligible() {
        customerFacade.setEligibility("c-suspended", false);

        boolean eligible = adapter.isCustomerEligible("c-suspended");

        assertThat(eligible).isFalse();
    }

    @Test
    @DisplayName("should throw NullPointerException when customerFacade is null")
    void shouldThrowWhenFacadeIsNull() {
        assertThatThrownBy(() -> new CustomerVerificationAdapter(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customerFacade must not be null");
    }

    private static class FakeCustomerFacade implements CustomerFacade {
        private final Map<String, Boolean> eligibilityMap = new HashMap<>();

        void setEligibility(String customerId, boolean eligible) {
            eligibilityMap.put(customerId, eligible);
        }

        @Override
        public boolean isCustomerEligible(String customerId) {
            return eligibilityMap.getOrDefault(customerId, false);
        }
    }
}
