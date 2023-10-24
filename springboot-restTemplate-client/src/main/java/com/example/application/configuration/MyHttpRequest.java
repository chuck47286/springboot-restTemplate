package com.example.application.configuration;

import com.example.application.config.BizConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;

import java.net.URI;

/**
 * @Author yucheng
 * @Date 2023/10/19 16:48
 * @Version 1.0
 */
public class MyHttpRequest implements HttpRequest {

    private HttpRequest sourceRequest;
    private URI uri;

    public MyHttpRequest (HttpRequest sourceRequest, URI uri) {
        this.sourceRequest = sourceRequest;
        this.uri = uri;
    }

    public HttpHeaders getHeaders() {
        return sourceRequest.getHeaders();
    }
    public HttpMethod getMethod() {
        return sourceRequest.getMethod();
    }
    @Override
    public String getMethodValue() {
        return sourceRequest.getMethodValue();
    }
    /**
     * 将URI转换
     */
    public URI getURI() {
        try {
//            URI newUri = new URI(uri);
//            return newUri;
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sourceRequest.getURI();
    }
}
