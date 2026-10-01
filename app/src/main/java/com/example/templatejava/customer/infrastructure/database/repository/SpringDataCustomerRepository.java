package com.example.templatejava.customer.infrastructure.database.repository;

import com.example.templatejava.customer.infrastructure.database.entity.CustomerMongoEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataCustomerRepository extends MongoRepository<CustomerMongoEntity, String> {

    Optional<CustomerMongoEntity> findByEmail(String email);

    List<CustomerMongoEntity> findByStatus(String status);
}
