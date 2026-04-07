package com.shiyiju.modules.artist.vo;

import java.util.List;

public class ArtistDetailVO {

    private Long artistId;
    private String artistName;
    private String avatar;
    private String backgroundImageUrl;
    private String levelName;
    private String slogan;
    private String bio;
    private java.util.List<String> styleTags;
    private Integer fanCount;
    private Integer workCount;
    private Integer soldCount;
    private String totalSaleAmount;
    private Boolean signed;
    private List<ArtistWorkVO> works;

    public Long getArtistId() { return artistId; }
    public void setArtistId(Long artistId) { this.artistId = artistId; }
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getBackgroundImageUrl() { return backgroundImageUrl; }
    public void setBackgroundImageUrl(String backgroundImageUrl) { this.backgroundImageUrl = backgroundImageUrl; }
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
    public String getSlogan() { return slogan; }
    public void setSlogan(String slogan) { this.slogan = slogan; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public java.util.List<String> getStyleTags() { return styleTags; }
    public void setStyleTags(java.util.List<String> styleTags) { this.styleTags = styleTags; }
    public Integer getFanCount() { return fanCount; }
    public void setFanCount(Integer fanCount) { this.fanCount = fanCount; }
    public Integer getWorkCount() { return workCount; }
    public void setWorkCount(Integer workCount) { this.workCount = workCount; }
    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }
    public String getTotalSaleAmount() { return totalSaleAmount; }
    public void setTotalSaleAmount(String totalSaleAmount) { this.totalSaleAmount = totalSaleAmount; }
    public Boolean getSigned() { return signed; }
    public void setSigned(Boolean signed) { this.signed = signed; }
    public List<ArtistWorkVO> getWorks() { return works; }
    public void setWorks(List<ArtistWorkVO> works) { this.works = works; }
}
