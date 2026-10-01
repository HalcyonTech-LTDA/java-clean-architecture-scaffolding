package com.example.templatejava.order.infrastructure.database.repository;

import com.example.templatejava.order.infrastructure.database.entity.OrderMongoEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataOrderRepository extends MongoRepository<OrderMongoEntity, String> {

    List<OrderMongoEntity> findByStatusAndCreatedAtBefore(String status, Instant threshold);
}
