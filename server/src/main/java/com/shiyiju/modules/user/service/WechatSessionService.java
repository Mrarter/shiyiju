package com.shiyiju.modules.user.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.config.WechatProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Service
public class WechatSessionService {

    private final WechatProperties wechatProperties;
    private final MockWechatService mockWechatService;
    private final RestTemplate restTemplate;

    @Autowired
    public WechatSessionService(WechatProperties wechatProperties,
                                MockWechatService mockWechatService,
                                RestTemplateBuilder restTemplateBuilder) {
        this(wechatProperties, mockWechatService, restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(5))
            .build());
    }

    WechatSessionService(WechatProperties wechatProperties,
                         MockWechatService mockWechatService,
                         RestTemplate restTemplate) {
        this.wechatProperties = wechatProperties;
        this.mockWechatService = mockWechatService;
        this.restTemplate = restTemplate;
    }

    public MockWechatService.SessionInfo exchangeCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new IllegalArgumentException("wechat code must not be blank");
        }
        if (!hasRealWechatCredential()) {
            if (wechatProperties.isMockEnabled()) {
                return mockWechatService.exchangeCode(code);
            }
            throw new BusinessException(50001, "微信登录配置缺失");
        }
        try {
            ResponseEntity<WechatCode2SessionResponse> response = restTemplate.getForEntity(buildCode2SessionUrl(code),
                WechatCode2SessionResponse.class);
            WechatCode2SessionResponse body = response.getBody();
            if (body == null) {
                throw new BusinessException(50002, "微信登录响应为空");
            }
            if (body.errcode() != null && body.errcode() != 0) {
                throw new BusinessException(40002, "微信登录失败: " + body.errmsg());
            }
            if (!StringUtils.hasText(body.openid()) || !StringUtils.hasText(body.sessionKey())) {
                throw new BusinessException(50002, "微信登录返回数据不完整");
            }
            return new MockWechatService.SessionInfo(
                body.openid(),
                body.unionid(),
                body.sessionKey(),
                wechatProperties.getMiniappAppId()
            );
        } catch (RestClientException exception) {
            throw new BusinessException(50002, "微信登录服务调用失败");
        }
    }

    private boolean hasRealWechatCredential() {
        return StringUtils.hasText(wechatProperties.getMiniappAppId())
            && StringUtils.hasText(wechatProperties.getMiniappAppSecret());
    }

    private String buildCode2SessionUrl(String code) {
        return UriComponentsBuilder.fromHttpUrl(wechatProperties.getCode2SessionUrl())
            .queryParam("appid", wechatProperties.getMiniappAppId())
            .queryParam("secret", wechatProperties.getMiniappAppSecret())
            .queryParam("js_code", code.trim())
            .queryParam("grant_type", "authorization_code")
            .build(true)
            .toUriString();
    }

    record WechatCode2SessionResponse(
        String openid,
        String unionid,
        @JsonProperty("session_key") String sessionKey,
        Integer errcode,
        String errmsg
    ) {
    }
}
