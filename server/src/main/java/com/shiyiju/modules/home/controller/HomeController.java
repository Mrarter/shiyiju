package com.shiyiju.modules.home.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.admin.service.AdminContentService;
import com.shiyiju.modules.admin.vo.AdminOperationVO;
import com.shiyiju.modules.artwork.service.ArtworkService;
import com.shiyiju.modules.artwork.vo.ArtworkListItemVO;
import com.shiyiju.modules.artwork.dto.ArtworkQueryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/home")
public class HomeController {

    private final AdminContentService adminContentService;
    private final ArtworkService artworkService;

    public HomeController(AdminContentService adminContentService, ArtworkService artworkService) {
        this.adminContentService = adminContentService;
        this.artworkService = artworkService;
    }

    @GetMapping("/banners")
    public ApiResponse<List<AdminOperationVO>> banners() {
        // 获取启用的 Banner 配置
        List<AdminOperationVO> operations = adminContentService.listOperations();
        List<AdminOperationVO> banners = operations.stream()
                .filter(op -> "ENABLED".equalsIgnoreCase(op.getStatus()))
                .filter(op -> "BANNER".equalsIgnoreCase(op.getType()))
                .sorted((a, b) -> {
                    // 按权重 sortNo 降序排序（值越大越靠前）
                    int sortA = a.getSortNo() != null ? a.getSortNo() : 0;
                    int sortB = b.getSortNo() != null ? b.getSortNo() : 0;
                    return Integer.compare(sortB, sortA);
                })
                .map(op -> {
                    AdminOperationVO banner = new AdminOperationVO();
                    banner.setId(op.getId());
                    banner.setTitle(op.getTitle());
                    banner.setType(op.getType());
                    banner.setStatus(op.getStatus());
                    banner.setSubtitle(op.getTarget() != null ? op.getTarget() : "查看详情");
                    banner.setDate(op.getUpdatedAt());
                    banner.setImageUrl(op.getImageUrl());
                    banner.setCoverUrl(op.getImageUrl());
                    banner.setSortNo(op.getSortNo());
                    return banner;
                })
                .collect(Collectors.toList());
        return ApiResponse.success(banners);
    }

    @GetMapping("/works")
    public ApiResponse<List<ArtworkListItemVO>> works(ArtworkQueryDTO queryDTO) {
        return ApiResponse.success(artworkService.listWorks(queryDTO));
    }
}
