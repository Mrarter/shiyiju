package com.shiyiju.modules.artwork.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.artwork.dto.ArtworkQueryDTO;
import com.shiyiju.modules.artwork.service.ArtworkService;
import com.shiyiju.modules.artwork.vo.ArtworkDetailVO;
import com.shiyiju.modules.artwork.vo.ArtworkListItemVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/works")
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

    @PostMapping("/{id}/click")
    public ApiResponse<Void> recordClick(@PathVariable("id") Long artworkId) {
        artworkService.recordClick(artworkId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/favorite")
    public ApiResponse<Void> recordFavorite(@PathVariable("id") Long artworkId, @RequestBody Map<String, Boolean> body) {
        Boolean favorite = body.get("favorite");
        artworkService.recordFavorite(artworkId, favorite != null && favorite);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/cart")
    public ApiResponse<Void> recordCart(@PathVariable("id") Long artworkId) {
        artworkService.recordCart(artworkId);
        return ApiResponse.success(null);
    }
}
