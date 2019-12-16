package com.core.wxpay.config;

import com.core.wxpay.sdk.IWXPayDomain;
import com.core.wxpay.sdk.WXPayConfig;
import com.core.wxpay.sdk.WXPayConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class IWxPayConfig extends WXPayConfig {

    private byte[] certData;

    @Value("${vendor.wx.mp.app_id}")
    private String app_id;

    @Value("${vendor.wx.pay.key}")
    private String wx_pay_key;

    @Value("${vendor.wx.pay.mch_id}")
    private String wx_pay_mch_id;

    public IWxPayConfig() throws Exception {
        String certPath = "/data/config/pica/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    @Override
    public String getAppID() {
        return app_id;
    }

    @Override
    public String getMchID() {
        return wx_pay_mch_id;
    }

    @Override
    public String getKey() {
        return wx_pay_key;
    }

    @Override
    public InputStream getCertStream() {
        return new ByteArrayInputStream(this.certData);
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        IWXPayDomain iwxPayDomain = new IWXPayDomain() {
            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {

            }
            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new IWXPayDomain.DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        };
        return iwxPayDomain;
    }
}
