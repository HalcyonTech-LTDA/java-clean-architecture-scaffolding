package com.example.templatejava.customer.infrastructure.webclient.bureau;

import com.example.templatejava.customer.infrastructure.webclient.bureau.response.BureauClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-bureau-client", url = "${app.feign.bureau.url}")
public interface CustomerBureauFeignClient {

    @GetMapping("/api/v1/bureau/customers/{id}")
    BureauClientResponse getCustomerReport(@PathVariable("id") String customerId);
}
