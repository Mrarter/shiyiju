package com.shiyiju.modules.user;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.user.dto.UpdateCurrentUserDTO;
import com.shiyiju.modules.user.entity.UserAccountEntity;
import com.shiyiju.modules.user.mapper.UserAccountMapper;
import com.shiyiju.modules.user.service.UserService;
import com.shiyiju.modules.user.vo.CurrentUserVO;
import com.shiyiju.modules.user.vo.UserAuthorizationVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserAccountMapper userAccountMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldGetCurrentUser() {
        UserAccountEntity entity = new UserAccountEntity();
        entity.setId(1L);
        entity.setUserNo("U001");
        entity.setNickname("测试用户");
        entity.setAvatarUrl("https://example.com/avatar.png");
        entity.setMobile("13800138000");
        entity.setGender(1);
        entity.setStatus("ENABLED");
        entity.setRoleCodes("COLLECTOR,ARTIST");
        when(userAccountMapper.findCurrentUser(1L)).thenReturn(entity);
        when(userAccountMapper.findArtistIdByUserId(1L)).thenReturn(99L);
        when(userAccountMapper.findDistributorIdByUserId(1L)).thenReturn(null);

        CurrentUserVO result = userService.getCurrentUser(1L);

        assertEquals(1L, result.getUserId());
        assertEquals(2, result.getRoles().size());
        assertEquals("13800138000", result.getMobile());
        assertEquals("ARTIST", result.getDefaultRole());
        assertEquals(99L, result.getArtistId());
    }

    @Test
    void shouldUpdateCurrentUser() {
        UserAccountEntity entity = new UserAccountEntity();
        entity.setId(1L);
        entity.setStatus("ENABLED");
        entity.setRoleCodes("COLLECTOR");
        when(userAccountMapper.findCurrentUser(1L)).thenReturn(entity, entity);
        when(userAccountMapper.findArtistIdByUserId(1L)).thenReturn(null);
        when(userAccountMapper.findDistributorIdByUserId(1L)).thenReturn(null);

        UpdateCurrentUserDTO request = new UpdateCurrentUserDTO();
        request.setNickname("新昵称");
        request.setAvatarUrl("https://example.com/new.png");
        request.setMobile("13800138000");
        request.setGender(2);

        userService.updateCurrentUser(1L, request);
        verify(userAccountMapper).updateProfile(1L, "新昵称", "https://example.com/new.png", "13800138000", 2);
    }

    @Test
    void shouldGetAuthorizationSnapshot() {
        UserAccountEntity entity = new UserAccountEntity();
        entity.setId(1L);
        entity.setStatus("ENABLED");
        entity.setRoleCodes("COLLECTOR,DISTRIBUTOR");
        when(userAccountMapper.findCurrentUser(1L)).thenReturn(entity);
        when(userAccountMapper.findArtistIdByUserId(1L)).thenReturn(null);
        when(userAccountMapper.findDistributorIdByUserId(1L)).thenReturn(8L);

        UserAuthorizationVO result = userService.getAuthorization(1L);

        assertEquals("DISTRIBUTOR", result.getDefaultRole());
        assertEquals(8L, result.getDistributorId());
        assertEquals(6, result.getPermissions().size());
    }

    @Test
    void shouldThrowWhenUserMissing() {
        when(userAccountMapper.findCurrentUser(1L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> userService.getCurrentUser(1L));
    }

    @Test
    void shouldThrowWhenUserDisabled() {
        UserAccountEntity entity = new UserAccountEntity();
        entity.setId(1L);
        entity.setStatus("DISABLED");
        when(userAccountMapper.findCurrentUser(1L)).thenReturn(entity);

        assertThrows(BusinessException.class, () -> userService.getCurrentUser(1L));
    }
}
