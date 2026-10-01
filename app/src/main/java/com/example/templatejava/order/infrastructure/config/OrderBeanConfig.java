package com.example.templatejava.order.infrastructure.config;

import com.example.templatejava.customer.application.api.CustomerFacade;
import com.example.templatejava.order.application.gateway.CustomerVerificationProviderGateway;
import com.example.templatejava.order.application.usecase.CreateOrderUseCase;
import com.example.templatejava.order.application.usecase.ExpireOrdersUseCase;
import com.example.templatejava.order.application.usecase.FindOrderByIdUseCase;
import com.example.templatejava.order.application.usecase.impl.CreateOrderUseCaseImpl;
import com.example.templatejava.order.application.usecase.impl.ExpireOrdersUseCaseImpl;
import com.example.templatejava.order.application.usecase.impl.FindOrderByIdUseCaseImpl;
import com.example.templatejava.order.domain.repository.OrderRepository;
import com.example.templatejava.order.infrastructure.acl.CustomerVerificationAdapter;
import com.example.templatejava.order.infrastructure.database.adapter.OrderDatabaseAdapter;
import com.example.templatejava.order.infrastructure.database.mapper.OrderDatabaseMapper;
import com.example.templatejava.order.infrastructure.database.repository.SpringDataOrderRepository;
import com.example.templatejava.order.infrastructure.web.mapper.OrderWebMapper;
import java.time.Clock;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OrderBeanConfig {

    @Bean
    public OrderDatabaseMapper orderDatabaseMapper() {
        return new OrderDatabaseMapper();
    }

    @Bean
    public OrderWebMapper orderWebMapper() {
        return new OrderWebMapper();
    }

    @Bean
    public OrderRepository orderRepository(
            SpringDataOrderRepository springDataOrderRepository,
            OrderDatabaseMapper orderDatabaseMapper) {
        return new OrderDatabaseAdapter(springDataOrderRepository, orderDatabaseMapper);
    }

    @Bean
    public CustomerVerificationProviderGateway customerVerificationProviderGateway(
            CustomerFacade customerFacade) {
        return new CustomerVerificationAdapter(customerFacade);
    }

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            OrderRepository orderRepository,
            CustomerVerificationProviderGateway customerVerificationProviderGateway,
            Clock clock) {
        return new CreateOrderUseCaseImpl(
                orderRepository, customerVerificationProviderGateway, clock);
    }

    @Bean
    public ExpireOrdersUseCase expireOrdersUseCase(OrderRepository orderRepository, Clock clock) {
        return new ExpireOrdersUseCaseImpl(orderRepository, clock, Duration.ofMinutes(15));
    }

    @Bean
    public FindOrderByIdUseCase findOrderByIdUseCase(OrderRepository orderRepository) {
        return new FindOrderByIdUseCaseImpl(orderRepository);
    }
}
