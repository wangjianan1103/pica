package com.core.pica.model.params;

import lombok.Data;

@Data
public class WxSdkConfigResp {
    private String nonceStr;
    private String signature;
    private String timestamp;
    private String appId;

}
