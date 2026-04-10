package com.shiyiju.modules.artist.controller;

import com.shiyiju.common.api.ApiResponse;
import com.shiyiju.modules.artist.service.ArtistService;
import com.shiyiju.modules.artist.vo.ArtistCardVO;
import com.shiyiju.modules.artist.vo.ArtistDetailVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/{id}")
    public ApiResponse<ArtistDetailVO> detail(@PathVariable("id") Long artistId) {
        return ApiResponse.success(artistService.getArtistDetail(artistId));
    }

    @GetMapping("/recommend")
    public ApiResponse<List<ArtistCardVO>> recommend(@RequestParam(value = "limit", defaultValue = "6") Integer limit) {
        return ApiResponse.success(artistService.listRecommendedArtists(limit));
    }
}
