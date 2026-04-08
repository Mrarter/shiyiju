package com.shiyiju.modules.admin.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.admin.dto.AdminOperationSaveDTO;
import com.shiyiju.modules.admin.dto.AdminArtistSaveDTO;
import com.shiyiju.modules.admin.dto.AdminArtworkSaveDTO;
import com.shiyiju.modules.admin.dto.AdminRemarkUpdateDTO;
import com.shiyiju.modules.admin.dto.AdminShipmentUpdateDTO;
import com.shiyiju.modules.admin.dto.AdminStatusUpdateDTO;
import com.shiyiju.modules.admin.service.AdminContentService;
import com.shiyiju.modules.admin.vo.AdminArtistVO;
import com.shiyiju.modules.admin.vo.AdminArtworkVO;
import com.shiyiju.modules.admin.vo.AdminOperationVO;
import com.shiyiju.modules.admin.vo.AdminOrderVO;
import com.shiyiju.modules.admin.vo.AdminUserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/operations")
    public ApiResponse<AdminOperationVO> createOperation(@Valid @RequestBody AdminOperationSaveDTO request) {
        return ApiResponse.success("创建成功", adminContentService.createOperation(request));
    }

    @PutMapping("/operations/{id}")
    public ApiResponse<AdminOperationVO> updateOperation(@PathVariable Long id, @Valid @RequestBody AdminOperationSaveDTO request) {
        return ApiResponse.success("更新成功", adminContentService.updateOperation(id, request));
    }

    @PutMapping("/operations/{id}/status")
    public ApiResponse<Void> updateOperationStatus(@PathVariable Long id, @Valid @RequestBody AdminStatusUpdateDTO request) {
        adminContentService.updateOperationStatus(id, request.getStatus());
        return ApiResponse.success("更新成功", null);
    }

    @GetMapping("/artists")
    public ApiResponse<List<AdminArtistVO>> artists() {
        return ApiResponse.success(adminContentService.listArtists());
    }

    @PostMapping("/artists")
    public ApiResponse<AdminArtistVO> createArtist(@Valid @RequestBody AdminArtistSaveDTO request) {
        return ApiResponse.success("创建成功", adminContentService.createArtist(request));
    }

    @PutMapping("/artists/{id}")
    public ApiResponse<AdminArtistVO> updateArtist(@PathVariable Long id, @Valid @RequestBody AdminArtistSaveDTO request) {
        return ApiResponse.success("更新成功", adminContentService.updateArtist(id, request));
    }

    @PutMapping("/artists/{id}/status")
    public ApiResponse<Void> updateArtistStatus(@PathVariable Long id, @Valid @RequestBody AdminStatusUpdateDTO request) {
        adminContentService.updateArtistStatus(id, request.getStatus());
        return ApiResponse.success("更新成功", null);
    }

    @GetMapping("/artworks")
    public ApiResponse<List<AdminArtworkVO>> artworks() {
        return ApiResponse.success(adminContentService.listArtworks());
    }

    @PostMapping("/artworks")
    public ApiResponse<AdminArtworkVO> createArtwork(@Valid @RequestBody AdminArtworkSaveDTO request) {
        return ApiResponse.success("创建成功", adminContentService.createArtwork(request));
    }

    @PutMapping("/artworks/{id}")
    public ApiResponse<AdminArtworkVO> updateArtwork(@PathVariable Long id, @Valid @RequestBody AdminArtworkSaveDTO request) {
        return ApiResponse.success("更新成功", adminContentService.updateArtwork(id, request));
    }

    @PutMapping("/artworks/{id}/status")
    public ApiResponse<Void> updateArtworkStatus(@PathVariable Long id, @Valid @RequestBody AdminStatusUpdateDTO request) {
        adminContentService.updateArtworkStatus(id, request.getStatus());
        return ApiResponse.success("更新成功", null);
    }

    @GetMapping("/users")
    public ApiResponse<List<AdminUserVO>> users() {
        return ApiResponse.success(adminContentService.listUsers());
    }

    @GetMapping("/orders")
    public ApiResponse<List<AdminOrderVO>> orders() {
        return ApiResponse.success(adminContentService.listOrders());
    }

    @PutMapping("/orders/{id}/shipment")
    public ApiResponse<Void> updateOrderShipment(@PathVariable Long id, @Valid @RequestBody AdminShipmentUpdateDTO request) {
        adminContentService.updateOrderShipment(id, request);
        return ApiResponse.success("更新成功", null);
    }

    @PutMapping("/orders/{id}/remark")
    public ApiResponse<Void> updateOrderRemark(@PathVariable Long id, @Valid @RequestBody AdminRemarkUpdateDTO request) {
        adminContentService.updateOrderRemark(id, request);
        return ApiResponse.success("更新成功", null);
    }
}
