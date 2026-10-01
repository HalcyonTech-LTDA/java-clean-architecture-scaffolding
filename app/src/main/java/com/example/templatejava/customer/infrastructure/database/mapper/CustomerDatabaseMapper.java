package com.example.templatejava.customer.infrastructure.database.mapper;

import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.domain.model.Customer.CustomerStatus;
import com.example.templatejava.customer.infrastructure.database.entity.CustomerMongoEntity;
import java.util.Objects;

public class CustomerDatabaseMapper {

    public CustomerMongoEntity toEntity(Customer customer) {
        Objects.requireNonNull(customer, "customer must not be null");
        return new CustomerMongoEntity(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getStatus().name(),
                customer.getBureauScore(),
                customer.getCreatedAt());
    }

    public Customer toDomain(CustomerMongoEntity entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        return new Customer(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                CustomerStatus.valueOf(entity.getStatus()),
                entity.getBureauScore(),
                entity.getCreatedAt());
    }
}
