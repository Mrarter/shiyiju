package com.shiyiju.modules.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.admin.dto.AdminAccountSaveDTO;
import com.shiyiju.modules.admin.dto.AdminConfigItemSaveDTO;
import com.shiyiju.modules.admin.dto.AdminRoleSaveDTO;
import com.shiyiju.modules.admin.mapper.AdminMapper;
import com.shiyiju.modules.admin.vo.AdminAccountVO;
import com.shiyiju.modules.admin.vo.AdminConfigItemVO;
import com.shiyiju.modules.admin.vo.AdminRoleVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AdminSettingsService {

    private static final String ACCOUNTS_KEY = "admin.settings.accounts";
    private static final String ROLES_KEY = "admin.settings.roles";
    private static final String CONFIGS_KEY = "admin.settings.configs";

    private final AdminMapper adminMapper;
    private final ObjectMapper objectMapper;

    public AdminSettingsService(AdminMapper adminMapper, ObjectMapper objectMapper) {
        this.adminMapper = adminMapper;
        this.objectMapper = objectMapper;
    }

    public List<AdminAccountVO> listAccounts() {
        List<AdminAccountVO> accounts = readList(ACCOUNTS_KEY, new TypeReference<>() {}, defaultAccounts());
        List<AdminRoleVO> roles = listRoles();
        String fallbackRole = roles.isEmpty() ? "运营人员" : roles.get(0).getName();
        return accounts.stream().peek(account -> {
            if (account.getRole() == null || account.getRole().isBlank()) {
                account.setRole(fallbackRole);
            }
            if (account.getStatus() == null || account.getStatus().isBlank()) {
                account.setStatus("启用");
            }
        }).toList();
    }

    public AdminAccountVO saveAccount(Long id, AdminAccountSaveDTO request) {
        List<AdminAccountVO> accounts = new ArrayList<>(listAccounts());
        if (id == null) {
            AdminAccountVO account = new AdminAccountVO();
            account.setId(nextId(accounts.stream().map(AdminAccountVO::getId).toList(), 7000));
            account.setName(request.getName().trim());
            account.setRole(request.getRole().trim());
            account.setStatus(request.getStatus().trim());
            account.setLastLogin("刚刚创建");
            accounts.add(0, account);
            writeList(ACCOUNTS_KEY, accounts, "settings", "后台账号列表");
            return account;
        }
        AdminAccountVO target = accounts.stream().filter(item -> Objects.equals(item.getId(), id)).findFirst()
            .orElseThrow(() -> new BusinessException(40404, "账号不存在"));
        target.setName(request.getName().trim());
        target.setRole(request.getRole().trim());
        target.setStatus(request.getStatus().trim());
        writeList(ACCOUNTS_KEY, accounts, "settings", "后台账号列表");
        return target;
    }

    public List<AdminRoleVO> listRoles() {
        List<AdminRoleVO> roles = readList(ROLES_KEY, new TypeReference<>() {}, defaultRoles());
        List<AdminAccountVO> accounts = readList(ACCOUNTS_KEY, new TypeReference<>() {}, defaultAccounts());
        return roles.stream().map(role -> normalizeRole(role, accounts)).toList();
    }

    public AdminRoleVO saveRole(Long id, AdminRoleSaveDTO request) {
        List<AdminRoleVO> roles = new ArrayList<>(readList(ROLES_KEY, new TypeReference<>() {}, defaultRoles()));
        List<AdminAccountVO> accounts = new ArrayList<>(listAccounts());
        if (id == null) {
            AdminRoleVO role = new AdminRoleVO();
            role.setId(nextId(roles.stream().map(AdminRoleVO::getId).toList(), 8000));
            role.setName(request.getName().trim());
            role.setScopes(new ArrayList<>(request.getScopes()));
            role.setStatus(request.getStatus().trim());
            roles.add(0, role);
            writeList(ROLES_KEY, roles, "settings", "后台角色权限");
            return normalizeRole(role, accounts);
        }
        AdminRoleVO target = roles.stream().filter(item -> Objects.equals(item.getId(), id)).findFirst()
            .orElseThrow(() -> new BusinessException(40404, "角色不存在"));
        String oldName = target.getName();
        target.setName(request.getName().trim());
        target.setScopes(new ArrayList<>(request.getScopes()));
        target.setStatus(request.getStatus().trim());
        accounts.forEach(account -> {
            if (oldName.equals(account.getRole())) {
                account.setRole(target.getName());
            }
        });
        writeList(ACCOUNTS_KEY, accounts, "settings", "后台账号列表");
        writeList(ROLES_KEY, roles, "settings", "后台角色权限");
        return normalizeRole(target, accounts);
    }

    public void deleteRole(Long id) {
        List<AdminRoleVO> roles = new ArrayList<>(readList(ROLES_KEY, new TypeReference<>() {}, defaultRoles()));
        List<AdminAccountVO> accounts = listAccounts();
        AdminRoleVO target = roles.stream().filter(item -> Objects.equals(item.getId(), id)).findFirst()
            .orElseThrow(() -> new BusinessException(40404, "角色不存在"));
        long members = accounts.stream().filter(account -> target.getName().equals(account.getRole())).count();
        if (members > 0) {
            throw new BusinessException(40001, "该角色仍有账号绑定，不能删除");
        }
        roles.removeIf(item -> Objects.equals(item.getId(), id));
        writeList(ROLES_KEY, roles, "settings", "后台角色权限");
    }

    public List<AdminConfigItemVO> listConfigs() {
        return readList(CONFIGS_KEY, new TypeReference<>() {}, defaultConfigs());
    }

    public AdminConfigItemVO saveConfig(Long id, AdminConfigItemSaveDTO request) {
        List<AdminConfigItemVO> configs = new ArrayList<>(listConfigs());
        if (id == null) {
            AdminConfigItemVO item = new AdminConfigItemVO();
            item.setId(nextId(configs.stream().map(AdminConfigItemVO::getId).toList(), 9000));
            item.setGroup(request.getGroup().trim());
            item.setKey(request.getKey().trim());
            item.setValue(request.getValue());
            item.setRemark(request.getRemark());
            configs.add(0, item);
            writeList(CONFIGS_KEY, configs, "settings", "系统配置项");
            return item;
        }
        AdminConfigItemVO target = configs.stream().filter(item -> Objects.equals(item.getId(), id)).findFirst()
            .orElseThrow(() -> new BusinessException(40404, "配置项不存在"));
        target.setGroup(request.getGroup().trim());
        target.setKey(request.getKey().trim());
        target.setValue(request.getValue());
        target.setRemark(request.getRemark());
        writeList(CONFIGS_KEY, configs, "settings", "系统配置项");
        return target;
    }

    public AdminConfigItemVO duplicateConfig(Long id) {
        List<AdminConfigItemVO> configs = new ArrayList<>(listConfigs());
        AdminConfigItemVO source = configs.stream().filter(item -> Objects.equals(item.getId(), id)).findFirst()
            .orElseThrow(() -> new BusinessException(40404, "配置项不存在"));
        AdminConfigItemVO item = new AdminConfigItemVO();
        item.setId(nextId(configs.stream().map(AdminConfigItemVO::getId).toList(), 9000));
        item.setGroup(source.getGroup());
        item.setKey(source.getKey() + ".copy");
        item.setValue(source.getValue());
        item.setRemark(source.getRemark());
        configs.add(0, item);
        writeList(CONFIGS_KEY, configs, "settings", "系统配置项");
        return item;
    }

    public void deleteConfig(Long id) {
        List<AdminConfigItemVO> configs = new ArrayList<>(listConfigs());
        if (!configs.removeIf(item -> Objects.equals(item.getId(), id))) {
            throw new BusinessException(40404, "配置项不存在");
        }
        writeList(CONFIGS_KEY, configs, "settings", "系统配置项");
    }

    private AdminRoleVO normalizeRole(AdminRoleVO role, List<AdminAccountVO> accounts) {
        AdminRoleVO result = new AdminRoleVO();
        result.setId(role.getId());
        result.setName(role.getName());
        result.setScopes(role.getScopes() == null ? List.of() : role.getScopes());
        result.setScopeText(String.join("、", result.getScopes()));
        result.setStatus(role.getStatus() == null ? "启用" : role.getStatus());
        result.setMemberCount((int) accounts.stream().filter(account -> role.getName().equals(account.getRole())).count());
        return result;
    }

    private <T> List<T> readList(String key, TypeReference<List<T>> type, List<T> fallback) {
        String raw = adminMapper.findConfigValue(key);
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return objectMapper.readValue(raw, type);
        } catch (JsonProcessingException exception) {
            return fallback;
        }
    }

    private void writeList(String key, Object value, String group, String remark) {
        try {
            adminMapper.upsertConfig(key, objectMapper.writeValueAsString(value), group, remark);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(50001, "系统配置保存失败");
        }
    }

    private long nextId(List<Long> ids, long seed) {
        AtomicLong max = new AtomicLong(seed);
        ids.stream().filter(Objects::nonNull).forEach(value -> max.set(Math.max(max.get(), value)));
        return max.incrementAndGet();
    }

    private List<AdminAccountVO> defaultAccounts() {
        AdminAccountVO admin = new AdminAccountVO();
        admin.setId(7001L);
        admin.setName("admin");
        admin.setRole("超级管理员");
        admin.setStatus("启用");
        admin.setLastLogin("2026-04-07 15:30");

        AdminAccountVO operator = new AdminAccountVO();
        operator.setId(7002L);
        operator.setName("operator_01");
        operator.setRole("运营人员");
        operator.setStatus("启用");
        operator.setLastLogin("2026-04-07 14:12");
        return List.of(admin, operator);
    }

    private List<AdminRoleVO> defaultRoles() {
        return List.of(
            role(8001L, "超级管理员", List.of("控制台", "首页运营", "艺术家管理", "作品管理", "用户管理", "订单管理", "系统设置"), "启用"),
            role(8002L, "运营人员", List.of("控制台", "首页运营", "艺术家管理", "作品管理", "订单管理"), "启用"),
            role(8003L, "客服", List.of("控制台", "用户管理", "订单管理"), "启用")
        );
    }

    private List<AdminConfigItemVO> defaultConfigs() {
        return List.of(
            config(9001L, "homepage", "homepage.banner.autoRotateSeconds", "5", "首页 Banner 自动轮播秒数"),
            config(9002L, "trade", "trade.order.shipmentTimeoutHours", "72", "订单超时发货提醒时长"),
            config(9003L, "artist", "artist.profile.requiredFields", "name,tags,avatar", "艺术家资料必填字段")
        );
    }

    private AdminRoleVO role(Long id, String name, List<String> scopes, String status) {
        AdminRoleVO role = new AdminRoleVO();
        role.setId(id);
        role.setName(name);
        role.setScopes(scopes);
        role.setStatus(status);
        return role;
    }

    private AdminConfigItemVO config(Long id, String group, String key, String value, String remark) {
        AdminConfigItemVO item = new AdminConfigItemVO();
        item.setId(id);
        item.setGroup(group);
        item.setKey(key);
        item.setValue(value);
        item.setRemark(remark);
        return item;
    }
}
