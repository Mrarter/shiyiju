package com.shiyiju.modules.artwork.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.artwork.dto.ArtworkQueryDTO;
import com.shiyiju.modules.artwork.service.ArtworkService;
import com.shiyiju.modules.artwork.vo.ArtworkDetailVO;
import com.shiyiju.modules.artwork.vo.ArtworkListItemVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/works")
public class ArtworkController {

    private final ArtworkService artworkService;

    public ArtworkController(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }

    @GetMapping
    public ApiResponse<List<ArtworkListItemVO>> list(ArtworkQueryDTO queryDTO) {
        return ApiResponse.success(artworkService.listWorks(queryDTO));
    }

    @GetMapping("/{id}")
    public ApiResponse<ArtworkDetailVO> detail(@PathVariable("id") Long artworkId) {
        return ApiResponse.success(artworkService.getWorkDetail(artworkId));
    }
}
