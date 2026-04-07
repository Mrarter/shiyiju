package com.shiyiju.modules.artwork.dto;

public class ArtworkQueryDTO {

    private String keyword;
    private String category;
    private Long artistId;
    private String status;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Long getArtistId() { return artistId; }
    public void setArtistId(Long artistId) { this.artistId = artistId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
