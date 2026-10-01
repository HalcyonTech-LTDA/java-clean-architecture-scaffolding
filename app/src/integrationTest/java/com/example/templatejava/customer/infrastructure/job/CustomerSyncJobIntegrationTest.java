package com.example.templatejava.customer.infrastructure.job;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.templatejava.common.AbstractIntegrationTest;
import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.domain.model.Customer.CustomerStatus;
import com.example.templatejava.customer.domain.repository.CustomerRepository;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("customer-sync-job")
@Import(CustomerSyncJob.class)
class CustomerSyncJobIntegrationTest extends AbstractIntegrationTest {

    @Autowired private CustomerSyncJob customerSyncJob;

    @Autowired private CustomerRepository customerRepository;

    @Test
    @DisplayName(
            "should execute customer sync job and enrich pending customers using MockServer and"
                    + " MongoDB")
    void shouldSyncCustomersSuccessfully() {
        String customerId = "cust-sync-1";
        Customer customer =
                Customer.create(customerId, "Sync User", "sync@example.com", Instant.now());
        customerRepository.save(customer);

        MockServerClient mockServer =
                new MockServerClient(
                        mockServerContainer.getHost(), mockServerContainer.getServerPort());
        mockServer.reset();

        mockServer
                .when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/api/v1/bureau/customers/" + customerId))
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(
                                        "{\"customerId\": \""
                                                + customerId
                                                + "\", \"score\": 820, \"status\": \"APPROVED\"}"));

        customerSyncJob.syncCustomers();

        Customer updatedCustomer = customerRepository.findById(customerId).orElseThrow();

        assertThat(updatedCustomer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(updatedCustomer.getBureauScore()).isEqualTo(820);
    }
}
