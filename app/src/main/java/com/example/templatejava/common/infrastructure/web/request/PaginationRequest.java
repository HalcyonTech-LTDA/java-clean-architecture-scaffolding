package com.example.templatejava.common.infrastructure.web.request;

public record PaginationRequest(int page, int size, String sortDirection, String sortBy) {

    public PaginationRequest {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 20;
        }
        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "ASC";
        }
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }
    }
}
