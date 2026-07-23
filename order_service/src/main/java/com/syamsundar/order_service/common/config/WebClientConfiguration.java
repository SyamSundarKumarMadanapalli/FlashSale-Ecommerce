package com.syamsundar.order_service.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient productWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://product-service:8080")
                .build();
    }
}
