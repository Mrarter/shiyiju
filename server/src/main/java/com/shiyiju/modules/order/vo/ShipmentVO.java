package com.shiyiju.modules.order.vo;

import java.time.LocalDateTime;

public class ShipmentVO {

    private String shipmentNo;
    private String logisticsCompany;
    private String logisticsCode;
    private String trackingNo;
    private String shipmentStatus;
    private LocalDateTime consignedAt;
    private LocalDateTime signedAt;

    public String getShipmentNo() { return shipmentNo; }
    public void setShipmentNo(String shipmentNo) { this.shipmentNo = shipmentNo; }
    public String getLogisticsCompany() { return logisticsCompany; }
    public void setLogisticsCompany(String logisticsCompany) { this.logisticsCompany = logisticsCompany; }
    public String getLogisticsCode() { return logisticsCode; }
    public void setLogisticsCode(String logisticsCode) { this.logisticsCode = logisticsCode; }
    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }
    public String getShipmentStatus() { return shipmentStatus; }
    public void setShipmentStatus(String shipmentStatus) { this.shipmentStatus = shipmentStatus; }
    public LocalDateTime getConsignedAt() { return consignedAt; }
    public void setConsignedAt(LocalDateTime consignedAt) { this.consignedAt = consignedAt; }
    public LocalDateTime getSignedAt() { return signedAt; }
    public void setSignedAt(LocalDateTime signedAt) { this.signedAt = signedAt; }
}
