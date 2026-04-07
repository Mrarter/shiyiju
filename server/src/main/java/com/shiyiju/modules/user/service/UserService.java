package com.shiyiju.modules.user.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.user.dto.UpdateCurrentUserDTO;
import com.shiyiju.modules.user.entity.UserAccountEntity;
import com.shiyiju.modules.user.mapper.UserAccountMapper;
import com.shiyiju.modules.user.vo.CurrentUserVO;
import com.shiyiju.modules.user.vo.UserAuthorizationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserAccountMapper userAccountMapper;

    public UserService(UserAccountMapper userAccountMapper) {
        this.userAccountMapper = userAccountMapper;
    }

    public CurrentUserVO getCurrentUser(Long userId) {
        UserAccountEntity entity = userAccountMapper.findCurrentUser(userId);
        if (entity == null) {
            throw new BusinessException(40001, "用户不存在");
        }
        validateUserStatus(entity);
        return toCurrentUser(entity);
    }

    @Transactional
    public CurrentUserVO updateCurrentUser(Long userId, UpdateCurrentUserDTO request) {
        if (userAccountMapper.findCurrentUser(userId) == null) {
            throw new BusinessException(40001, "用户不存在");
        }
        userAccountMapper.updateProfile(
            userId,
            request.getNickname().trim(),
            StringUtils.hasText(request.getAvatarUrl()) ? request.getAvatarUrl().trim() : null,
            StringUtils.hasText(request.getMobile()) ? request.getMobile().trim() : null,
            request.getGender()
        );
        return getCurrentUser(userId);
    }

    public CurrentUserVO toCurrentUser(UserAccountEntity entity) {
        List<String> roles = parseRoles(entity.getRoleCodes());
        CurrentUserVO vo = new CurrentUserVO();
        vo.setUserId(entity.getId());
        vo.setUserNo(entity.getUserNo());
        vo.setNickname(entity.getNickname());
        vo.setAvatarUrl(entity.getAvatarUrl());
        vo.setMobile(entity.getMobile());
        vo.setGender(entity.getGender());
        vo.setStatus(entity.getStatus());
        vo.setRegisterSource(entity.getRegisterSource());
        vo.setInvitationCodeUsed(entity.getInvitationCodeUsed());
        vo.setRoles(roles);
        vo.setDefaultRole(resolveDefaultRole(roles));
        vo.setPermissions(resolvePermissions(roles));
        vo.setArtistId(userAccountMapper.findArtistIdByUserId(entity.getId()));
        vo.setDistributorId(userAccountMapper.findDistributorIdByUserId(entity.getId()));
        return vo;
    }

    public UserAuthorizationVO getAuthorization(Long userId) {
        CurrentUserVO currentUser = getCurrentUser(userId);
        UserAuthorizationVO vo = new UserAuthorizationVO();
        vo.setUserId(currentUser.getUserId());
        vo.setStatus(currentUser.getStatus());
        vo.setDefaultRole(currentUser.getDefaultRole());
        vo.setRoles(currentUser.getRoles());
        vo.setPermissions(currentUser.getPermissions());
        vo.setArtistId(currentUser.getArtistId());
        vo.setDistributorId(currentUser.getDistributorId());
        return vo;
    }

    private void validateUserStatus(UserAccountEntity entity) {
        if (!"ENABLED".equals(entity.getStatus())) {
            throw new BusinessException(40003, "用户状态异常");
        }
    }

    private java.util.List<String> parseRoles(String roleCodes) {
        if (!StringUtils.hasText(roleCodes)) {
            return Collections.singletonList("COLLECTOR");
        }
        return Arrays.stream(roleCodes.split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .toList();
    }

    private String resolveDefaultRole(List<String> roles) {
        if (roles.contains("ARTIST")) {
            return "ARTIST";
        }
        if (roles.contains("DISTRIBUTOR")) {
            return "DISTRIBUTOR";
        }
        if (roles.contains("ADMIN")) {
            return "ADMIN";
        }
        return "COLLECTOR";
    }

    private List<String> resolvePermissions(List<String> roles) {
        Set<String> permissions = new LinkedHashSet<>();
        permissions.add("PROFILE_EDIT");
        permissions.add("WORK_VIEW");
        permissions.add("ORDER_VIEW");
        if (roles.contains("COLLECTOR")) {
            permissions.add("ORDER_CREATE");
            permissions.add("COLLECTION_VIEW");
        }
        if (roles.contains("ARTIST")) {
            permissions.add("ARTIST_HOME_VIEW");
            permissions.add("ARTWORK_PUBLISH");
        }
        if (roles.contains("DISTRIBUTOR")) {
            permissions.add("DISTRIBUTOR_CENTER_VIEW");
        }
        if (roles.contains("ADMIN")) {
            permissions.add("ADMIN_CONSOLE");
        }
        return permissions.stream().toList();
    }
}
