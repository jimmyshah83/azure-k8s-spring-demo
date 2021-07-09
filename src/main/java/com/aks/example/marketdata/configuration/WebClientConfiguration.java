package com.aks.example.marketdata.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient marketStackWebClient() {
        return WebClient.builder()
                .baseUrl("http://api.marketstack.com/v1")
                .build();
    }
}
