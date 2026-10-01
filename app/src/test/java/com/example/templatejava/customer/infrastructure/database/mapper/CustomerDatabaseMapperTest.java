package com.example.templatejava.customer.infrastructure.database.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.domain.model.Customer.CustomerStatus;
import com.example.templatejava.customer.infrastructure.database.entity.CustomerMongoEntity;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerDatabaseMapperTest {

    private CustomerDatabaseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomerDatabaseMapper();
    }

    @Test
    @DisplayName("should map domain customer to mongo entity")
    void shouldMapDomainToEntity() {
        Instant now = Instant.now();
        Customer customer =
                new Customer("c-1", "John", "john@example.com", CustomerStatus.ACTIVE, 750, now);

        CustomerMongoEntity entity = mapper.toEntity(customer);

        assertThat(entity.getId()).isEqualTo("c-1");
        assertThat(entity.getName()).isEqualTo("John");
        assertThat(entity.getEmail()).isEqualTo("john@example.com");
        assertThat(entity.getStatus()).isEqualTo("ACTIVE");
        assertThat(entity.getBureauScore()).isEqualTo(750);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("should map mongo entity to domain customer")
    void shouldMapEntityToDomain() {
        Instant now = Instant.now();
        CustomerMongoEntity entity =
                new CustomerMongoEntity("c-1", "John", "john@example.com", "ACTIVE", 750, now);

        Customer customer = mapper.toDomain(entity);

        assertThat(customer.getId()).isEqualTo("c-1");
        assertThat(customer.getName()).isEqualTo("John");
        assertThat(customer.getEmail()).isEqualTo("john@example.com");
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(customer.getBureauScore()).isEqualTo(750);
        assertThat(customer.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("should throw NullPointerException when argument is null")
    void shouldThrowWhenNull() {
        assertThatThrownBy(() -> mapper.toEntity(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customer must not be null");

        assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("entity must not be null");
    }
}
