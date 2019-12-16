package com.core.pica.config;

import com.core.pica.enums.ErrorCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;

/**
 * controller 增强器
 */
@RestControllerAdvice
public class PicaControllerAdvice implements ResponseBodyAdvice<Object> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 全局异常捕捉处理
     *
     * @param ex 系统异常
     * @return 统一返回内容数据
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseInfo errorHandler(Exception ex) {
        logger.error("errorHandler, exception", ex);
        return new ResponseInfo(ErrorCodeEnum.SYS_FAIL.getErrorCode(), ex.getMessage());
    }

    /**
     * 拦截捕捉自定义异常 PicaException.class
     * @param ex 自定义异常
     * @return 统一返回内容数据
     */
    @ResponseBody
    @ExceptionHandler(value = PicaException.class)
    public ResponseInfo picaErrorHandler(PicaException ex) {
        return new ResponseInfo(ex.getStatus(), ErrorCodeEnum.of(ex.getStatus()).getMessage());
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 统一接口返回对象
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Resource) {
            return body;
        }
        if (body instanceof ResponseInfo) {
            return body;
        }
        return new ResponseInfo(ErrorCodeEnum.SYS_SUCCESS, body);
    }
}
