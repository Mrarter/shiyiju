<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">控制台</div>
        <div class="page-subtitle">查看平台核心数据、待处理事项和快捷入口</div>
      </div>
      <el-button type="primary" @click="exportReport">导出日报</el-button>
    </div>

    <div class="metric-grid">
      <div v-for="item in metrics" :key="item.label" class="section-card metric-card">
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
            <el-timeline-item v-for="item in todos" :key="item" timestamp="待处理">{{ item }}</el-timeline-item>
          </el-timeline>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="section-card" style="padding: 20px;">
          <div class="page-title" style="font-size: 18px;">当前接口能力</div>
          <div style="display: grid; gap: 10px; margin-top: 16px; font-size: 13px; color: #646a73;">
            <div v-for="(value, key) in capabilities" :key="key">
              <strong style="color:#1f2329;">{{ key }}</strong>
              <div>{{ value }}</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '../../stores/admin'
import { downloadText } from '../../utils/download'

const adminStore = useAdminStore()
const { dashboard, capabilities } = storeToRefs(adminStore)

const metrics = computed(() => dashboard.value?.metrics || [])
const todos = computed(() => dashboard.value?.todos || [])

function exportReport() {
  const now = new Date()
  const dateText = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
  const content = [
    `拾艺局运营后台日报`,
    `日期：${dateText}`,
    '',
    '核心指标：',
    ...metrics.value.map((item) => `- ${item.label}：${item.value}（${item.delta}）`),
    '',
    '待处理事项：',
    ...todos.value.map((item) => `- ${item}`)
  ].join('\n')
  downloadText(`shiyiju-dashboard-${dateText}.txt`, content)
  ElMessage.success('日报已导出')
}

onMounted(async () => {
  if (!dashboard.value) await adminStore.loadDashboard()
  if (!capabilities.value) await adminStore.loadCapabilities()
})
</script>
