package com.example.templatejava.customer.infrastructure.webclient.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.customer.application.gateway.CustomerBureauProviderGateway.BureauData;
import com.example.templatejava.customer.infrastructure.webclient.bureau.CustomerBureauFeignClient;
import com.example.templatejava.customer.infrastructure.webclient.bureau.response.BureauClientResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerBureauAdapterTest {

    private FakeCustomerBureauFeignClient feignClient;
    private CustomerBureauAdapter adapter;

    @BeforeEach
    void setUp() {
        feignClient = new FakeCustomerBureauFeignClient();
        adapter = new CustomerBureauAdapter(feignClient);
    }

    @Test
    @DisplayName("should fetch bureau data and map to domain record")
    void shouldFetchBureauData() {
        feignClient.responseToReturn = new BureauClientResponse("c-100", 790, "APPROVED");

        BureauData result = adapter.fetchBureauData("c-100", "test@example.com");

        assertThat(result.customerId()).isEqualTo("c-100");
        assertThat(result.score()).isEqualTo(790);
        assertThat(result.status()).isEqualTo("APPROVED");
    }

    @Test
    @DisplayName("should throw NullPointerException when feignClient is null")
    void shouldThrowWhenClientIsNull() {
        assertThatThrownBy(() -> new CustomerBureauAdapter(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("feignClient must not be null");
    }

    @Test
    @DisplayName("should throw NullPointerException when customerId is null")
    void shouldThrowWhenCustomerIdIsNull() {
        assertThatThrownBy(() -> adapter.fetchBureauData(null, "email@example.com"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customerId must not be null");
    }

    private static class FakeCustomerBureauFeignClient implements CustomerBureauFeignClient {
        private BureauClientResponse responseToReturn;

        @Override
        public BureauClientResponse getCustomerReport(String customerId) {
            return responseToReturn;
        }
    }
}
