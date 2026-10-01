package com.example.templatejava.common.infrastructure.web.exception;

import com.example.templatejava.common.infrastructure.web.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.FieldErrorDetail> details =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(
                                err ->
                                        new ErrorResponse.FieldErrorDetail(
                                                err.getField(), err.getDefaultMessage()))
                        .toList();

        ErrorResponse response =
                new ErrorResponse(
                        "VALIDATION_ERROR",
                        "Validation failed for request",
                        HttpStatus.BAD_REQUEST.value(),
                        Instant.now(),
                        request.getRequestURI(),
                        details);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse response =
                new ErrorResponse(
                        "INVALID_ARGUMENT",
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value(),
                        Instant.now(),
                        request.getRequestURI(),
                        List.of());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception caught by GlobalExceptionHandler", ex);
        ErrorResponse response =
                new ErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        Instant.now(),
                        request.getRequestURI(),
                        List.of());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
