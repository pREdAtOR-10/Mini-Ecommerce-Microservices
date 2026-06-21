package com.abhi.microservices.orderservice;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private final LoadBalancerInterceptor loadBalancerInterceptor;

    // We explicitly ask Spring to give us the actual routing engine bean
    public RestClientConfig(LoadBalancerInterceptor loadBalancerInterceptor) {
        this.loadBalancerInterceptor = loadBalancerInterceptor;
    }
    @Bean
    @LoadBalanced // <-- Magic happens here. Enables Eureka service name lookups!
    public RestClient loadBalancedRestClientBuilder() {
        return RestClient.builder()
                // Explicitly inject the Eureka routing engine interceptor!
                .requestInterceptor(loadBalancerInterceptor)
                .build();
    }

}
