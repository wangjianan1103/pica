package com.core.pica.service.impl;

import com.core.pica.config.PicaException;
import com.core.pica.enums.ErrorCodeEnum;
import com.core.pica.model.params.QueryStatusParam;
import com.core.pica.model.params.QueryStatusResp;
import com.core.pica.model.params.WxSdkConfigParam;
import com.core.pica.model.params.WxSdkConfigResp;
import com.core.pica.model.wx.JsApiTicketResponse;
import com.core.pica.service.WxPayService;
import com.core.pica.service.WxTokenService;
import com.core.pica.service.http.HttpService;
import com.core.pica.util.Utility;
import com.core.pica.util.WxWebUrl;
import com.core.wxpay.config.IWxPayConfig;
import com.core.wxpay.config.JSSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WxPayServiceImpl extends AbstractWxService implements WxPayService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static ConcurrentHashMap<String, String> TICKET_MAP = new ConcurrentHashMap<>();
    private static final String JSAPI_TICKET = "jsapi_ticket";
    private static final String EXPIRES_IN = "expires_in";

    @Autowired
    private IWxPayConfig iWxPayConfig;
    @Autowired
    private WxTokenService wxTokenService;
    @Autowired
    private HttpService httpService;

    @Override
    public WxSdkConfigResp wxSdkConfig(WxSdkConfigParam param) {
        logger.info("wxSdkConfig, start, param={}", param);
        if (param == null) {
            throw new PicaException(ErrorCodeEnum.SYS_PARAMETER_ERROR.getErrorCode());
        }
        WxSdkConfigResp resp = new WxSdkConfigResp();
        Map<String, String> result = JSSign.sign(this.getJSAPITicket(), param.getUrl());

        resp.setAppId(iWxPayConfig.getAppID());
        resp.setNonceStr(result.get("nonceStr"));
        resp.setSignature(result.get("signature"));
        resp.setTimestamp(result.get("timestamp"));
        logger.info("wxSdkConfig, end, resp={}", resp);
        return resp;
    }

    private String getJSAPITicket() {
        String JsApiTicket = null;

        if (TICKET_MAP.get(JSAPI_TICKET) != null && Integer.parseInt(TICKET_MAP.get(EXPIRES_IN)) > Utility.getCurrentTimeStamp()) {
            JsApiTicket = TICKET_MAP.get(JSAPI_TICKET);
            logger.info("getJSAPITicket() end, js_api_ticket from redis, expires_in, accessToken={}", JsApiTicket);
            return JsApiTicket;
        }

        String accessToken = wxTokenService.getWxToken();

        // params
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(WxWebUrl.UrlParam.ACCESS_TOKEN, accessToken);
        JsApiTicketResponse response = null;
        try {
            response = httpService.get(this.fmtReqWebsUrl(WxWebUrl.GET_TICKET, paramMap), JsApiTicketResponse.class);
        } catch (Exception e) {
            logger.error("getJSAPITicket() exception. ", e);
        }

        if (response != null) {
            JsApiTicket = response.getTicket();
            Integer expiresIn = response.getExpires_in();
            TICKET_MAP.put(JSAPI_TICKET, JsApiTicket);
            TICKET_MAP.put(EXPIRES_IN, String.valueOf(expiresIn + Utility.getCurrentTimeStamp() - 600));
        }
        logger.info("getJSAPITicket() end, JsApiTicket={}", JsApiTicket);
        return JsApiTicket;
    }
}
