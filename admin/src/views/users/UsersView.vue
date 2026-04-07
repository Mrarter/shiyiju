<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">用户管理</div>
        <div class="page-subtitle">查看微信登录用户资料与最近活跃情况</div>
      </div>
      <el-button>导出用户</el-button>
    </div>

    <div class="section-card" style="padding: 20px;">
      <div class="toolbar">
        <el-input placeholder="搜索昵称/用户编号" style="max-width: 260px;" />
        <el-select placeholder="状态" style="width: 140px;"><el-option label="全部" value="all" /></el-select>
      </div>
      <el-table :data="users">
        <el-table-column prop="nickname" label="昵称" min-width="140" />
        <el-table-column prop="userNo" label="用户编号" min-width="160" />
        <el-table-column prop="gender" label="性别" width="100" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="lastLogin" label="最近登录时间" width="180" />
        <el-table-column label="操作" width="160">
          <template #default>
            <el-button link type="primary">查看详情</el-button>
            <el-button link type="danger">禁用</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { users } from '../../mock/data'
</script>
