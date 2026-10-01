package com.example.templatejava.customer.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.customer.application.usecase.CreateCustomerUseCase;
import com.example.templatejava.customer.application.usecase.FindCustomerByIdUseCase;
import com.example.templatejava.customer.domain.exception.CustomerNotFoundException;
import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.infrastructure.web.mapper.CustomerWebMapper;
import com.example.templatejava.customer.infrastructure.web.request.CreateCustomerRequest;
import com.example.templatejava.customer.infrastructure.web.response.CustomerResponse;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class CustomerControllerTest {

    private FakeCreateCustomerUseCase createCustomerUseCase;
    private FakeFindCustomerByIdUseCase findCustomerByIdUseCase;
    private CustomerWebMapper customerWebMapper;
    private CustomerController controller;

    @BeforeEach
    void setUp() {
        createCustomerUseCase = new FakeCreateCustomerUseCase();
        findCustomerByIdUseCase = new FakeFindCustomerByIdUseCase();
        customerWebMapper = new CustomerWebMapper();
        controller =
                new CustomerController(
                        createCustomerUseCase, findCustomerByIdUseCase, customerWebMapper);
    }

    @Test
    @DisplayName("should create customer and return 201 Created")
    void shouldCreateCustomer() {
        var request = new CreateCustomerRequest("John", "john@example.com");

        ResponseEntity<CustomerResponse> response = controller.createCustomer(request);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("John");
        assertThat(response.getBody().email()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("should get customer by id and return 200 OK")
    void shouldGetCustomerById() {
        Customer customer = Customer.create("c-1", "John", "john@example.com", Instant.now());
        findCustomerByIdUseCase.customerToReturn = customer;

        ResponseEntity<CustomerResponse> response = controller.getCustomerById("c-1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo("c-1");
    }

    @Test
    @DisplayName("should throw CustomerNotFoundException when customer is not found")
    void shouldThrowWhenNotFound() {
        findCustomerByIdUseCase.customerToReturn = null;

        assertThatThrownBy(() -> controller.getCustomerById("unknown"))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with ID 'unknown' was not found");
    }

    private static class FakeCreateCustomerUseCase implements CreateCustomerUseCase {
        @Override
        public Customer execute(CreateCustomerCommand command) {
            return Customer.create("generated-id", command.name(), command.email(), Instant.now());
        }
    }

    private static class FakeFindCustomerByIdUseCase implements FindCustomerByIdUseCase {
        private Customer customerToReturn;

        @Override
        public Optional<Customer> execute(String customerId) {
            return Optional.ofNullable(customerToReturn);
        }
    }
}
