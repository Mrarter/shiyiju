package com.shiyiju.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminAccountSaveDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String role;

    @NotBlank
    private String status;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
