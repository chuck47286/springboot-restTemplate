package com.example.myapplication.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

/**
 * @Author yucheng
 * @Date 2023/10/19 16:26
 * @Version 1.0
 */
@Slf4j
@RestController
//@Configuration
public class RestTemplateController {

    @Autowired
    private Environment environment;


    /**
     * 最终的请求都会转到这个服务
     */
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String hello() {
        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest().build();
        String requestURL = uriComponents.toUriString();
        String clientIP = uriComponents.getHost();
        int serverPort = uriComponents.getPort();

        log.info("Request URL: {}, Client IP: {}, Server Port: {}", requestURL, clientIP, serverPort);
        return "Hello World -- invoker; uri= " + requestURL;
    }

}
