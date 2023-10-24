package com.example.application.configuration;

import com.example.application.aspect.MyLoadBalanced;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author yucheng
 * @Date 2023/10/19 16:51
 * @Version 1.0
 */
@Configuration
public class MyAutoConfiguration {

    @Autowired(required=false)
    @MyLoadBalanced
    private List<RestTemplate> myTemplates = Collections.emptyList();

    @Autowired
    private MyInterceptor myInterceptor;

    @Bean
    public SmartInitializingSingleton myLoadBalancedRestTemplateInitializer() {
        System.out.println("====  这个Bean将在容器初始化时创建    =====");
        return new SmartInitializingSingleton() {

            public void afterSingletonsInstantiated() {
                for(RestTemplate tpl : myTemplates) {
                    // 创建一个自定义的拦截器实例
//                    MyInterceptor mi = new MyInterceptor();

                    // 获取RestTemplate原来的拦截器
                    List list = new ArrayList(tpl.getInterceptors());
                    // 添加到拦截器集合
                    list.add(myInterceptor);
                    // 将新的拦截器集合设置到RestTemplate实例
                    tpl.setInterceptors(list);
                }
            }
        };
    }
}
