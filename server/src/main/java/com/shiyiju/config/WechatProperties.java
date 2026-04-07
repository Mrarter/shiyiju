package com.shiyiju.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.wechat")
public class WechatProperties {

    private String miniappAppId;
    private String miniappAppSecret;
    private String code2SessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
    private boolean mockEnabled = true;

    public String getMiniappAppId() {
        return miniappAppId;
    }

    public void setMiniappAppId(String miniappAppId) {
        this.miniappAppId = miniappAppId;
    }

    public String getMiniappAppSecret() {
        return miniappAppSecret;
    }

    public void setMiniappAppSecret(String miniappAppSecret) {
        this.miniappAppSecret = miniappAppSecret;
    }

    public String getCode2SessionUrl() {
        return code2SessionUrl;
    }

    public void setCode2SessionUrl(String code2SessionUrl) {
        this.code2SessionUrl = code2SessionUrl;
    }

    public boolean isMockEnabled() {
        return mockEnabled;
    }

    public void setMockEnabled(boolean mockEnabled) {
        this.mockEnabled = mockEnabled;
    }
}
