<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">系统设置</div>
        <div class="page-subtitle">管理后台账号、角色权限和基础配置项</div>
      </div>
      <el-button type="primary">新增账号</el-button>
    </div>

    <div class="section-card" style="padding: 24px;">
      <el-tabs>
        <el-tab-pane label="账号管理">
          <el-table :data="accounts">
            <el-table-column prop="name" label="账号" min-width="160" />
            <el-table-column prop="role" label="角色" width="140" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column prop="lastLogin" label="最近登录" width="180" />
            <el-table-column label="操作" width="160">
              <template #default>
                <el-button link type="primary">编辑</el-button>
                <el-button link>重置密码</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="角色权限">
          <el-empty description="下一阶段补齐角色权限矩阵" />
        </el-tab-pane>
        <el-tab-pane label="配置项">
          <el-empty description="下一阶段补齐系统配置管理" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
const accounts = [
  { name: 'admin', role: '超级管理员', status: '启用', lastLogin: '2026-04-07 15:30' },
  { name: 'operator_01', role: '运营人员', status: '启用', lastLogin: '2026-04-07 14:12' }
]
</script>
