package com.example.templatejava.customer.application.api.impl;

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

class CustomerFacadeImplTest {

    private InMemoryCustomerRepository customerRepository;
    private CustomerFacadeImpl customerFacade;

    @BeforeEach
    void setUp() {
        customerRepository = new InMemoryCustomerRepository();
        customerFacade = new CustomerFacadeImpl(customerRepository);
    }

    @Test
    @DisplayName("should throw NullPointerException when repository is null")
    void shouldThrowWhenRepoIsNull() {
        assertThatThrownBy(() -> new CustomerFacadeImpl(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customerRepository must not be null");
    }

    @Test
    @DisplayName("should return false when customerId is null or blank")
    void shouldReturnFalseWhenIdIsBlank() {
        assertThat(customerFacade.isCustomerEligible(null)).isFalse();
        assertThat(customerFacade.isCustomerEligible("")).isFalse();
        assertThat(customerFacade.isCustomerEligible("   ")).isFalse();
    }

    @Test
    @DisplayName("should return false when customer is not found")
    void shouldReturnFalseWhenNotFound() {
        assertThat(customerFacade.isCustomerEligible("unknown")).isFalse();
    }

    @Test
    @DisplayName("should return true when customer exists and is active")
    void shouldReturnTrueWhenActive() {
        Customer customer =
                new Customer(
                        "cust-1",
                        "John",
                        "john@example.com",
                        CustomerStatus.ACTIVE,
                        800,
                        Instant.now());
        customerRepository.save(customer);

        assertThat(customerFacade.isCustomerEligible("cust-1")).isTrue();
    }

    @Test
    @DisplayName("should return false when customer is suspended")
    void shouldReturnFalseWhenSuspended() {
        Customer customer =
                new Customer(
                        "cust-2",
                        "Suspended",
                        "susp@example.com",
                        CustomerStatus.SUSPENDED,
                        200,
                        Instant.now());
        customerRepository.save(customer);

        assertThat(customerFacade.isCustomerEligible("cust-2")).isFalse();
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
