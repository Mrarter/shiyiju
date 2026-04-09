package com.shiyiju.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AdminRoleSaveDTO {

    @NotBlank
    private String name;

    @NotNull
    private List<String> scopes;

    @NotBlank
    private String status;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getScopes() { return scopes; }
    public void setScopes(List<String> scopes) { this.scopes = scopes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
