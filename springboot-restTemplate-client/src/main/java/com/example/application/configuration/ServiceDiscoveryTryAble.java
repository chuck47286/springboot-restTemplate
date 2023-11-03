package com.example.application.configuration;

import cn.hutool.core.util.StrUtil;
import com.example.application.config.BizConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 應該是不需要的存在
 *
 *
 *
 * @Author yucheng
 * @Date 2023/10/25 10:52
 * @Version 1.0
 */
@Slf4j
@Component
public class ServiceDiscoveryTryAble {

    private List<String> originalServiceUrls; // 所有的原始服务URLs

    public ServiceDiscoveryTryAble(BizConfig bizConfig) {
        this.originalServiceUrls = loadServiceAddresses(bizConfig);
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
        if (originalServiceUrls.isEmpty()) {
            return null; // 或者抛出异常，取决于您的需求
        }
//        int index = currentIndex.getAndIncrement() % activeServiceUrls.size();
        int index = (int)(Math.random() * originalServiceUrls.size());
        return originalServiceUrls.get(index);
    }

}
