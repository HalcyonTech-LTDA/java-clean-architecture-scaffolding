package com.example.templatejava.customer.domain.repository;

import com.example.templatejava.customer.domain.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(String id);

    Optional<Customer> findByEmail(String email);

    List<Customer> findByStatus(Customer.CustomerStatus status);
}
