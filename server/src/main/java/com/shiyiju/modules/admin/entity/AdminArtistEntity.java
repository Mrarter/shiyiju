package com.shiyiju.modules.admin.entity;

public class AdminArtistEntity {

    private Long id;
    private Long userId;
    private String name;
    private String city;
    private String tags;
    private Integer works;
    private String status;
    private Integer sort;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Integer getWorks() { return works; }
    public void setWorks(Integer works) { this.works = works; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
}
