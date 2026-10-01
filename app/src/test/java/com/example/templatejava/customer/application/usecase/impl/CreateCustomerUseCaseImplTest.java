package com.example.templatejava.customer.application.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.customer.application.usecase.CreateCustomerUseCase.CreateCustomerCommand;
import com.example.templatejava.customer.domain.exception.CustomerAlreadyExistsException;
import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.domain.model.Customer.CustomerStatus;
import com.example.templatejava.customer.domain.repository.CustomerRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateCustomerUseCaseImplTest {

    private InMemoryCustomerRepository customerRepository;
    private Clock fixedClock;
    private CreateCustomerUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        customerRepository = new InMemoryCustomerRepository();
        fixedClock = Clock.fixed(Instant.parse("2026-03-30T10:00:00Z"), ZoneId.of("UTC"));
        useCase = new CreateCustomerUseCaseImpl(customerRepository, fixedClock);
    }

    @Test
    @DisplayName("should create customer successfully when email does not exist")
    void shouldCreateCustomerSuccessfully() {
        var command = new CreateCustomerCommand("John Doe", "john.doe@example.com");

        Customer created = useCase.execute(command);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotBlank();
        assertThat(created.getName()).isEqualTo("John Doe");
        assertThat(created.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(created.getStatus()).isEqualTo(CustomerStatus.PENDING_BUREAU_ENRICHMENT);
        assertThat(created.getCreatedAt()).isEqualTo(fixedClock.instant());

        assertThat(customerRepository.findById(created.getId())).isPresent();
    }

    @Test
    @DisplayName("should throw CustomerAlreadyExistsException when email already exists")
    void shouldThrowWhenEmailAlreadyExists() {
        Customer existing =
                Customer.create(
                        "existing-id",
                        "Existing User",
                        "john.doe@example.com",
                        fixedClock.instant());
        customerRepository.save(existing);

        var command = new CreateCustomerCommand("New John", "john.doe@example.com");

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessage("Customer with email 'john.doe@example.com' already exists");
    }

    @Test
    @DisplayName("should throw NullPointerException when command is null")
    void shouldThrowWhenCommandIsNull() {
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("command must not be null");
    }

    @Test
    @DisplayName("should throw NullPointerException when dependencies are null")
    void shouldThrowWhenDependenciesAreNull() {
        assertThatThrownBy(() -> new CreateCustomerUseCaseImpl(null, fixedClock))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customerRepository must not be null");

        assertThatThrownBy(() -> new CreateCustomerUseCaseImpl(customerRepository, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("clock must not be null");
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
