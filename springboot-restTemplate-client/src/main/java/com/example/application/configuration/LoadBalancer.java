package com.example.application.configuration;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.example.application.config.BizConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author yucheng
 * @Date 2023/10/24 14:59
 * @Version 1.0
 */
@Slf4j
@Configuration
public class LoadBalancer {

//    @Autowired
//    private BizConfig bizConfig;

    @Autowired
    @Qualifier("getRestTemplate")
    private RestTemplate restTemplate;

    private List<String> originalServiceUrls; // 所有的原始服务URLs
    private List<String> activeServiceUrls;   // 当前可用的服务URLs
    private AtomicInteger currentIndex = new AtomicInteger(0);
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final int CHECK_INTERVAL = 300; // 以分钟为单位(5min)

    public LoadBalancer(BizConfig bizConfig) {
        this.originalServiceUrls = loadServiceAddresses(bizConfig);
        this.activeServiceUrls = new CopyOnWriteArrayList<>(); // 线程安全的列表
    }

    private List<String> loadServiceAddresses(BizConfig bizConfig) {
        Map<String, String> biz = bizConfig.getJjt();
        String bizAddress = biz.get("address");
        String path = biz.get("path");
        if (StrUtil.isBlank(bizAddress)) {
            return Collections.emptyList();
        }
        String[] splits = bizAddress.split(",");
        return  Arrays.stream(splits).map(split -> split.trim() + path).collect(Collectors.toList());
    }

    public String getNextServiceUrl() {
        // 尝试更新一下之前下线的服务是否已经启动
        CompletableFuture.runAsync(this::probeInactiveServices);
        if (activeServiceUrls.isEmpty()) {
            return null; // 或者抛出异常，取决于您的需求
        }
//        int index = currentIndex.getAndIncrement() % activeServiceUrls.size();
        int index = (int)(Math.random() * activeServiceUrls.size());
        return activeServiceUrls.get(index);
    }
    // 探活服务上线
    private void probeInactiveServices() {
        List<String> inactiveServices = originalServiceUrls.stream().filter(serviceUrl -> !activeServiceUrls.contains(serviceUrl)).collect(Collectors.toList());
        for (String service : inactiveServices) {
            if (isServiceAvailable(service)) {
                activeServiceUrls.add(service);
                log.info("probeInactiveServices succeed! activeServiceUrls={}", activeServiceUrls);
            }
        }
    }
    // 服务下线
    void checkAndShutDownServices(String url) {
        if (StrUtil.isBlank(url)) {
            log.info("checkAndUpdateServices check param is empty!");
            return;
        }
        activeServiceUrls.remove(url);
        log.info("checkAndShutDownServices succeed! activeServiceUrls={}", activeServiceUrls);
    }

    @PostConstruct
    public void init() {
        startServiceChecker();
    }
    // 定时更新服务
    private void startServiceChecker() {
        scheduler.scheduleAtFixedRate(this::checkAndUpdateServices, 0, CHECK_INTERVAL, TimeUnit.SECONDS);
    }

    private void checkAndUpdateServices() {
        for (String url : originalServiceUrls) {
            if (isServiceAvailable(url)) {
                if (!activeServiceUrls.contains(url)) {
                    activeServiceUrls.add(url);
                }
            } else {
                activeServiceUrls.remove(url);
            }
        }
        log.info("activeServiceUrls {}", activeServiceUrls);
    }


    private boolean isServiceAvailable(String url) {
        // 这里实现您的逻辑来检查给定的URL是否可用。例如，尝试与服务进行简短的HTTP GET请求。
        // 如果成功返回true，否则返回false。
        // 注意：您可能还想使用timeout来确保检查不会持续太久。
        try {
            restTemplate.getForObject(url, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
