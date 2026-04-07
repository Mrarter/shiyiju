package com.shiyiju.modules.admin.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.admin.service.AdminContentService;
import com.shiyiju.modules.admin.vo.AdminArtistVO;
import com.shiyiju.modules.admin.vo.AdminArtworkVO;
import com.shiyiju.modules.admin.vo.AdminOperationVO;
import com.shiyiju.modules.admin.vo.AdminOrderVO;
import com.shiyiju.modules.admin.vo.AdminUserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/v1")
public class AdminContentController {

    private final AdminContentService adminContentService;

    public AdminContentController(AdminContentService adminContentService) {
        this.adminContentService = adminContentService;
    }

    @GetMapping("/operations")
    public ApiResponse<List<AdminOperationVO>> operations() {
        return ApiResponse.success(adminContentService.listOperations());
    }

    @GetMapping("/artists")
    public ApiResponse<List<AdminArtistVO>> artists() {
        return ApiResponse.success(adminContentService.listArtists());
    }

    @GetMapping("/artworks")
    public ApiResponse<List<AdminArtworkVO>> artworks() {
        return ApiResponse.success(adminContentService.listArtworks());
    }

    @GetMapping("/users")
    public ApiResponse<List<AdminUserVO>> users() {
        return ApiResponse.success(adminContentService.listUsers());
    }

    @GetMapping("/orders")
    public ApiResponse<List<AdminOrderVO>> orders() {
        return ApiResponse.success(adminContentService.listOrders());
    }
}
