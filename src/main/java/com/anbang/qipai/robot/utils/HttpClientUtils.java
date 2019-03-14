package com.anbang.qipai.robot.utils;

import org.apache.http.impl.client.CloseableHttpClient;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 1:04 PM
 * @Version 1.0
 */
public class HttpClientUtils {
    private final static HttpClientPool httpClientPool;

    static {
        httpClientPool = new HttpClientPool();
    }

    public static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = null;
        synchronized (httpClientPool) {
            httpClient = httpClientPool.getClient();
        }
        return httpClient;
    }
}
