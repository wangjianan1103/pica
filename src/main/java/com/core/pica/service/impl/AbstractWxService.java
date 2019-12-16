package com.core.pica.service.impl;

import com.core.pica.service.WxTokenService;
import com.core.pica.util.Utility;
import com.core.pica.util.WxWebUrl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractWxService {

    @Autowired
    private WxTokenService wxTokenService;

    public String fmtReqWebsUrl(WxWebUrl websUrl, Map<String, String> urlParamMap) {
        String url = websUrl.getUrl();
        if (urlParamMap == null) {
            urlParamMap = new HashMap<>();
        }
        // replace URL param
        if (url.contains(WxWebUrl.UrlParam.ACCESS_TOKEN)) {
            // get access token
            String acToken = wxTokenService.getWxToken();
            if (Utility.isBlank(acToken)) {
                throw new IllegalStateException("access token invalid.");
            }
            urlParamMap.put(WxWebUrl.UrlParam.ACCESS_TOKEN, acToken);
        }
        for (Map.Entry<String, String> entry : urlParamMap.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if (Utility.isNotBlank(key) && val != null) {
                url = url.replace(key, val);
            }
        }
        return url;
    }
}
