package com.shiyiju.modules.admin.vo;

import java.util.List;

public class AdminRoleVO {

    private Long id;
    private String name;
    private List<String> scopes;
    private String scopeText;
    private Integer memberCount;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getScopes() { return scopes; }
    public void setScopes(List<String> scopes) { this.scopes = scopes; }
    public String getScopeText() { return scopeText; }
    public void setScopeText(String scopeText) { this.scopeText = scopeText; }
    public Integer getMemberCount() { return memberCount; }
    public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
