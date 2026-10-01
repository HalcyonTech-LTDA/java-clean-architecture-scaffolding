package com.example.templatejava.common.infrastructure.webclient.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
        basePackages = "com.example.templatejava",
        defaultConfiguration = DefaultFeignClientConfiguration.class)
public class FeignConfig {}
