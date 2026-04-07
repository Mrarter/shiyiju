package com.shiyiju.modules.user.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.user.dto.WxLoginRequestDTO;
import com.shiyiju.modules.user.service.AuthService;
import com.shiyiju.modules.user.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/wx/login")
    public ApiResponse<LoginVO> wxLogin(@Valid @RequestBody WxLoginRequestDTO request) {
        return ApiResponse.success(authService.wxLogin(request));
    }
}
