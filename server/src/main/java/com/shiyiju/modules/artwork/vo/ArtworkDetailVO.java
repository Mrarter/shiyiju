package com.shiyiju.modules.artwork.vo;

import java.math.BigDecimal;
import java.util.List;

public class ArtworkDetailVO {

    private Long artworkId;
    private String artworkNo;
    private Long artistId;
    private String artistName;
    private String artistLevelName;
    private String artistAvatar;
    private String title;
    private String category;
    private String style;
    private BigDecimal widthCm;
    private BigDecimal heightCm;
    private BigDecimal depthCm;
    private String material;
    private String creationYear;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal currentPrice;
    private String saleStatus;
    private String priceStatus;
    private Integer onlineDays;
    private Integer favoriteCount;
    private Integer viewCount;
    private boolean groupEnabled;
    private boolean resaleEnabled;
    private String certificateType;
    private String saleMode;
    private String coverUrl;
    private List<String> mediaUrls;
    private ArtworkPriceRuleVO priceRule;

    public Long getArtworkId() { return artworkId; }
    public void setArtworkId(Long artworkId) { this.artworkId = artworkId; }
    public String getArtworkNo() { return artworkNo; }
    public void setArtworkNo(String artworkNo) { this.artworkNo = artworkNo; }
    public Long getArtistId() { return artistId; }
    public void setArtistId(Long artistId) { this.artistId = artistId; }
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public String getArtistLevelName() { return artistLevelName; }
    public void setArtistLevelName(String artistLevelName) { this.artistLevelName = artistLevelName; }
    public String getArtistAvatar() { return artistAvatar; }
    public void setArtistAvatar(String artistAvatar) { this.artistAvatar = artistAvatar; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    public BigDecimal getWidthCm() { return widthCm; }
    public void setWidthCm(BigDecimal widthCm) { this.widthCm = widthCm; }
    public BigDecimal getHeightCm() { return heightCm; }
    public void setHeightCm(BigDecimal heightCm) { this.heightCm = heightCm; }
    public BigDecimal getDepthCm() { return depthCm; }
    public void setDepthCm(BigDecimal depthCm) { this.depthCm = depthCm; }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public String getCreationYear() { return creationYear; }
    public void setCreationYear(String creationYear) { this.creationYear = creationYear; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public String getSaleStatus() { return saleStatus; }
    public void setSaleStatus(String saleStatus) { this.saleStatus = saleStatus; }
    public String getPriceStatus() { return priceStatus; }
    public void setPriceStatus(String priceStatus) { this.priceStatus = priceStatus; }
    public Integer getOnlineDays() { return onlineDays; }
    public void setOnlineDays(Integer onlineDays) { this.onlineDays = onlineDays; }
    public Integer getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public boolean isGroupEnabled() { return groupEnabled; }
    public void setGroupEnabled(boolean groupEnabled) { this.groupEnabled = groupEnabled; }
    public boolean isResaleEnabled() { return resaleEnabled; }
    public void setResaleEnabled(boolean resaleEnabled) { this.resaleEnabled = resaleEnabled; }
    public String getCertificateType() { return certificateType; }
    public void setCertificateType(String certificateType) { this.certificateType = certificateType; }
    public String getSaleMode() { return saleMode; }
    public void setSaleMode(String saleMode) { this.saleMode = saleMode; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public List<String> getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(List<String> mediaUrls) { this.mediaUrls = mediaUrls; }
    public ArtworkPriceRuleVO getPriceRule() { return priceRule; }
    public void setPriceRule(ArtworkPriceRuleVO priceRule) { this.priceRule = priceRule; }
}
