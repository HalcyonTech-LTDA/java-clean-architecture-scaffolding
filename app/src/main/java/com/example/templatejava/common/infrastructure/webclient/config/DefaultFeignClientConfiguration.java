package com.example.templatejava.common.infrastructure.webclient.config;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import feign.optionals.OptionalDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.cloud.openfeign.support.FeignEncoderProperties;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.PageableSpringEncoder;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.databind.json.JsonMapper;

public class DefaultFeignClientConfiguration {

    @Bean
    public FeignHttpMessageConverters feignHttpMessageConverters(
            ObjectProvider<JsonMapper> jsonMapperProvider) {
        JsonMapper jsonMapper = jsonMapperProvider.getIfAvailable();
        JacksonJsonHttpMessageConverter jsonConverter =
                jsonMapper != null
                        ? new JacksonJsonHttpMessageConverter(jsonMapper)
                        : new JacksonJsonHttpMessageConverter();

        List<HttpMessageConverter<?>> converters =
                List.of(
                        jsonConverter,
                        new FormHttpMessageConverter(),
                        new StringHttpMessageConverter(StandardCharsets.UTF_8),
                        new ByteArrayHttpMessageConverter(),
                        new ResourceHttpMessageConverter(false));

        return new FeignHttpMessageConverters(
                new StaticListableBeanFactory()
                        .getBeanProvider(
                                org.springframework.boot.http.converter.autoconfigure
                                        .ClientHttpMessageConvertersCustomizer.class),
                new StaticListableBeanFactory()
                        .getBeanProvider(HttpMessageConverterCustomizer.class)) {
            @Override
            public List<HttpMessageConverter<?>> getConverters() {
                return converters;
            }
        };
    }

    @Bean
    public Encoder feignEncoder(
            ObjectProvider<FeignHttpMessageConverters> feignHttpMessageConverters) {
        return new PageableSpringEncoder(
                new SpringEncoder(
                        new SpringFormEncoder(),
                        new FeignEncoderProperties(),
                        feignHttpMessageConverters));
    }

    @Bean
    public Decoder feignDecoder(
            ObjectProvider<FeignHttpMessageConverters> feignHttpMessageConverters) {
        return new OptionalDecoder(
                new ResponseEntityDecoder(new SpringDecoder(feignHttpMessageConverters)));
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new ErrorDecoder.Default();
    }
}
