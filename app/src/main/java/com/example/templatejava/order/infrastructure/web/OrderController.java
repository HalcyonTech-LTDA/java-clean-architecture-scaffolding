package com.example.templatejava.order.infrastructure.web;

import com.example.templatejava.order.application.usecase.CreateOrderUseCase;
import com.example.templatejava.order.application.usecase.FindOrderByIdUseCase;
import com.example.templatejava.order.domain.exception.OrderNotFoundException;
import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.infrastructure.web.mapper.OrderWebMapper;
import com.example.templatejava.order.infrastructure.web.request.CreateOrderRequest;
import com.example.templatejava.order.infrastructure.web.response.OrderResponse;
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
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final FindOrderByIdUseCase findOrderByIdUseCase;
    private final OrderWebMapper orderWebMapper;

    public OrderController(
            CreateOrderUseCase createOrderUseCase,
            FindOrderByIdUseCase findOrderByIdUseCase,
            OrderWebMapper orderWebMapper) {
        this.createOrderUseCase =
                Objects.requireNonNull(createOrderUseCase, "createOrderUseCase must not be null");
        this.findOrderByIdUseCase =
                Objects.requireNonNull(
                        findOrderByIdUseCase, "findOrderByIdUseCase must not be null");
        this.orderWebMapper =
                Objects.requireNonNull(orderWebMapper, "orderWebMapper must not be null");
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = createOrderUseCase.execute(orderWebMapper.toCommand(request));
        OrderResponse response = orderWebMapper.toResponse(order);
        return ResponseEntity.created(URI.create("/orders/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("id") String id) {
        return findOrderByIdUseCase
                .execute(id)
                .map(orderWebMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
