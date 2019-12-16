package com.core.pica.service;

import com.core.pica.model.params.WxSdkConfigParam;
import com.core.pica.model.params.WxSdkConfigResp;

public interface WxPayService {

    public WxSdkConfigResp wxSdkConfig(WxSdkConfigParam param);
}
