package com.shiyiju.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminRemarkUpdateDTO {

    @NotBlank
    private String remark;

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
