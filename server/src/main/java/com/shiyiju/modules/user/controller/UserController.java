package com.shiyiju.modules.user.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.user.dto.UpdateCurrentUserDTO;
import com.shiyiju.modules.user.service.UserService;
import com.shiyiju.modules.user.vo.CurrentUserVO;
import com.shiyiju.modules.user.vo.UserAuthorizationVO;
import com.shiyiju.security.CurrentUserHolder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserVO> me() {
        return ApiResponse.success(userService.getCurrentUser(CurrentUserHolder.get()));
    }

    @GetMapping("/me/authorization")
    public ApiResponse<UserAuthorizationVO> authorization() {
        return ApiResponse.success(userService.getAuthorization(CurrentUserHolder.get()));
    }

    @PutMapping("/me")
    public ApiResponse<CurrentUserVO> updateMe(@Valid @RequestBody UpdateCurrentUserDTO request) {
        return ApiResponse.success("更新成功", userService.updateCurrentUser(CurrentUserHolder.get(), request));
    }
}
