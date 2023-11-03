package com.example.application.controller;

import com.example.application.component.RestServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
//import org.springframework.web.client.RestTemplate;

/**
 * @Author yucheng
 * @Date 2023/10/19 16:26
 * @Version 1.0
 */
@Slf4j
@RestController
public class RestTemplateController {

//    @Autowired
//    private RestTemplate restTpl;
    @Autowired
    private RestServiceClient restServiceClient;


    /**
     * 最终的请求都会转到这个服务
     */
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String hello(HttpServletRequest request) {
        // 根据名称来调用服务，这个URI会被拦截器所置换
//        String json = restTpl.getForObject("http://provider-server/hello", String.class);
//        return json;
//        String requestURI =  "test[http://localhost:9081/]" + request.getRequestURI();
        String requestURI =  "http://127.0.0.1:9081,127.0.0.1:9082/get/request";
//        System.out.println("请求路径：" + requestURI);

        ResponseEntity<String> stringResponseEntity = restServiceClient.sendRequestToAvailableService(requestURI);
        log.info("{}", stringResponseEntity);
        return stringResponseEntity != null ? stringResponseEntity.toString() : "no result !!!";
    }

}
