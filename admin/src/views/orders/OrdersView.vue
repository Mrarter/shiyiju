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
        <el-input v-model="keyword" placeholder="搜索订单号/用户" style="max-width: 260px;" />
      </div>
      <el-table :data="filteredOrders">
        <el-table-column prop="orderNo" label="订单号" min-width="200" />
        <el-table-column prop="user" label="用户" width="120" />
        <el-table-column prop="artwork" label="作品" min-width="160" />
        <el-table-column prop="amount" label="金额" width="120" />
        <el-table-column prop="status" label="订单状态" width="120" />
        <el-table-column prop="payStatus" label="支付状态" width="120" />
        <el-table-column prop="shipStatus" label="发货状态" width="120" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button link type="primary" @click="selectOrder(row)">详情</el-button>
            <el-button link @click="prefillShipment(row)">发货</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="section-card" style="padding: 24px;">
      <div class="page-title" style="font-size: 18px;">订单处理</div>
      <div class="page-subtitle">当前订单：{{ currentOrder?.orderNo || '未选择' }}</div>
      <div class="form-grid" style="margin-top: 16px;">
        <el-input v-model="shipment.company" placeholder="物流公司" />
        <el-input v-model="shipment.trackingNo" placeholder="物流单号" />
      </div>
      <el-input v-model="remark.remark" type="textarea" :rows="4" placeholder="运营备注" style="margin-top: 16px;" />
      <div style="margin-top: 16px; display: flex; gap: 12px;">
        <el-button @click="resetForms">取消</el-button>
        <el-button type="primary" @click="saveShipment">更新为已发货</el-button>
        <el-button @click="saveRemark">保存备注</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '../../stores/admin'

const adminStore = useAdminStore()
const { orders: ordersState } = storeToRefs(adminStore)
const keyword = ref('')
const currentOrder = ref(null)
const shipment = reactive({ company: '顺丰', trackingNo: '' })
const remark = reactive({ remark: '' })
const orders = computed(() => ordersState.value)
const filteredOrders = computed(() => {
  const q = keyword.value.trim()
  if (!q) return orders.value
  return orders.value.filter((item) => (item.orderNo || '').includes(q) || (item.user || '').includes(q))
})

function selectOrder(row) {
  currentOrder.value = row
}

function prefillShipment(row) {
  currentOrder.value = row
  shipment.company = '顺丰'
  shipment.trackingNo = ''
}

function resetForms() {
  shipment.company = '顺丰'
  shipment.trackingNo = ''
  remark.remark = ''
}

function extractOrderId(row) {
  if (!row?.orderNo) return null
  const map = {
    'SYJ202604070001': 5001,
    'SYJ202604070002': 5002,
    'SYJ202604060018': 5003
  }
  return map[row.orderNo] || null
}

async function saveShipment() {
  try {
    const id = currentOrder.value?.id || extractOrderId(currentOrder.value)
    if (!id) {
      ElMessage.warning('请先选择订单')
      return
    }
    await adminStore.saveOrderShipment(id, { company: shipment.company, trackingNo: shipment.trackingNo })
    ElMessage.success('发货信息已更新')
  } catch (error) {
    ElMessage.error(error.message || '发货更新失败')
  }
}

async function saveRemark() {
  try {
    const id = currentOrder.value?.id || extractOrderId(currentOrder.value)
    if (!id) {
      ElMessage.warning('请先选择订单')
      return
    }
    await adminStore.saveOrderRemark(id, { remark: remark.remark })
    ElMessage.success('备注已保存')
  } catch (error) {
    ElMessage.error(error.message || '备注保存失败')
  }
}

onMounted(async () => {
  if (!ordersState.value.length) await adminStore.loadOrders()
})
</script>
