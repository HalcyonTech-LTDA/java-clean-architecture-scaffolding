package com.example.templatejava.customer.infrastructure.config;

import com.example.templatejava.customer.application.api.CustomerFacade;
import com.example.templatejava.customer.application.api.impl.CustomerFacadeImpl;
import com.example.templatejava.customer.application.gateway.CustomerBureauProviderGateway;
import com.example.templatejava.customer.application.usecase.CreateCustomerUseCase;
import com.example.templatejava.customer.application.usecase.FindCustomerByIdUseCase;
import com.example.templatejava.customer.application.usecase.SyncCustomersUseCase;
import com.example.templatejava.customer.application.usecase.impl.CreateCustomerUseCaseImpl;
import com.example.templatejava.customer.application.usecase.impl.FindCustomerByIdUseCaseImpl;
import com.example.templatejava.customer.application.usecase.impl.SyncCustomersUseCaseImpl;
import com.example.templatejava.customer.domain.repository.CustomerRepository;
import com.example.templatejava.customer.infrastructure.database.adapter.CustomerDatabaseAdapter;
import com.example.templatejava.customer.infrastructure.database.mapper.CustomerDatabaseMapper;
import com.example.templatejava.customer.infrastructure.database.repository.SpringDataCustomerRepository;
import com.example.templatejava.customer.infrastructure.web.mapper.CustomerWebMapper;
import com.example.templatejava.customer.infrastructure.webclient.adapter.CustomerBureauAdapter;
import com.example.templatejava.customer.infrastructure.webclient.bureau.CustomerBureauFeignClient;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CustomerBeanConfig {

    @Bean
    public CustomerDatabaseMapper customerDatabaseMapper() {
        return new CustomerDatabaseMapper();
    }

    @Bean
    public CustomerWebMapper customerWebMapper() {
        return new CustomerWebMapper();
    }

    @Bean
    public CustomerRepository customerRepository(
            SpringDataCustomerRepository springDataRepository,
            CustomerDatabaseMapper customerDatabaseMapper) {
        return new CustomerDatabaseAdapter(springDataRepository, customerDatabaseMapper);
    }

    @Bean
    public CustomerFacade customerFacade(CustomerRepository customerRepository) {
        return new CustomerFacadeImpl(customerRepository);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase(
            CustomerRepository customerRepository, Clock clock) {
        return new CreateCustomerUseCaseImpl(customerRepository, clock);
    }

    @Bean
    public CustomerBureauProviderGateway customerBureauProviderGateway(
            CustomerBureauFeignClient feignClient) {
        return new CustomerBureauAdapter(feignClient);
    }

    @Bean
    public SyncCustomersUseCase syncCustomersUseCase(
            CustomerRepository customerRepository,
            CustomerBureauProviderGateway customerBureauProviderGateway) {
        return new SyncCustomersUseCaseImpl(customerRepository, customerBureauProviderGateway);
    }

    @Bean
    public FindCustomerByIdUseCase findCustomerByIdUseCase(CustomerRepository customerRepository) {
        return new FindCustomerByIdUseCaseImpl(customerRepository);
    }
}
