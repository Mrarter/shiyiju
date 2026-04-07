<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">控制台</div>
        <div class="page-subtitle">查看平台核心数据、待处理事项和快捷入口</div>
      </div>
      <el-button type="primary">导出日报</el-button>
    </div>

    <div class="metric-grid">
      <div v-for="item in dashboardMetrics" :key="item.label" class="section-card metric-card">
        <div class="metric-label">{{ item.label }}</div>
        <div class="metric-value">{{ item.value }}</div>
        <div class="page-subtitle">{{ item.delta }}</div>
      </div>
    </div>

    <el-row :gutter="16" style="margin-top: 16px;">
      <el-col :span="14">
        <div class="section-card" style="padding: 20px;">
          <div class="page-title" style="font-size: 18px;">待处理事项</div>
          <el-timeline style="margin-top: 16px;">
            <el-timeline-item timestamp="高优先级">补齐首页推荐与艺术家展示数据</el-timeline-item>
            <el-timeline-item timestamp="中优先级">修正首页空数据误显示为加载失败</el-timeline-item>
            <el-timeline-item timestamp="中优先级">补齐订单联调后的后台处理动作</el-timeline-item>
          </el-timeline>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="section-card" style="padding: 20px;">
          <div class="page-title" style="font-size: 18px;">快捷入口</div>
          <div style="display: grid; gap: 12px; margin-top: 16px;">
            <el-button plain>新建艺术家</el-button>
            <el-button plain>新建作品</el-button>
            <el-button plain>配置首页推荐</el-button>
            <el-button plain>查看待发货订单</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { dashboardMetrics } from '../../mock/data'
</script>
