package com.core.pica.service.http.impl;

import com.core.pica.enums.ContentTypeEnum;
import com.core.pica.service.http.HttpService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HttpServiceImpl implements HttpService {
    private final static Logger logger = LoggerFactory.getLogger(HttpServiceImpl.class);
    private CloseableHttpClient sslHttpClient;

    public HttpServiceImpl() {
        // set config
        int connTimeoutMs = 5000;
        int connRequestTimeoutMs = 5000;
        int soTimeoutMs = 20000;
        int maxPerRoute = 10;
        int maxTotal = 100;
        // HTTP
        this.loadSSLHttp(soTimeoutMs, connTimeoutMs, connRequestTimeoutMs, maxPerRoute, maxTotal);
    }

    private void loadSSLHttp(int soTimeoutMs, int connTimeoutMs, int connRequestTimeoutMs, int maxPerRoute, int maxTotal) {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            //不进行主机名验证
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(),
                    NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslConnectionSocketFactory)
                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(100);
            sslHttpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .setDefaultCookieStore(new BasicCookieStore())
                    .setDefaultRequestConfig(getRequestConfig(soTimeoutMs, connTimeoutMs, connRequestTimeoutMs))
                    .setConnectionManager(cm).build();
        } catch (Exception e) {
            throw new IllegalStateException("Load HTTP async HttpClient is fail. \n", e);
        }

    }

    /**
     * Get RequestConfig
     */
    private RequestConfig getRequestConfig(int soTimeoutMs, int connTimeoutMs, int connRequestTimeoutMs) {
        return RequestConfig.custom()
                .setSocketTimeout(soTimeoutMs)
                .setConnectTimeout(connTimeoutMs)
                .setConnectionRequestTimeout(connRequestTimeoutMs)
                .build();
    }

    @Override
    public <T> T get(String url, Class<T> clazz) throws Exception {
        return this.get(url, null, clazz);
    }

    @Override
    public <T> T get(String url, Map<String, String> headerMap, Class<T> clazz) throws Exception {
        logger.debug("http get, url={}", url);
        String responseBody = null;
        HttpGet httpGet = new HttpGet(url);

        if (headerMap != null && !headerMap.isEmpty()) {
            for (Map.Entry<String, String> _entry : headerMap.entrySet()) {
                httpGet.setHeader(_entry.getKey(), _entry.getValue());
            }
        }
        HttpResponse response = sslHttpClient.execute(httpGet);
        logger.info("get() http get response={}", response.getStatusLine());
        int status = response.getStatusLine().getStatusCode();
        if (status == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            responseBody = EntityUtils.toString(entity);
            logger.info("get() http get success responseBody={}", responseBody);
            return convertJson(String.valueOf(responseBody), clazz);
        }
        return null;
    }

    @Override
    public <T> T post(String url, Map<String, String> paramMap, Map<String, String> headerMap, Class<T> clazz) throws Exception {
        logger.debug("http post, url={}", url);
        String responseBody = null;

        AbstractHttpEntity httpEntity = null;
        if (paramMap != null && !paramMap.isEmpty()) {
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> _entry : paramMap.entrySet()) {
                formParams.add(new BasicNameValuePair(_entry.getKey(), _entry.getValue()));
            }
            httpEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
        }

        HttpPost httpPost = new HttpPost(url);
        if (httpEntity != null) {
            httpPost.setEntity(httpEntity);
        }
        ContentTypeEnum contentType = null;
        if (headerMap != null && !headerMap.isEmpty()) {
            for (Map.Entry<String, String> _entry : headerMap.entrySet()) {
                httpPost.setHeader(_entry.getKey(), _entry.getValue());
            }
        } else {
            contentType = ContentTypeEnum.HTML;
        }

        if (contentType != null) {
            httpPost.setHeader(HttpHeaders.ACCEPT, contentType.getValue());
            httpPost.setHeader(HTTP.CONTENT_TYPE, contentType.getValue() + ";charset=UTF-8");
        }

        HttpResponse response = sslHttpClient.execute(httpPost);
        logger.info("http post response={}", response.getStatusLine());

        int status = response.getStatusLine().getStatusCode();
        if (status == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            responseBody = EntityUtils.toString(entity);
            return convertJson(String.valueOf(responseBody), clazz);
        }
        return null;
    }

    @Override
    public <T> T post(String url, Map<String, String> params, Class<T> clazz) throws Exception {
        return this.post(url, params, null, clazz);
    }

    @Override
    public <T> T post(String url, String params, Map<String, String> headerMap, Class<T> clazz) throws Exception {
        logger.debug("http post, url={}", url);
        String responseBody = null;

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(params, "UTF-8"));
        ContentTypeEnum contentType = null;
        if (headerMap != null && !headerMap.isEmpty()) {
            for (Map.Entry<String, String> _entry : headerMap.entrySet()) {
                httpPost.setHeader(_entry.getKey(), _entry.getValue());
            }
        } else {
            contentType = ContentTypeEnum.HTML;
        }

        if (contentType != null) {
            httpPost.setHeader(HttpHeaders.ACCEPT, contentType.getValue());
            httpPost.setHeader(HTTP.CONTENT_TYPE, contentType.getValue() + ";charset=UTF-8");
        }

        HttpResponse response = sslHttpClient.execute(httpPost);
        logger.info("http post response={}", response.getStatusLine());

        int status = response.getStatusLine().getStatusCode();
        if (status == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            responseBody = EntityUtils.toString(entity);
            return convertJson(String.valueOf(responseBody), clazz);
        }
        return null;
    }

    @Override
    public <T> T post(String url, String params, Class<T> clazz) throws Exception {
        return this.post(url, params, null, clazz);
    }

    private static <T> T convertJson(String json, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
