package com.shiyiju.modules.admin.entity;

public class AdminOrderEntity {

    private Long id;
    private String orderNo;
    private String user;
    private String artwork;
    private String amount;
    private String status;
    private String payStatus;
    private String shipStatus;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getArtwork() { return artwork; }
    public void setArtwork(String artwork) { this.artwork = artwork; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
    public String getShipStatus() { return shipStatus; }
    public void setShipStatus(String shipStatus) { this.shipStatus = shipStatus; }
}
