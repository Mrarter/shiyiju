package com.shiyiju.modules.artist.entity;

import java.math.BigDecimal;

public class ArtistWorkEntity {

    private Long artworkId;
    private String artworkNo;
    private String title;
    private String coverUrl;
    private BigDecimal currentPrice;
    private String saleStatus;

    public Long getArtworkId() { return artworkId; }
    public void setArtworkId(Long artworkId) { this.artworkId = artworkId; }
    public String getArtworkNo() { return artworkNo; }
    public void setArtworkNo(String artworkNo) { this.artworkNo = artworkNo; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public String getSaleStatus() { return saleStatus; }
    public void setSaleStatus(String saleStatus) { this.saleStatus = saleStatus; }
}
