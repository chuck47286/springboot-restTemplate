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
//
//    private ClientHttpResponse tryAlternateURIs(HttpRequest request, byte[] body,
//                                                ClientHttpRequestExecution execution) {
//        System.out.println("               原来的URI请求失败：" + request.getURI());
//        List<String> serviceAddresses = loadBalancer.loadServiceAddresses(request.getURI().getHost());
//
//        for (String address : serviceAddresses) {
//            URI newUri = URI.create(address);
//            MyHttpRequest newRequest = new MyHttpRequest(request, newUri);
//            System.out.println("               尝试新的URI：" + newRequest.getURI());
//
//            long st1 = System.currentTimeMillis();
//            try {
//                ClientHttpResponse response = execution.execute(newRequest, body);
//                System.out.println("tryAlternateURIs succeed! time cost= " + (System.currentTimeMillis() - st1));
//                if (response.getStatusCode().is2xxSuccessful()) {
//                    System.out.println("               新的URI请求成功：" + newRequest.getURI());
//                    return response;
//                } else {
//                    System.out.println("               新的URI请求失败：" + newRequest.getURI());
//                }
//            } catch (Exception e) {
//                System.out.println("tryAlternateURIs failed! time cost= " + (System.currentTimeMillis() - st1));
//                System.out.println("               新的URI请求异常：" + newRequest.getURI()+ ", err= " + e.getMessage());
////                newException.printStackTrace();
//            }
//        }
//
////        throw new RuntimeException("All URI requests failed");
//        log.info("All URI requests failed");
//        return null;
//    }


//    private List<String> loadServiceAddresses(URI uri) {
//        Map<String, String> biz = bizConfig.getJjt();
//        String bizAddress = biz.get("address");
//        if (StrUtil.isBlank(bizAddress)) {
//            return Collections.emptyList();
//        }
//        String[] splits = bizAddress.split(",");
//        return  Arrays.stream(splits).map(split -> split.trim()).filter(address -> !address.equals(uri.getHost())).map(address -> address + uri.getPath()).collect(Collectors.toList());
//    }
}
