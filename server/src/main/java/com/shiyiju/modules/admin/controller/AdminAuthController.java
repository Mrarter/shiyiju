package com.shiyiju.modules.admin.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.admin.dto.AdminLoginRequestDTO;
import com.shiyiju.modules.admin.service.AdminAuthService;
import com.shiyiju.modules.admin.vo.AdminLoginUserVO;
import com.shiyiju.modules.admin.vo.AdminLoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/v1/auth")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public ApiResponse<AdminLoginVO> login(@Valid @RequestBody AdminLoginRequestDTO request) {
        return ApiResponse.success(adminAuthService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<AdminLoginUserVO> me() {
        return ApiResponse.success(adminAuthService.currentUser());
    }
}
