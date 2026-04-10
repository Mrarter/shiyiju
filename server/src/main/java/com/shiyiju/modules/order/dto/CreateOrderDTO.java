package com.shiyiju.modules.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateOrderDTO {

    // 支持单个商品下单（兼容旧接口）
    private Long artworkId;
    
    // 支持多个商品下单
    private List<Long> artworkIds;
    
    @NotNull
    private Long addressId;

    @Size(max = 500)
    private String remark;

    private Boolean anonymous;

    public Long getArtworkId() { return artworkId; }
    public void setArtworkId(Long artworkId) { this.artworkId = artworkId; }
    public List<Long> getArtworkIds() { return artworkIds; }
    public void setArtworkIds(List<Long> artworkIds) { this.artworkIds = artworkIds; }
    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Boolean getAnonymous() { return anonymous; }
    public void setAnonymous(Boolean anonymous) { this.anonymous = anonymous; }
    
    /**
     * 获取商品ID列表
     */
    public List<Long> resolveArtworkIds() {
        if (artworkIds != null && !artworkIds.isEmpty()) {
            return artworkIds;
        }
        if (artworkId != null) {
            return List.of(artworkId);
        }
        return List.of();
    }
}
