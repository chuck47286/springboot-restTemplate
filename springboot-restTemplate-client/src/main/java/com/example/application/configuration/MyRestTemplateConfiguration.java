package com.example.application.configuration;

import com.example.application.aspect.MyLoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(1000);                // 设置连接超时，单位是毫秒
        factory.setReadTimeout(5000);                   // 设置读取超时，单位是毫秒
        factory.setConnectionRequestTimeout(3000);      // 设置读等待取超时，单位是毫秒
        return new RestTemplate(factory);
    }

    @Bean("getRestTemplate")
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
