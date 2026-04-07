package com.shiyiju.modules.user;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.user.dto.WxLoginRequestDTO;
import com.shiyiju.modules.user.entity.UserAccountEntity;
import com.shiyiju.modules.user.mapper.UserAccountMapper;
import com.shiyiju.modules.user.service.AuthService;
import com.shiyiju.modules.user.service.UserService;
import com.shiyiju.modules.user.service.WechatSessionService;
import com.shiyiju.modules.user.vo.CurrentUserVO;
import com.shiyiju.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private WechatSessionService wechatSessionService;

    @Mock
    private UserAccountMapper userAccountMapper;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterNewWechatUser() {
        WxLoginRequestDTO request = new WxLoginRequestDTO();
        request.setCode("demo-code");
        request.setNickname("新用户");
        request.setAvatarUrl("https://example.com/a.png");
        request.setInviteCode(" INV-001 ");
        request.setGender(2);

        when(wechatSessionService.exchangeCode("demo-code"))
            .thenReturn(new com.shiyiju.modules.user.service.MockWechatService.SessionInfo("openid-001", "unionid-001", "session-001", "wx-app"));
        when(userAccountMapper.findByOpenid("openid-001")).thenReturn(null);
        doAnswer(invocation -> {
            UserAccountEntity entity = invocation.getArgument(0);
            entity.setId(101L);
            return 1;
        }).when(userAccountMapper).insertUserAccount(any(UserAccountEntity.class));
        when(jwtTokenProvider.createToken(101L)).thenReturn("jwt-101");
        when(userService.getCurrentUser(101L)).thenReturn(buildCurrentUser(101L));

        var result = authService.wxLogin(request);

        ArgumentCaptor<UserAccountEntity> captor = ArgumentCaptor.forClass(UserAccountEntity.class);
        verify(userAccountMapper).insertUserAccount(captor.capture());
        assertEquals("新用户", captor.getValue().getNickname());
        assertEquals("INV-001", captor.getValue().getInvitationCodeUsed());
        assertEquals(2, captor.getValue().getGender());
        assertNotNull(captor.getValue().getUserNo());
        verify(userAccountMapper).insertWechatAuth(101L, "openid-001", "unionid-001", "session-001", "wx-app");
        verify(userAccountMapper).insertUserRole(101L, "COLLECTOR");
        assertEquals("jwt-101", result.getToken());
    }

    @Test
    void shouldLoginExistingWechatUser() {
        WxLoginRequestDTO request = new WxLoginRequestDTO();
        request.setCode("demo-code");
        request.setNickname("老用户");
        request.setAvatarUrl("https://example.com/b.png");
        request.setGender(1);

        UserAccountEntity existing = new UserAccountEntity();
        existing.setId(102L);
        existing.setStatus("ENABLED");

        when(wechatSessionService.exchangeCode("demo-code"))
            .thenReturn(new com.shiyiju.modules.user.service.MockWechatService.SessionInfo("openid-002", "unionid-002", "session-002", "wx-app"));
        when(userAccountMapper.findByOpenid("openid-002")).thenReturn(existing);
        when(jwtTokenProvider.createToken(102L)).thenReturn("jwt-102");
        when(userService.getCurrentUser(102L)).thenReturn(buildCurrentUser(102L));

        var result = authService.wxLogin(request);

        verify(userAccountMapper).updateLoginProfile(102L, "老用户", "https://example.com/b.png", 1);
        verify(userAccountMapper).updateWechatAuth(102L, "session-002", "unionid-002");
        verify(userAccountMapper).insertUserRole(102L, "COLLECTOR");
        assertEquals("jwt-102", result.getToken());
    }

    @Test
    void shouldRejectDisabledUserLogin() {
        WxLoginRequestDTO request = new WxLoginRequestDTO();
        request.setCode("demo-code");

        UserAccountEntity existing = new UserAccountEntity();
        existing.setId(103L);
        existing.setStatus("DISABLED");

        when(wechatSessionService.exchangeCode("demo-code"))
            .thenReturn(new com.shiyiju.modules.user.service.MockWechatService.SessionInfo("openid-003", "unionid-003", "session-003", "wx-app"));
        when(userAccountMapper.findByOpenid("openid-003")).thenReturn(existing);

        assertThrows(BusinessException.class, () -> authService.wxLogin(request));
        verify(userAccountMapper, never()).updateWechatAuth(anyLong(), any(), any());
    }

    @Test
    void shouldRejectInvalidGender() {
        WxLoginRequestDTO request = new WxLoginRequestDTO();
        request.setCode("demo-code");
        request.setGender(3);

        assertThrows(IllegalArgumentException.class, () -> authService.wxLogin(request));
        verify(wechatSessionService, never()).exchangeCode(any());
    }

    private CurrentUserVO buildCurrentUser(Long userId) {
        CurrentUserVO user = new CurrentUserVO();
        user.setUserId(userId);
        user.setUserNo("U" + userId);
        user.setNickname("测试用户");
        user.setStatus("ENABLED");
        user.setRoles(List.of("COLLECTOR"));
        user.setDefaultRole("COLLECTOR");
        return user;
    }
}
