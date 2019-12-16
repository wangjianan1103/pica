package com.core.pica.controller;

import com.core.pica.model.params.WxSdkConfigParam;
import com.core.pica.model.params.WxSdkConfigResp;
import com.core.pica.service.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信支付相关接口
 */
@RestController
@RequestMapping(value = "wx")
public class WxPaymentController {

    @Autowired
    private WxPayService wxPayService;

    /**
     * 微信支付配置
     */
    @RequestMapping(value = "getSdkConfig", method = RequestMethod.POST)
    public WxSdkConfigResp getSdkConfig(@RequestBody(required = false) WxSdkConfigParam param) {
        return wxPayService.wxSdkConfig(param);
    }
}
