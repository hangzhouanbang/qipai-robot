package com.anbang.fake.utils;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/14 10:39 AM
 * @Version 1.0
 */
public class HttpUtils {

    private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static final CloseableHttpClient httpclient = HttpClients.createDefault();

    private final static String CHAR_CODE = "UTF-8";

    public static String doGet(String url) throws IOException {
        //从连接池中获取请求client
        CloseableHttpClient httpClient = HttpClientUtils.getHttpClient();
        //组装HttpPost
        HttpGet httpGet = getHttpGet(url);
        httpGet = addHeader(httpGet, url);
        //发起HTTP连接
        CloseableHttpResponse httpResp = httpClient.execute(httpGet);
        //字符串类型的返回结果
        String content = EntityUtils.toString(httpResp.getEntity(), CHAR_CODE);
        //关闭HTTP返回
        httpResp.close();
        return content;
    }

    /**
     * 调用HTTP-POST
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> params) throws IOException {
        return doPost(url, params, StringUtils.EMPTY);
    }

    /**
     * 调用HTTP-POST
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> params, String token) throws IOException {
        //从连接池中获取请求client
        CloseableHttpClient httpClient = HttpClientUtils.getHttpClient();
        //组装HttpPost
        HttpPost httpPost = getHttpPost(url);
        if (StringUtils.isNotEmpty(token)) {
            httpPost.setHeader("token", token);
        } else {
            httpPost = addHeader(httpPost, url);
        }
        List<BasicNameValuePair> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        //整体编码转换
        HttpEntity requestEntity = new UrlEncodedFormEntity(list, CHAR_CODE);
        httpPost.setEntity(requestEntity);
        //发起HTTP连接
        CloseableHttpResponse httpResp = httpClient.execute(httpPost);
        //字符串类型的返回结果
        String content = EntityUtils.toString(httpResp.getEntity(), CHAR_CODE);
        //关闭HTTP返回
        httpResp.close();
        return content;
    }

    private static HttpPost getHttpPost(String url) {
        HttpPost httpPost = new HttpPost(url);
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(30000).build();
        httpPost.setConfig(config);
        return httpPost;
    }

    private static HttpGet getHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(30000).build();
        httpGet.setConfig(config);
        return httpGet;
    }

    private static HttpPost addHeader(HttpPost httpPost, String url) {
        httpPost.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded;CharSet=gbk"));
        httpPost.setHeader(new BasicHeader("UserAgent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36"));
        httpPost.setHeader(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36"));
        httpPost.setHeader(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
        httpPost.setHeader(new BasicHeader("Host", getHost(url)));
        httpPost.setHeader(new BasicHeader("Origin", "https://" + getHost(url)));
        httpPost.setHeader(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        httpPost.setHeader(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
        httpPost.setHeader(new BasicHeader("Connection", "keep-alive"));
        return httpPost;
    }

    private static HttpGet addHeader(HttpGet httpGet, String url) {
        httpGet.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded;CharSet=gbk"));
        httpGet.setHeader(new BasicHeader("UserAgent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36"));
        httpGet.setHeader(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36"));
        httpGet.setHeader(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
        httpGet.setHeader(new BasicHeader("Host", getHost(url)));
        httpGet.setHeader(new BasicHeader("Origin", "https://" + getHost(url)));
        httpGet.setHeader(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        httpGet.setHeader(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
        httpGet.setHeader(new BasicHeader("Connection", "keep-alive"));
        return httpGet;
    }

    private static String getHost(String url) {
        if (StringUtils.isEmpty(url) || !(url.startsWith("http://") || !url.startsWith("https://"))) {
            return "";
        }
        url = url.substring(url.indexOf("://") + 3, url.length());
        if (url.contains("/")) {
            url = url.substring(0, url.indexOf("/"));
        }

        return url;
    }


    /**
     * 发送HttpPost请求，参数为json字符串 * * @param url * @param jsonStr * @return
     */
    public static String sendPost(String url, String jsonStr) {
        String result = null;
        // 字符串编码
        StringEntity entity = new StringEntity(jsonStr, Consts.UTF_8);
        // 设置content-type
        entity.setContentType("application/json");
        HttpPost httpPost = new HttpPost(url);
        // 防止被当成攻击添加的
        httpPost.setHeader("User-Agent", userAgent);
        // 接收参数设置
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            result = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            // 关闭CloseableHttpResponse
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return result;
    }
}
