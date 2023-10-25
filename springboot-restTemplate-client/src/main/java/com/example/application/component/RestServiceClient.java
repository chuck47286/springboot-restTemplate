package com.example.application.component;

import cn.hutool.core.util.StrUtil;
import com.example.application.config.BizConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author yucheng
 * @Date 2023/10/20 10:04
 * @Version 1.0
 */
@Slf4j
@Component
public class RestServiceClient {

    private RestTemplate restTemplate;

    public RestServiceClient(@Qualifier("getMyRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    private BizConfig bizConfig;


    public ResponseEntity<String> sendRequestToAvailableService(String requestURI) {

        ResponseEntity<String> response = null;
        try {

            response = restTemplate.exchange(requestURI, HttpMethod.GET, null, String.class);
        } catch (Exception e) {
            log.debug("err={}", e.getMessage());
        }
        return response;
        /**
         * 原来的逻辑：
         * 需要自己轮训判断到底远端的那个rul可用
         * 现在是放到拦截器中实现
         */
//        List<String> collect = loadServiceAddress();
//        for (String serviceUrl : collect) {
//            // 1. 检查服务是否可用
//            if (isServiceAvailable(serviceUrl)) {
//                // 2. 如果服务可用，发送请求
//                ResponseEntity<String> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, null, String.class);
//                if (response.getStatusCode() == HttpStatus.OK) {
//                    return response;
//                }
//            }
//        }
//
//        // 3. 如果没有可用的服务，抛出异常
//        throw new RuntimeException("No available services");
    }

    private List<String> loadServiceAddress() {
        Map<String, String> biz = bizConfig.getJjt();
        String bizAddress = biz.get("address");
        if (StrUtil.isBlank(bizAddress)) {
            return Collections.emptyList();
        }
        String[] splits = bizAddress.split(",");
        List<String> collect = Arrays.stream(splits).map(split -> split.trim()).collect(Collectors.toList());
        return collect;
    }

    private boolean isServiceAvailable(String serviceUrl) {
        try {
            // 使用HEAD请求来检查服务是否可达
            ResponseEntity<String> response = restTemplate.exchange(serviceUrl, HttpMethod.HEAD, null, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
}
