package com.shiyiju.modules.artwork.vo;

import java.math.BigDecimal;

public class ArtworkListItemVO {

    private Long artworkId;
    private String artworkNo;
    private Long artistId;
    private String artistName;
    private String artistLevelName;
    private String title;
    private String category;
    private String saleStatus;
    private String saleMode;
    private String coverUrl;
    private BigDecimal currentPrice;
    private Integer favoriteCount;
    private Integer viewCount;

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
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSaleStatus() { return saleStatus; }
    public void setSaleStatus(String saleStatus) { this.saleStatus = saleStatus; }
    public String getSaleMode() { return saleMode; }
    public void setSaleMode(String saleMode) { this.saleMode = saleMode; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public Integer getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
}
