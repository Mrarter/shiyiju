package com.shiyiju.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminShipmentUpdateDTO {

    @NotBlank
    private String company;

    @NotBlank
    private String trackingNo;

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }
}
