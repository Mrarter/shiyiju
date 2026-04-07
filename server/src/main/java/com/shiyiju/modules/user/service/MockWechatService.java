package com.shiyiju.modules.user.service;

import com.shiyiju.config.WechatProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Service
public class MockWechatService {

    private final WechatProperties wechatProperties;

    public MockWechatService(WechatProperties wechatProperties) {
        this.wechatProperties = wechatProperties;
    }

    public SessionInfo exchangeCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new IllegalArgumentException("wechat code must not be blank");
        }
        String digest = DigestUtils.md5DigestAsHex(code.getBytes(StandardCharsets.UTF_8));
        return new SessionInfo(
            "mock-openid-" + digest.substring(0, 16),
            "mock-unionid-" + digest.substring(0, 16),
            "mock-session-" + digest.substring(0, 16),
            wechatProperties.getMiniappAppId()
        );
    }

    public record SessionInfo(String openid, String unionid, String sessionKey, String appId) {}
}
