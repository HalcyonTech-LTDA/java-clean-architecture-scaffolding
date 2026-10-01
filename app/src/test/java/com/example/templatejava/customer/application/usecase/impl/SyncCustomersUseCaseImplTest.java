package com.example.templatejava.customer.application.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.customer.application.gateway.CustomerBureauProviderGateway;
import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.domain.model.Customer.CustomerStatus;
import com.example.templatejava.customer.domain.repository.CustomerRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SyncCustomersUseCaseImplTest {

    private InMemoryCustomerRepository customerRepository;
    private FakeBureauProviderGateway bureauGateway;
    private SyncCustomersUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        customerRepository = new InMemoryCustomerRepository();
        bureauGateway = new FakeBureauProviderGateway();
        useCase = new SyncCustomersUseCaseImpl(customerRepository, bureauGateway);
    }

    @Test
    @DisplayName("should sync pending customers and update bureau score and status")
    void shouldSyncCustomersSuccessfully() {
        Customer c1 = Customer.create("c-1", "Alice", "alice@example.com", Instant.now());
        Customer c2 = Customer.create("c-2", "Bob", "bob@example.com", Instant.now());
        customerRepository.save(c1);
        customerRepository.save(c2);

        bureauGateway.registerReport("c-1", 850, "APPROVED");
        bureauGateway.registerReport("c-2", 400, "REJECTED");

        int updatedCount = useCase.execute();

        assertThat(updatedCount).isEqualTo(2);

        Customer updatedC1 = customerRepository.findById("c-1").orElseThrow();
        assertThat(updatedC1.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(updatedC1.getBureauScore()).isEqualTo(850);

        Customer updatedC2 = customerRepository.findById("c-2").orElseThrow();
        assertThat(updatedC2.getStatus()).isEqualTo(CustomerStatus.SUSPENDED);
        assertThat(updatedC2.getBureauScore()).isEqualTo(400);
    }

    @Test
    @DisplayName("should throw NullPointerException when constructor dependencies are null")
    void shouldThrowWhenDependenciesAreNull() {
        assertThatThrownBy(() -> new SyncCustomersUseCaseImpl(null, bureauGateway))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customerRepository must not be null");

        assertThatThrownBy(() -> new SyncCustomersUseCaseImpl(customerRepository, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customerBureauProviderGateway must not be null");
    }

    private static class InMemoryCustomerRepository implements CustomerRepository {
        private final List<Customer> customers = new ArrayList<>();

        @Override
        public Customer save(Customer customer) {
            customers.removeIf(c -> c.getId().equals(customer.getId()));
            customers.add(customer);
            return customer;
        }

        @Override
        public Optional<Customer> findById(String id) {
            return customers.stream().filter(c -> c.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Customer> findByEmail(String email) {
            return customers.stream().filter(c -> c.getEmail().equals(email)).findFirst();
        }

        @Override
        public List<Customer> findByStatus(CustomerStatus status) {
            return customers.stream().filter(c -> c.getStatus() == status).toList();
        }
    }

    private static class FakeBureauProviderGateway implements CustomerBureauProviderGateway {
        private final Map<String, BureauData> reports = new HashMap<>();

        void registerReport(String customerId, int score, String status) {
            reports.put(customerId, new BureauData(customerId, score, status));
        }

        @Override
        public BureauData fetchBureauData(String customerId, String email) {
            BureauData data = reports.get(customerId);
            if (data == null) {
                return new BureauData(customerId, 0, "NOT_FOUND");
            }
            return data;
        }
    }
}
