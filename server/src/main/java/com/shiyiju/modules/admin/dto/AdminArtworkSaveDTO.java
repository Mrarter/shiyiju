package com.shiyiju.modules.admin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AdminArtworkSaveDTO {

    @NotBlank
    private String name;

    @NotNull
    private Long artistId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer stock;

    @NotBlank
    private String status;

    private String category;
    private String material;
    private Integer creationYear;
    private BigDecimal widthCm;
    private BigDecimal heightCm;
    private BigDecimal depthCm;
    private Integer adminWeight;
    private String tag;
    private String description;
    private String coverUrl;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getArtistId() { return artistId; }
    public void setArtistId(Long artistId) { this.artistId = artistId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public Integer getCreationYear() { return creationYear; }
    public void setCreationYear(Integer creationYear) { this.creationYear = creationYear; }
    public BigDecimal getWidthCm() { return widthCm; }
    public void setWidthCm(BigDecimal widthCm) { this.widthCm = widthCm; }
    public BigDecimal getHeightCm() { return heightCm; }
    public void setHeightCm(BigDecimal heightCm) { this.heightCm = heightCm; }
    public BigDecimal getDepthCm() { return depthCm; }
    public void setDepthCm(BigDecimal depthCm) { this.depthCm = depthCm; }
    public Integer getAdminWeight() { return adminWeight; }
    public void setAdminWeight(Integer adminWeight) { this.adminWeight = adminWeight; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}
