package com.example.application.bean;

import lombok.Data;

/**
 * @Author yucheng
 * @Date 2023/10/27 13:20
 * @Version 1.0
 */
@Data
public class RequestDemo {
    String syncId;
    String bizId;
    int startIdx;
    int recordNumber;
}
