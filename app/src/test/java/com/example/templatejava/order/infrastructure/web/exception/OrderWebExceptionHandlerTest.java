package com.example.templatejava.order.infrastructure.web.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.templatejava.common.infrastructure.web.response.ErrorResponse;
import com.example.templatejava.order.domain.exception.CustomerNotEligibleException;
import com.example.templatejava.order.domain.exception.InvalidOrderException;
import com.example.templatejava.order.domain.exception.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class OrderWebExceptionHandlerTest {

    private OrderWebExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new OrderWebExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/orders");
    }

    @Test
    @DisplayName("should handle OrderNotFoundException with 404")
    void shouldHandleNotFound() {
        var ex = new OrderNotFoundException("ord-1");
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("ORDER_NOT_FOUND");
    }

    @Test
    @DisplayName("should handle CustomerNotEligibleException with 422")
    void shouldHandleNotEligible() {
        var ex = new CustomerNotEligibleException("c-1");
        ResponseEntity<ErrorResponse> response = handler.handleNotEligible(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("CUSTOMER_NOT_ELIGIBLE");
    }

    @Test
    @DisplayName("should handle InvalidOrderException with 422")
    void shouldHandleInvalidOrder() {
        var ex = new InvalidOrderException("Invalid amount");
        ResponseEntity<ErrorResponse> response = handler.handleInvalidOrder(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("INVALID_ORDER");
    }
}
