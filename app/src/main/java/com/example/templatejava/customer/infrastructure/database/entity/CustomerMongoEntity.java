package com.example.templatejava.customer.infrastructure.database.entity;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customers")
public class CustomerMongoEntity {

    @Id private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String status;
    private Integer bureauScore;
    private Instant createdAt;

    public CustomerMongoEntity() {}

    public CustomerMongoEntity(
            String id,
            String name,
            String email,
            String status,
            Integer bureauScore,
            Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.bureauScore = bureauScore;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getBureauScore() {
        return bureauScore;
    }

    public void setBureauScore(Integer bureauScore) {
        this.bureauScore = bureauScore;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
