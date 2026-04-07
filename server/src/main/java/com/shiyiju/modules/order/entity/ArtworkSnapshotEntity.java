package com.shiyiju.modules.order.entity;

import java.math.BigDecimal;

public class ArtworkSnapshotEntity {

    private Long artworkId;
    private Long artistId;
    private String title;
    private String artworkNo;
    private BigDecimal currentPrice;
    private Integer inventoryAvailable;
    private String status;
    private String coverUrl;

    public Long getArtworkId() { return artworkId; }
    public void setArtworkId(Long artworkId) { this.artworkId = artworkId; }
    public Long getArtistId() { return artistId; }
    public void setArtistId(Long artistId) { this.artistId = artistId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtworkNo() { return artworkNo; }
    public void setArtworkNo(String artworkNo) { this.artworkNo = artworkNo; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public Integer getInventoryAvailable() { return inventoryAvailable; }
    public void setInventoryAvailable(Integer inventoryAvailable) { this.inventoryAvailable = inventoryAvailable; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}
