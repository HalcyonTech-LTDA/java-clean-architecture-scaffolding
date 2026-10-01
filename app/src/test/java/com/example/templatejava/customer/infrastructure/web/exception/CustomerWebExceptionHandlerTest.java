package com.example.templatejava.customer.infrastructure.web.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.templatejava.common.infrastructure.web.response.ErrorResponse;
import com.example.templatejava.customer.domain.exception.CustomerAlreadyExistsException;
import com.example.templatejava.customer.domain.exception.CustomerNotFoundException;
import com.example.templatejava.customer.domain.exception.InvalidCustomerException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class CustomerWebExceptionHandlerTest {

    private CustomerWebExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new CustomerWebExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/customers");
    }

    @Test
    @DisplayName("should handle CustomerNotFoundException with 404")
    void shouldHandleNotFound() {
        var ex = new CustomerNotFoundException("c-1");
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("CUSTOMER_NOT_FOUND");
    }

    @Test
    @DisplayName("should handle CustomerAlreadyExistsException with 409")
    void shouldHandleAlreadyExists() {
        var ex = new CustomerAlreadyExistsException("test@example.com");
        ResponseEntity<ErrorResponse> response = handler.handleAlreadyExists(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("CUSTOMER_ALREADY_EXISTS");
    }

    @Test
    @DisplayName("should handle InvalidCustomerException with 422")
    void shouldHandleInvalidCustomer() {
        var ex = new InvalidCustomerException("Invalid name");
        ResponseEntity<ErrorResponse> response = handler.handleInvalidCustomer(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("INVALID_CUSTOMER");
    }
}
