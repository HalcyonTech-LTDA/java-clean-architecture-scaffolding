package com.example.templatejava.common.infrastructure.web.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String error,
        String message,
        int status,
        Instant timestamp,
        String path,
        List<FieldErrorDetail> details) {

    public record FieldErrorDetail(String field, String message) {}
}
