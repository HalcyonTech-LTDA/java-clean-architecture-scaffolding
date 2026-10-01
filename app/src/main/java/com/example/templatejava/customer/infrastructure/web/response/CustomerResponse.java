package com.example.templatejava.customer.infrastructure.web.response;

import java.time.Instant;

public record CustomerResponse(String id, String name, String email, Instant createdAt) {}
