package com.core.pica.service.impl;

import com.core.pica.config.PicaException;
import com.core.pica.enums.ErrorCodeEnum;
import com.core.pica.model.params.QueryStatusParam;
import com.core.pica.model.params.QueryStatusResp;
import com.core.pica.model.params.TransactionDoParam;
import com.core.pica.model.params.TransactionDoResp;
import com.core.pica.service.TransactionService;
import com.core.pica.util.Utility;
import com.core.wxpay.config.IWxPayConfig;
import com.core.wxpay.sdk.WXPay;
import com.core.wxpay.sdk.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.core.wxpay.sdk.WXPayConstants.SUCCESS;

@Service
public class TransactionServiceImpl implements TransactionService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${vendor.wx.config.callback}")
    private String callBack;
    @Autowired
    private IWxPayConfig iWxPayConfig;

    @Override
    public TransactionDoResp transactionDo(TransactionDoParam param) {
        TransactionDoResp resp = new TransactionDoResp();
        logger.info("transactionDo start, param={}", param);

        final String openId = "oPg6Q0TE3VitYTroCpxnA89ZBF5M"; // 微信关注openId
        final BigDecimal payAmount = param.getPayAmount();
        final String orderGid = Utility.generateUUID();

        // 发起微信支付
        WXPay wxpay;
        try {
            wxpay = new WXPay(iWxPayConfig);

            /*
             * sp 1. 拼装请求参数
             */
            Map<String, String> data = new HashMap<>();
            data.put("body", "订单基本信息");
            data.put("out_trade_no", orderGid);
            data.put("total_fee", String.valueOf(payAmount.multiply(new BigDecimal(100)).intValue()));
            data.put("spbill_create_ip", "192.168.1.1");
            data.put("openid", openId);
            data.put("notify_url", callBack); // 订单回调接口
            data.put("trade_type", "JSAPI");
            data.put("time_expire", Utility.fmtYmdHms(Utility.getCurrentTimeStamp() + 600));

            /*
             * sp 2. 调用微信支付下单接口
             */
            logger.info("发起微信支付下单接口, request={}", data);
            Map<String, String> response = wxpay.unifiedOrder(data);
            logger.info("微信支付下单成功, 返回值 response={}", response);
            String returnCode = response.get("return_code");
            if (!SUCCESS.equals(returnCode)) {
                return null;
            }
            String resultCode = response.get("result_code");
            if (!SUCCESS.equals(resultCode)) {
                return null;
            }
            String prepay_id = response.get("prepay_id");
            if (prepay_id == null) {
                return null;
            }

            /*
             * sp 3. 进行自己的业务逻辑，保存下单结果
             */

            /*
             * sp 4. 处理下单结果内容，生成前端调起微信支付的sign
             */
            final String nonceStr = Utility.generateUUID();
            final String packages = "prepay_id=" + prepay_id;
            Map<String, String> wxPayMap = new HashMap<>();
            wxPayMap.put("appId", iWxPayConfig.getAppID());
            wxPayMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
            wxPayMap.put("nonceStr", nonceStr);
            wxPayMap.put("package", packages);
            wxPayMap.put("signType", "MD5");
            String sign = WXPayUtil.generateSignature(wxPayMap, iWxPayConfig.getKey());

            resp.setAppId(iWxPayConfig.getAppID());
            resp.setTimeStamp(String.valueOf(System.currentTimeMillis()));
            resp.setNonceStr(nonceStr);
            resp.setSignType("MD5");
            resp.setPrepayId(prepay_id);
            resp.setSign(sign);
        } catch (Exception e) {
            logger.error("transactionDo error", e);
            throw new PicaException(ErrorCodeEnum.SYS_FAIL.getErrorCode(), e.getMessage());
        }
        return resp;
    }

    @Override
    public QueryStatusResp queryStatus(QueryStatusParam param) {
        return new QueryStatusResp();
    }
}
