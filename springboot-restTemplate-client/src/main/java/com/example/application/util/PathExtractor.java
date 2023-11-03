package com.example.application.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author yucheng
 * @Date 2023/10/27 16:08
 * @Version 1.0
 */
public class PathExtractor {
    public static void main(String[] args) {
        String input = "http://10.10.10.1:7080,10.10.10.10:7081/get/request";
        String path = extractPath(input);
        if (path != null) {
            System.out.println("Extracted path: " + path);
        } else {
            System.out.println("No path found.");
        }
    }

    public static String extractPath(String url) {
        // 正则表达式匹配 URL 的结尾部分，即 "path" 部分
        Pattern pattern = Pattern.compile(".*?:\\d+(.+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
