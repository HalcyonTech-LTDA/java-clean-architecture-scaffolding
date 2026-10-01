package com.example.templatejava.customer.infrastructure.web;

import com.example.templatejava.customer.application.usecase.CreateCustomerUseCase;
import com.example.templatejava.customer.application.usecase.FindCustomerByIdUseCase;
import com.example.templatejava.customer.domain.exception.CustomerNotFoundException;
import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.infrastructure.web.mapper.CustomerWebMapper;
import com.example.templatejava.customer.infrastructure.web.request.CreateCustomerRequest;
import com.example.templatejava.customer.infrastructure.web.response.CustomerResponse;
import java.net.URI;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final FindCustomerByIdUseCase findCustomerByIdUseCase;
    private final CustomerWebMapper customerWebMapper;

    public CustomerController(
            CreateCustomerUseCase createCustomerUseCase,
            FindCustomerByIdUseCase findCustomerByIdUseCase,
            CustomerWebMapper customerWebMapper) {
        this.createCustomerUseCase =
                Objects.requireNonNull(
                        createCustomerUseCase, "createCustomerUseCase must not be null");
        this.findCustomerByIdUseCase =
                Objects.requireNonNull(
                        findCustomerByIdUseCase, "findCustomerByIdUseCase must not be null");
        this.customerWebMapper =
                Objects.requireNonNull(customerWebMapper, "customerWebMapper must not be null");
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @RequestBody CreateCustomerRequest request) {
        Customer customer = createCustomerUseCase.execute(customerWebMapper.toCommand(request));
        CustomerResponse response = customerWebMapper.toResponse(customer);
        return ResponseEntity.created(URI.create("/customers/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable("id") String id) {
        return findCustomerByIdUseCase
                .execute(id)
                .map(customerWebMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }
}
