package com.shiyiju.modules.artist.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.artist.entity.ArtistHomeEntity;
import com.shiyiju.modules.artist.mapper.ArtistMapper;
import com.shiyiju.modules.artist.vo.ArtistCardVO;
import com.shiyiju.modules.artist.vo.ArtistDetailVO;
import com.shiyiju.modules.artist.vo.ArtistWorkVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ArtistService {

    private final ArtistMapper artistMapper;

    public ArtistService(ArtistMapper artistMapper) {
        this.artistMapper = artistMapper;
    }

    public ArtistDetailVO getArtistDetail(Long artistId) {
        ArtistHomeEntity entity = artistMapper.findArtistHome(artistId);
        if (entity == null) {
            throw new BusinessException(40001, "艺术家不存在");
        }

        ArtistDetailVO vo = new ArtistDetailVO();
        vo.setArtistId(entity.getArtistId());
        vo.setArtistName(entity.getArtistName());
        vo.setAvatar(entity.getAvatar());
        vo.setBackgroundImageUrl(entity.getBackgroundImageUrl());
        vo.setLevelName(entity.getLevelName());
        vo.setSlogan(entity.getSlogan());
        vo.setBio(entity.getBio());
        vo.setStyleTags(parseTags(entity.getStyleTags()));
        vo.setFanCount(entity.getFanCount());
        vo.setWorkCount(entity.getWorkCount());
        vo.setSoldCount(entity.getSoldCount());
        vo.setTotalSaleAmount(entity.getTotalSaleAmount());
        vo.setSigned(entity.getSignedFlag() != null && entity.getSignedFlag() == 1);
        vo.setWorks(artistMapper.findArtistWorks(artistId).stream().map(work -> {
            ArtistWorkVO workVO = new ArtistWorkVO();
            workVO.setArtworkId(work.getArtworkId());
            workVO.setArtworkNo(work.getArtworkNo());
            workVO.setTitle(work.getTitle());
            workVO.setCoverUrl(work.getCoverUrl());
            workVO.setCurrentPrice(work.getCurrentPrice());
            workVO.setSaleStatus(work.getSaleStatus());
            return workVO;
        }).toList());
        return vo;
    }

    public List<ArtistCardVO> listRecommendedArtists(int limit) {
        int effectiveLimit = limit <= 0 ? 6 : Math.min(limit, 20);
        return artistMapper.findRecommendedArtists(effectiveLimit).stream()
            .map(this::toArtistCard)
            .toList();
    }

    private ArtistCardVO toArtistCard(ArtistHomeEntity entity) {
        ArtistCardVO vo = new ArtistCardVO();
        vo.setArtistId(entity.getArtistId());
        vo.setArtistName(entity.getArtistName());
        vo.setAvatar(entity.getAvatar());
        vo.setLevelName(entity.getLevelName());
        vo.setSlogan(entity.getSlogan());
        return vo;
    }

    private List<String> parseTags(String styleTags) {
        if (!StringUtils.hasText(styleTags)) {
            return Collections.emptyList();
        }
        return Arrays.stream(styleTags.split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .toList();
    }
}
