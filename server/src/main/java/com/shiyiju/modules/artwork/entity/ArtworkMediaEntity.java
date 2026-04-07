package com.shiyiju.modules.artwork.entity;

public class ArtworkMediaEntity {

    private String mediaType;
    private String mediaUrl;
    private Integer sortNo;
    private Integer isMain;

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
    public Integer getIsMain() { return isMain; }
    public void setIsMain(Integer isMain) { this.isMain = isMain; }
}
