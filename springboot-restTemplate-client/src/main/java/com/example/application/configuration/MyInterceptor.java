package com.example.application.configuration;

import cn.hutool.core.util.StrUtil;
import com.example.application.config.BizConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author yucheng
 * @Date 2023/10/19 16:44
 * @Version 1.0
 */
@Slf4j
@Component
public class MyInterceptor implements ClientHttpRequestInterceptor {

    @Autowired
    private BizConfig bizConfig;


    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        /**
         * 原先逻辑
         *
         */
//        System.out.println("=============  这是自定义拦截器实现");
//        System.out.println("               原来的URI：" + request.getURI());
//        // 换成新的请求对象（更换URI）
//        MyHttpRequest newRequest = new MyHttpRequest(request, bizConfig);
//        System.out.println("               拦截后新的URI：" + newRequest.getURI());
//        return execution.execute(newRequest, body);
        /**
         * 调整如下
         */
        System.out.println("=============  这是自定义拦截器实现");
        System.out.println("               原来的URI：" + request.getURI());

        try {
            return tryOriginalURI(request, body, execution);
        } catch (Exception originalException) {
            return tryAlternateURIs(request, body, execution);
        }
    }

    private ClientHttpResponse tryOriginalURI(HttpRequest request, byte[] body,
                                              ClientHttpRequestExecution execution) throws Exception {
        ClientHttpResponse response = execution.execute(request, body);
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("               原来的URI请求成功：" + request.getURI());
            return response;
        }
        System.out.println(" 原来的URI请求不成功：返回 null");
        return null;
//        throw new RuntimeException("Original URI request failed");
    }

    private ClientHttpResponse tryAlternateURIs(HttpRequest request, byte[] body,
                                                ClientHttpRequestExecution execution) {
        System.out.println("               原来的URI请求失败：" + request.getURI());
        List<String> serviceAddresses = loadServiceAddresses(request.getURI());

        for (String address : serviceAddresses) {
            URI newUri = URI.create(address);
            MyHttpRequest newRequest = new MyHttpRequest(request, newUri);
            System.out.println("               尝试新的URI：" + newRequest.getURI());

            try {
                ClientHttpResponse response = execution.execute(newRequest, body);
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("               新的URI请求成功：" + newRequest.getURI());
                    return response;
                } else {
                    System.out.println("               新的URI请求失败：" + newRequest.getURI());
                }
            } catch (Exception newException) {
                System.out.println("               新的URI请求异常：" + newRequest.getURI());
//                newException.printStackTrace();
            }
        }

//        throw new RuntimeException("All URI requests failed");
        log.info("All URI requests failed");
        return null;
    }


    private List<String> loadServiceAddresses(URI uri) {
        Map<String, String> biz = bizConfig.getJjt();
        String bizAddress = biz.get("address");
        if (StrUtil.isBlank(bizAddress)) {
            return Collections.emptyList();
        }
        String[] splits = bizAddress.split(",");
        return  Arrays.stream(splits).map(split -> split.trim()).filter(address -> !address.equals(uri.getHost())).map(address -> address + uri.getPath()).collect(Collectors.toList());
    }
}
