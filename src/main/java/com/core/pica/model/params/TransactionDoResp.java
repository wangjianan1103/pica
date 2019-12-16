package com.core.pica.model.params;

import lombok.Data;

@Data
public class TransactionDoResp {
    private String prepayId;
    private String sign;
    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String signType;

}
