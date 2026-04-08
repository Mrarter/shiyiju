package com.shiyiju.modules.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdminArtistSaveDTO {

    @NotBlank
    private String name;

    private String tags;

    @NotNull
    @Min(0)
    private Integer works;

    @NotBlank
    private String status;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Integer getWorks() { return works; }
    public void setWorks(Integer works) { this.works = works; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
