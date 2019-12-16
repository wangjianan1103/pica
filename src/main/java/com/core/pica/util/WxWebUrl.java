package com.core.pica.util;

public enum WxWebUrl {

    GET_WX_TOKEN("获取access_token", "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APP_ID&secret=APP_SECRET"),

    GET_EX_USER_INFO("获取用户信息", "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN"),

    GET_WX_O_AUTH_2("微信o_auth2授权", "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APP_ID&secret=APP_SECRET&code=CODE&grant_type=authorization_code"),

    GET_TICKET("权限签名算法 jsapi_ticket", "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi"),

    POST_SEND_MESSAGE("发送模板消息", "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN");


    WxWebUrl(String text, String url) {
        this.text = text;
        this.url = url;
    }
    private String text;
    private String url;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public interface UrlParam {
        String APP_ID = "APP_ID";

        String APP_SECRET = "APP_SECRET";

        String ACCESS_TOKEN = "ACCESS_TOKEN";

        String OPENID = "OPENID";

        String CODE = "CODE";
    }

}
