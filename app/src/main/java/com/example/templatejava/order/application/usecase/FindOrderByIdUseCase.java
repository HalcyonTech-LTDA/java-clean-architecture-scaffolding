package com.example.templatejava.order.application.usecase;

import com.example.templatejava.order.domain.model.Order;
import java.util.Optional;

public interface FindOrderByIdUseCase {

    Optional<Order> execute(String id);
}
