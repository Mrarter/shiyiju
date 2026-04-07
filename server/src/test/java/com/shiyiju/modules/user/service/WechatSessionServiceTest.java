package com.shiyiju.modules.user.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.config.WechatProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WechatSessionServiceTest {

    @Mock
    private MockWechatService mockWechatService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void shouldFallbackToMockWhenCredentialMissing() {
        WechatProperties properties = new WechatProperties();
        properties.setMiniappAppId("wx-demo");
        properties.setMockEnabled(true);
        when(mockWechatService.exchangeCode("demo-code"))
            .thenReturn(new MockWechatService.SessionInfo("openid-1", "unionid-1", "session-1", "wx-demo"));

        WechatSessionService service = new WechatSessionService(properties, mockWechatService, restTemplate);

        MockWechatService.SessionInfo result = service.exchangeCode("demo-code");

        assertEquals("openid-1", result.openid());
        verify(restTemplate, never()).getForEntity(anyString(), eq(WechatSessionService.WechatCode2SessionResponse.class));
    }

    @Test
    void shouldUseRealWechatResponseWhenCredentialProvided() {
        WechatProperties properties = new WechatProperties();
        properties.setMiniappAppId("wx-demo");
        properties.setMiniappAppSecret("secret-demo");
        properties.setMockEnabled(false);
        when(restTemplate.getForEntity(anyString(), eq(WechatSessionService.WechatCode2SessionResponse.class)))
            .thenReturn(new ResponseEntity<>(
                new WechatSessionService.WechatCode2SessionResponse("openid-2", "unionid-2", "session-2", 0, "ok"),
                HttpStatus.OK
            ));

        WechatSessionService service = new WechatSessionService(properties, mockWechatService, restTemplate);

        MockWechatService.SessionInfo result = service.exchangeCode("real-code");

        assertEquals("openid-2", result.openid());
        assertEquals("session-2", result.sessionKey());
    }

    @Test
    void shouldThrowWhenWechatReturnsError() {
        WechatProperties properties = new WechatProperties();
        properties.setMiniappAppId("wx-demo");
        properties.setMiniappAppSecret("secret-demo");
        properties.setMockEnabled(false);
        when(restTemplate.getForEntity(anyString(), eq(WechatSessionService.WechatCode2SessionResponse.class)))
            .thenReturn(new ResponseEntity<>(
                new WechatSessionService.WechatCode2SessionResponse(null, null, null, 40029, "invalid code"),
                HttpStatus.OK
            ));

        WechatSessionService service = new WechatSessionService(properties, mockWechatService, restTemplate);

        assertThrows(BusinessException.class, () -> service.exchangeCode("bad-code"));
    }
}
