package com.shiyiju.modules.admin.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.admin.dto.AdminOperationSaveDTO;
import com.shiyiju.modules.admin.dto.AdminAccountSaveDTO;
import com.shiyiju.modules.admin.dto.AdminArtistSaveDTO;
import com.shiyiju.modules.admin.dto.AdminArtworkSaveDTO;
import com.shiyiju.modules.admin.dto.AdminConfigItemSaveDTO;
import com.shiyiju.modules.admin.dto.AdminRemarkUpdateDTO;
import com.shiyiju.modules.admin.dto.AdminRoleSaveDTO;
import com.shiyiju.modules.admin.dto.AdminShipmentUpdateDTO;
import com.shiyiju.modules.admin.dto.AdminStatusUpdateDTO;
import com.shiyiju.modules.admin.dto.AdminDistributorSaveDTO;
import com.shiyiju.modules.admin.service.AdminContentService;
import com.shiyiju.modules.admin.service.AdminSettingsService;
import com.shiyiju.modules.admin.vo.AdminAccountVO;
import com.shiyiju.modules.admin.vo.AdminArtistVO;
import com.shiyiju.modules.admin.vo.AdminArtworkVO;
import com.shiyiju.modules.admin.vo.AdminConfigItemVO;
import com.shiyiju.modules.admin.vo.AdminOperationVO;
import com.shiyiju.modules.admin.vo.AdminOrderVO;
import com.shiyiju.modules.admin.vo.AdminRoleVO;
import com.shiyiju.modules.admin.vo.AdminUploadVO;
import com.shiyiju.modules.admin.vo.AdminUserVO;
import com.shiyiju.modules.admin.vo.AdminDistributorVO;
import com.shiyiju.modules.admin.service.AdminAssetService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/v1")
public class AdminContentController {

    private final AdminContentService adminContentService;
    private final AdminAssetService adminAssetService;
    private final AdminSettingsService adminSettingsService;

    public AdminContentController(AdminContentService adminContentService, AdminAssetService adminAssetService, AdminSettingsService adminSettingsService) {
        this.adminContentService = adminContentService;
        this.adminAssetService = adminAssetService;
        this.adminSettingsService = adminSettingsService;
    }

    @GetMapping("/operations")
    public ApiResponse<List<AdminOperationVO>> operations() {
        return ApiResponse.success(adminContentService.listOperations());
    }

    @PostMapping(value = "/uploads/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AdminUploadVO> uploadImage(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success("上传成功", adminAssetService.uploadImage(file));
    }

    /**
     * 上传图片（支持裁剪）
     * @param file   图片文件
     * @param cropX  裁剪起点X坐标
     * @param cropY  裁剪起点Y坐标
     * @param cropW  裁剪宽度
     * @param cropH  裁剪高度
     * @param scale  缩放比例（可选）
     */
    @PostMapping(value = "/uploads/images/crop", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AdminUploadVO> uploadImageWithCrop(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "cropX", required = false) Integer cropX,
            @RequestParam(value = "cropY", required = false) Integer cropY,
            @RequestParam(value = "cropW", required = false) Integer cropW,
            @RequestParam(value = "cropH", required = false) Integer cropH,
            @RequestParam(value = "scale", required = false) Double scale) {
        return ApiResponse.success("上传成功", 
            adminAssetService.uploadImageWithCrop(file, cropX, cropY, cropW, cropH, scale));
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

    @DeleteMapping("/operations/{id}")
    public ApiResponse<Void> deleteOperation(@PathVariable Long id) {
        adminContentService.deleteOperation(id);
        return ApiResponse.success("删除成功", null);
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

    @PutMapping("/users/{id}/status")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long id, @Valid @RequestBody AdminStatusUpdateDTO request) {
        adminContentService.updateUserStatus(id, request.getStatus());
        return ApiResponse.success("更新成功", null);
    }

    @GetMapping("/settings/accounts")
    public ApiResponse<List<AdminAccountVO>> accounts() {
        return ApiResponse.success(adminSettingsService.listAccounts());
    }

    @PostMapping("/settings/accounts")
    public ApiResponse<AdminAccountVO> createAccount(@Valid @RequestBody AdminAccountSaveDTO request) {
        return ApiResponse.success("创建成功", adminSettingsService.saveAccount(null, request));
    }

    @PutMapping("/settings/accounts/{id}")
    public ApiResponse<AdminAccountVO> updateAccount(@PathVariable Long id, @Valid @RequestBody AdminAccountSaveDTO request) {
        return ApiResponse.success("更新成功", adminSettingsService.saveAccount(id, request));
    }

    @GetMapping("/settings/roles")
    public ApiResponse<List<AdminRoleVO>> roles() {
        return ApiResponse.success(adminSettingsService.listRoles());
    }

    @PostMapping("/settings/roles")
    public ApiResponse<AdminRoleVO> createRole(@Valid @RequestBody AdminRoleSaveDTO request) {
        return ApiResponse.success("创建成功", adminSettingsService.saveRole(null, request));
    }

    @PutMapping("/settings/roles/{id}")
    public ApiResponse<AdminRoleVO> updateRole(@PathVariable Long id, @Valid @RequestBody AdminRoleSaveDTO request) {
        return ApiResponse.success("更新成功", adminSettingsService.saveRole(id, request));
    }

    @DeleteMapping("/settings/roles/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        adminSettingsService.deleteRole(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/settings/configs")
    public ApiResponse<List<AdminConfigItemVO>> configs() {
        return ApiResponse.success(adminSettingsService.listConfigs());
    }

    @PostMapping("/settings/configs")
    public ApiResponse<AdminConfigItemVO> createConfig(@Valid @RequestBody AdminConfigItemSaveDTO request) {
        return ApiResponse.success("创建成功", adminSettingsService.saveConfig(null, request));
    }

    @PutMapping("/settings/configs/{id}")
    public ApiResponse<AdminConfigItemVO> updateConfig(@PathVariable Long id, @Valid @RequestBody AdminConfigItemSaveDTO request) {
        return ApiResponse.success("更新成功", adminSettingsService.saveConfig(id, request));
    }

    @PostMapping("/settings/configs/{id}/duplicate")
    public ApiResponse<AdminConfigItemVO> duplicateConfig(@PathVariable Long id) {
        return ApiResponse.success("复制成功", adminSettingsService.duplicateConfig(id));
    }

    @DeleteMapping("/settings/configs/{id}")
    public ApiResponse<Void> deleteConfig(@PathVariable Long id) {
        adminSettingsService.deleteConfig(id);
        return ApiResponse.success("删除成功", null);
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

    @GetMapping("/distributors")
    public ApiResponse<List<AdminDistributorVO>> distributors() {
        return ApiResponse.success(adminContentService.listDistributors());
    }

    @PostMapping("/distributors")
    public ApiResponse<AdminDistributorVO> createDistributor(@Valid @RequestBody AdminDistributorSaveDTO request) {
        return ApiResponse.success("创建成功", adminContentService.createDistributor(request));
    }

    @PutMapping("/distributors/{id}")
    public ApiResponse<AdminDistributorVO> updateDistributor(@PathVariable Long id, @Valid @RequestBody AdminDistributorSaveDTO request) {
        return ApiResponse.success("更新成功", adminContentService.updateDistributor(id, request));
    }

    @PutMapping("/distributors/{id}/status")
    public ApiResponse<Void> updateDistributorStatus(@PathVariable Long id, @Valid @RequestBody AdminStatusUpdateDTO request) {
        adminContentService.updateDistributorStatus(id, request.getStatus());
        return ApiResponse.success("更新成功", null);
    }
}
