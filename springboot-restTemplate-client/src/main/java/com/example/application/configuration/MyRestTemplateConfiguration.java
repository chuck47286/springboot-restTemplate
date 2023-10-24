package com.example.application.configuration;

import com.example.application.aspect.MyLoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @Author yucheng
 * @Date 2023/10/19 16:53
 * @Version 1.0
 */
@Configuration
public class MyRestTemplateConfiguration {
    @Bean("getMyRestTemplate")
    @MyLoadBalanced
    public RestTemplate getMyRestTemplate() {
        return new RestTemplate();
    }

    @Bean("getRestTemplate")
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
