package com.shiyiju.modules.admin.vo;

public class AdminConfigItemVO {

    private Long id;
    private String group;
    private String key;
    private String value;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
