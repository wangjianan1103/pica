package com.core.pica.service.http;

import java.util.Map;

public interface HttpService {

    <T> T get(String url, Class<T> clazz) throws Exception;

    <T> T get(String url, Map<String, String> headerMap, Class<T> clazz) throws Exception;

    <T> T post(String url, Map<String, String> params, Map<String, String> headerMap, Class<T> clazz) throws Exception;

    <T> T post(String url, Map<String, String> params, Class<T> clazz) throws Exception;

    <T> T post(String url, String params, Map<String, String> headerMap, Class<T> clazz) throws Exception;

    <T> T post(String url, String params, Class<T> clazz) throws Exception;
}
