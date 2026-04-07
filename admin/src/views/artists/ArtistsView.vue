<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">艺术家管理</div>
        <div class="page-subtitle">维护艺术家资料、排序和作品关联</div>
      </div>
      <el-button type="primary">新建艺术家</el-button>
    </div>

    <div class="section-card" style="padding: 20px;">
      <div class="toolbar">
        <el-input placeholder="搜索艺术家姓名/标签" style="max-width: 260px;" />
        <el-select placeholder="状态" style="width: 140px;"><el-option label="全部" value="all" /></el-select>
      </div>
      <el-table :data="artists">
        <el-table-column prop="name" label="艺术家姓名" min-width="160" />
        <el-table-column prop="city" label="城市" width="120" />
        <el-table-column prop="tags" label="标签" min-width="180" />
        <el-table-column prop="works" label="作品数" width="100" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="操作" width="160">
          <template #default>
            <el-button link type="primary">编辑</el-button>
            <el-button link>查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { artists } from '../../mock/data'
</script>
