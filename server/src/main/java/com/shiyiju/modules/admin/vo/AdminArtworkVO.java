package com.shiyiju.modules.admin.vo;

public class AdminArtworkVO {

    private Long id;
    private Long artistId;
    private String name;
    private String artist;
    private String price;
    private Integer stock;
    private String status;
    private String tag;
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getArtistId() { return artistId; }
    public void setArtistId(Long artistId) { this.artistId = artistId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
