package com.shiyiju.modules.artwork.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.artwork.dto.ArtworkQueryDTO;
import com.shiyiju.modules.artwork.entity.ArtworkDetailEntity;
import com.shiyiju.modules.artwork.mapper.ArtworkMapper;
import com.shiyiju.modules.artwork.vo.ArtworkDetailVO;
import com.shiyiju.modules.artwork.vo.ArtworkListItemVO;
import com.shiyiju.modules.artwork.vo.ArtworkPriceRuleVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ArtworkService {

    private final ArtworkMapper artworkMapper;

    public ArtworkService(ArtworkMapper artworkMapper) {
        this.artworkMapper = artworkMapper;
    }

    public List<ArtworkListItemVO> listWorks(ArtworkQueryDTO queryDTO) {
        ArtworkQueryDTO effectiveQuery = normalizeQuery(queryDTO);
        return artworkMapper.findWorks(effectiveQuery).stream().map(entity -> {
            ArtworkListItemVO vo = new ArtworkListItemVO();
            vo.setArtworkId(entity.getArtworkId());
            vo.setArtworkNo(entity.getArtworkNo());
            vo.setArtistId(entity.getArtistId());
            vo.setArtistName(entity.getArtistName());
            vo.setArtistLevelName(entity.getArtistLevelName());
            vo.setTitle(entity.getTitle());
            vo.setCategory(entity.getCategory());
            vo.setSaleMode(entity.getSaleMode());
            vo.setSaleStatus(mapSaleStatus(entity.getStatus()));
            vo.setCoverUrl(entity.getCoverUrl());
            vo.setCurrentPrice(entity.getCurrentPrice());
            vo.setFavoriteCount(defaultZero(entity.getFavoriteCount()));
            vo.setViewCount(defaultZero(entity.getViewCount()));
            return vo;
        }).toList();
    }

    public ArtworkDetailVO getWorkDetail(Long artworkId) {
        ArtworkDetailEntity entity = artworkMapper.findWorkDetail(artworkId);
        if (entity == null) {
            throw new BusinessException(40004, "作品不存在");
        }

        ArtworkDetailVO vo = new ArtworkDetailVO();
        vo.setArtworkId(entity.getArtworkId());
        vo.setArtworkNo(entity.getArtworkNo());
        vo.setArtistId(entity.getArtistId());
        vo.setArtistName(entity.getArtistName());
        vo.setArtistLevelName(entity.getArtistLevelName());
        vo.setTitle(entity.getTitle());
        vo.setCategory(entity.getCategory());
        vo.setStyle(entity.getStyle());
        vo.setWidthCm(entity.getWidthCm());
        vo.setHeightCm(entity.getHeightCm());
        vo.setDepthCm(entity.getDepthCm());
        vo.setMaterial(entity.getMaterial());
        vo.setCreationYear(entity.getCreationYear());
        vo.setDescription(entity.getDescription());
        vo.setBasePrice(entity.getBasePrice());
        vo.setCurrentPrice(entity.getCurrentPrice());
        vo.setSaleStatus(mapSaleStatus(entity.getStatus()));
        vo.setPriceStatus(entity.getPriceStatus());
        vo.setOnlineDays(defaultZero(entity.getOnlineDays()));
        vo.setFavoriteCount(defaultZero(entity.getFavoriteCount()));
        vo.setViewCount(defaultZero(entity.getViewCount()));
        vo.setGroupEnabled(entity.getSupportGroupBuy() != null && entity.getSupportGroupBuy() == 1);
        vo.setResaleEnabled(entity.getSupportResale() == null || entity.getSupportResale() == 1);
        vo.setCertificateType("ELECTRONIC");
        vo.setSaleMode(entity.getSaleMode());
        vo.setCoverUrl(entity.getCoverUrl());
        vo.setMediaUrls(artworkMapper.findArtworkMedia(artworkId).stream().map(media -> media.getMediaUrl()).toList());
        vo.setPriceRule(buildPriceRule(entity));
        return vo;
    }

    private ArtworkQueryDTO normalizeQuery(ArtworkQueryDTO queryDTO) {
        ArtworkQueryDTO normalized = queryDTO == null ? new ArtworkQueryDTO() : queryDTO;
        if (!StringUtils.hasText(normalized.getStatus())) {
            normalized.setStatus("PUBLISHED");
        }
        if (StringUtils.hasText(normalized.getKeyword())) {
            normalized.setKeyword(normalized.getKeyword().trim());
        }
        if (StringUtils.hasText(normalized.getCategory())) {
            normalized.setCategory(normalized.getCategory().trim());
        }
        if (StringUtils.hasText(normalized.getStatus())) {
            normalized.setStatus(normalized.getStatus().trim());
        }
        return normalized;
    }

    private ArtworkPriceRuleVO buildPriceRule(ArtworkDetailEntity entity) {
        if (entity.getOnlineDaysWeight() == null
            && entity.getFollowCountWeight() == null
            && entity.getArtistFloatMin() == null
            && entity.getArtistFloatMax() == null
            && entity.getPlatformMaxGrowthRate() == null
            && entity.getManualAdjustRate() == null) {
            return null;
        }
        ArtworkPriceRuleVO vo = new ArtworkPriceRuleVO();
        vo.setOnlineDaysWeight(entity.getOnlineDaysWeight());
        vo.setFollowCountWeight(entity.getFollowCountWeight());
        vo.setArtistFloatMin(entity.getArtistFloatMin());
        vo.setArtistFloatMax(entity.getArtistFloatMax());
        vo.setPlatformMaxGrowthRate(entity.getPlatformMaxGrowthRate());
        vo.setManualAdjustRate(entity.getManualAdjustRate());
        return vo;
    }

    private String mapSaleStatus(String status) {
        return switch (status) {
            case "PUBLISHED" -> "ON_SALE";
            case "COLLECTED", "SOLD_OUT" -> "COLLECTED";
            default -> status;
        };
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }
}
