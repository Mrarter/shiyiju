package com.shiyiju.modules.admin.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.admin.dto.AdminLoginRequestDTO;
import com.shiyiju.modules.admin.vo.AdminLoginUserVO;
import com.shiyiju.modules.admin.vo.AdminLoginVO;
import com.shiyiju.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService {

    private final JwtTokenProvider jwtTokenProvider;

    public AdminAuthService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AdminLoginVO login(AdminLoginRequestDTO request) {
        if (!"admin".equals(request.getUsername()) || !"123456".equals(request.getPassword())) {
            throw new BusinessException(40101, "账号或密码错误");
        }

        AdminLoginUserVO user = new AdminLoginUserVO();
        user.setId(900001L);
        user.setUsername("admin");
        user.setRole("SUPER_ADMIN");
        user.setDisplayName("超级管理员");

        AdminLoginVO result = new AdminLoginVO();
        result.setToken(jwtTokenProvider.createToken(user.getId()));
        result.setUser(user);
        return result;
    }

    public AdminLoginUserVO currentUser() {
        AdminLoginUserVO user = new AdminLoginUserVO();
        user.setId(900001L);
        user.setUsername("admin");
        user.setRole("SUPER_ADMIN");
        user.setDisplayName("超级管理员");
        return user;
    }
}
