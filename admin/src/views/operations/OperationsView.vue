<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">首页运营</div>
        <div class="page-subtitle">管理 Banner、热门藏品、正在升值和推荐艺术家</div>
      </div>
      <el-button type="primary">新增推荐位</el-button>
    </div>

    <div class="section-card" style="padding: 20px; margin-bottom: 16px;">
      <div class="toolbar">
        <el-input placeholder="搜索标题/关联对象" style="max-width: 260px;" />
        <el-select placeholder="状态" style="width: 140px;"><el-option label="全部" value="all" /></el-select>
        <el-select placeholder="类型" style="width: 160px;"><el-option label="Banner" value="banner" /></el-select>
      </div>
      <el-table :data="operationItems">
        <el-table-column prop="title" label="标题" min-width="220" />
        <el-table-column prop="type" label="类型" width="140" />
        <el-table-column prop="target" label="关联对象" min-width="180" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="updatedAt" label="更新时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button link type="primary">编辑</el-button>
            <el-button link>预览</el-button>
            <el-button link type="danger">停用</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="section-card" style="padding: 24px;">
      <div class="page-title" style="font-size: 18px;">推荐位编辑示例</div>
      <div class="form-grid" style="margin-top: 16px;">
        <el-input placeholder="标题" />
        <el-input placeholder="副标题" />
        <el-select placeholder="关联对象类型"><el-option label="作品" value="artwork" /></el-select>
        <el-input placeholder="推荐语" />
        <el-input placeholder="排序" />
        <el-date-picker type="datetimerange" start-placeholder="开始时间" end-placeholder="结束时间" />
      </div>
      <div style="margin-top: 16px;">
        <el-button>取消</el-button>
        <el-button type="primary">保存并发布</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { operationItems } from '../../mock/data'
</script>
