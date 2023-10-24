package com.example.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author yucheng
 * @Date 2023/10/20 10:13
 * @Version 1.0
 */
@Data
@Configuration
@ConfigurationProperties("iitp.itcs")
public class BizConfig {
    Map<String, String> jjt = new HashMap<>();

}
