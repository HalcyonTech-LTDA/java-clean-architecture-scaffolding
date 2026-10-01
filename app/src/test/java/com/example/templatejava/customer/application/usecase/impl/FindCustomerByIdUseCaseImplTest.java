package com.example.templatejava.customer.application.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.domain.model.Customer.CustomerStatus;
import com.example.templatejava.customer.domain.repository.CustomerRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FindCustomerByIdUseCaseImplTest {

    private InMemoryCustomerRepository customerRepository;
    private FindCustomerByIdUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        customerRepository = new InMemoryCustomerRepository();
        useCase = new FindCustomerByIdUseCaseImpl(customerRepository);
    }

    @Test
    @DisplayName("should throw NullPointerException when repository is null")
    void shouldThrowWhenRepoIsNull() {
        assertThatThrownBy(() -> new FindCustomerByIdUseCaseImpl(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customerRepository must not be null");
    }

    @Test
    @DisplayName("should throw NullPointerException when id is null")
    void shouldThrowWhenIdIsNull() {
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customerId must not be null");
    }

    @Test
    @DisplayName("should return customer when found by id")
    void shouldReturnCustomerWhenFound() {
        Customer customer = Customer.create("c-1", "Alice", "alice@example.com", Instant.now());
        customerRepository.save(customer);

        Optional<Customer> result = useCase.execute("c-1");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("should return empty optional when customer is not found")
    void shouldReturnEmptyWhenNotFound() {
        Optional<Customer> result = useCase.execute("c-unknown");
        assertThat(result).isEmpty();
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
}
