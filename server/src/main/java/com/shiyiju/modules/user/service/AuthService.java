package com.shiyiju.modules.user.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.user.dto.WxLoginRequestDTO;
import com.shiyiju.modules.user.entity.UserAccountEntity;
import com.shiyiju.modules.user.mapper.UserAccountMapper;
import com.shiyiju.modules.user.vo.LoginVO;
import com.shiyiju.security.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {

    private final WechatSessionService wechatSessionService;
    private final UserAccountMapper userAccountMapper;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(WechatSessionService wechatSessionService,
                       UserAccountMapper userAccountMapper,
                       UserService userService,
                       JwtTokenProvider jwtTokenProvider) {
        this.wechatSessionService = wechatSessionService;
        this.userAccountMapper = userAccountMapper;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public LoginVO wxLogin(WxLoginRequestDTO request) {
        Integer gender = normalizeGender(request.getGender());
        MockWechatService.SessionInfo sessionInfo = wechatSessionService.exchangeCode(request.getCode().trim());
        UserAccountEntity existing = userAccountMapper.findByOpenid(sessionInfo.openid());

        Long userId;
        String nickname = StringUtils.hasText(request.getNickname()) ? request.getNickname().trim() : "微信用户";
        String avatarUrl = StringUtils.hasText(request.getAvatarUrl()) ? request.getAvatarUrl().trim() : null;

        if (existing == null) {
            UserAccountEntity entity = new UserAccountEntity();
            entity.setUserNo(generateUserNo());
            entity.setNickname(nickname);
            entity.setAvatarUrl(avatarUrl);
            entity.setGender(gender);
            entity.setStatus("ENABLED");
            entity.setRegisterSource("WECHAT_MINIAPP");
            entity.setInvitationCodeUsed(trimToNull(request.getInviteCode()));
            entity.setLastLoginAt(LocalDateTime.now());
            userAccountMapper.insertUserAccount(entity);
            userAccountMapper.insertWechatAuth(entity.getId(), sessionInfo.openid(), sessionInfo.unionid(), sessionInfo.sessionKey(), sessionInfo.appId());
            userAccountMapper.insertUserRole(entity.getId(), "COLLECTOR");
            userId = entity.getId();
        } else {
            if (!"ENABLED".equals(existing.getStatus())) {
                throw new BusinessException(40003, "用户状态异常");
            }
            userAccountMapper.updateLoginProfile(existing.getId(), nickname, avatarUrl, gender);
            userAccountMapper.updateWechatAuth(existing.getId(), sessionInfo.sessionKey(), sessionInfo.unionid());
            userAccountMapper.insertUserRole(existing.getId(), "COLLECTOR");
            userId = existing.getId();
        }

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(jwtTokenProvider.createToken(userId));
        loginVO.setUser(userService.getCurrentUser(userId));
        return loginVO;
    }

    private String generateUserNo() {
        return "U" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
            + ThreadLocalRandom.current().nextInt(100, 1000);
    }

    private Integer normalizeGender(Integer gender) {
        if (gender == null) {
            return null;
        }
        if (gender < 0 || gender > 2) {
            throw new IllegalArgumentException("gender is invalid");
        }
        return gender;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
