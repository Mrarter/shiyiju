<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">订单管理</div>
        <div class="page-subtitle">跟踪订单状态、支付状态和发货进度</div>
      </div>
      <el-button>导出订单</el-button>
    </div>

    <div class="section-card" style="padding: 20px; margin-bottom: 16px;">
      <div class="toolbar">
        <el-input placeholder="搜索订单号/用户" style="max-width: 260px;" />
        <el-select placeholder="订单状态" style="width: 160px;"><el-option label="全部" value="all" /></el-select>
        <el-select placeholder="发货状态" style="width: 160px;"><el-option label="全部" value="all" /></el-select>
      </div>
      <el-table :data="orders">
        <el-table-column prop="orderNo" label="订单号" min-width="200" />
        <el-table-column prop="user" label="用户" width="120" />
        <el-table-column prop="artwork" label="作品" min-width="160" />
        <el-table-column prop="amount" label="金额" width="120" />
        <el-table-column prop="status" label="订单状态" width="120" />
        <el-table-column prop="payStatus" label="支付状态" width="120" />
        <el-table-column prop="shipStatus" label="发货状态" width="120" />
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button link type="primary">详情</el-button>
            <el-button link>发货</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="section-card" style="padding: 24px;">
      <div class="page-title" style="font-size: 18px;">发货操作示例</div>
      <div class="form-grid" style="margin-top: 16px;">
        <el-input placeholder="物流公司" />
        <el-input placeholder="物流单号" />
      </div>
      <el-input type="textarea" :rows="4" placeholder="运营备注" style="margin-top: 16px;" />
      <div style="margin-top: 16px;">
        <el-button>取消</el-button>
        <el-button type="primary">更新为已发货</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useAdminStore } from '../../stores/admin'

const adminStore = useAdminStore()
const { orders: ordersState } = storeToRefs(adminStore)
const orders = computed(() => ordersState.value)

onMounted(async () => {
  if (!ordersState.value.length) await adminStore.loadOrders()
})
</script>
