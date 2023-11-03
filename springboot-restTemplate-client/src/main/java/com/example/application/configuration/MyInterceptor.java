package com.example.application.configuration;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author yucheng
 * @Date 2023/10/19 16:44
 * @Version 1.0
 */
@Slf4j
@Component
public class MyInterceptor implements ClientHttpRequestInterceptor {

//    @Autowired
//    private BizConfig bizConfig;

    @Autowired
    private LoadBalancer loadBalancer;


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
        log.info("               原来的URI：{}", request.getURI());
        MyHttpRequest newRequest = null;
        String nextServiceUrl = loadBalancer.getNextServiceUrl();
        while (StrUtil.isNotBlank(nextServiceUrl)) {
            try {
                URI newUri = URI.create(nextServiceUrl);
                newRequest = new MyHttpRequest(request, newUri);
                return tryOriginalURI(newRequest, body, execution);
            } catch (Exception e) {
                log.info("{}", e.getMessage());
                if (newRequest != null) {
                    String url = buildRequiredUrl(newRequest.getURI());
                    loadBalancer.checkAndShutDownServices(url);
                    nextServiceUrl = loadBalancer.getNextServiceUrl();
                }
            }
        }
        log.info("no service available");
        return null;
    }

    private String buildRequiredUrl(URI uri) {
        if (uri == null) {
            return "";
        }
        return "http://" + uri.getHost() + ":" + uri.getPort() + uri.getPath();
    }

    private ClientHttpResponse tryOriginalURI(HttpRequest request, byte[] body,
                                              ClientHttpRequestExecution execution) throws Exception {
        ClientHttpResponse response = execution.execute(request, body);
        if (response.getStatusCode().is2xxSuccessful()) {
//            System.out.println("               原来的URI请求成功：" + request.getURI());
            return response;
        }
        System.out.println(" 原来的URI请求不成功：返回 null");
        throw new RuntimeException("Original URI request failed");
    }

    public static void main(String[] args) {
//        String input = "http://127.0.0.1:9081/,http:/127.0.0.1:9082";
//        String input = "http://127.0.0.1";
        String input = "http://127.0.0.1:9081/,http:/127.0.0.1:9082/get/request";
        /**
        // 使用正则表达式匹配 IP:PORT 格式的内容
        Pattern pattern = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d+)");
        Matcher matcher = pattern.matcher(input);

        List<String> matchedResults = new ArrayList<>();

        while (matcher.find()) {
            matchedResults.add(matcher.group());
        }

        // 输出匹配到的结果
        for (String result : matchedResults) {
            System.out.println(result);
        }
         */
        final String substring = input.substring(input.lastIndexOf(",") + 1);
        System.out.println(substring);
        final String substring1 = substring.substring(substring.indexOf("/") + 1);
        System.out.println(substring1);

    }
}
