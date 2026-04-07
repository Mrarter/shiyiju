package com.shiyiju.modules.admin.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.admin.service.AdminDashboardService;
import com.shiyiju.modules.admin.vo.AdminDashboardVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/v1/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping
    public ApiResponse<AdminDashboardVO> dashboard() {
        return ApiResponse.success(adminDashboardService.getDashboard());
    }
}
