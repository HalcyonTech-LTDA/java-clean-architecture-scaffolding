package com.example.templatejava.customer.infrastructure.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.customer.application.usecase.CreateCustomerUseCase.CreateCustomerCommand;
import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.infrastructure.web.request.CreateCustomerRequest;
import com.example.templatejava.customer.infrastructure.web.response.CustomerResponse;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerWebMapperTest {

    private CustomerWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomerWebMapper();
    }

    @Test
    @DisplayName("should map request to command")
    void shouldMapRequestToCommand() {
        var request = new CreateCustomerRequest("Alice", "alice@example.com");
        CreateCustomerCommand command = mapper.toCommand(request);

        assertThat(command.name()).isEqualTo("Alice");
        assertThat(command.email()).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("should map domain customer to response")
    void shouldMapDomainToResponse() {
        Instant now = Instant.now();
        Customer customer = Customer.create("c-1", "Alice", "alice@example.com", now);
        CustomerResponse response = mapper.toResponse(customer);

        assertThat(response.id()).isEqualTo("c-1");
        assertThat(response.name()).isEqualTo("Alice");
        assertThat(response.email()).isEqualTo("alice@example.com");
        assertThat(response.createdAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("should throw NullPointerException when argument is null")
    void shouldThrowWhenNull() {
        assertThatThrownBy(() -> mapper.toCommand(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("request must not be null");

        assertThatThrownBy(() -> mapper.toResponse(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customer must not be null");
    }
}
