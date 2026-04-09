<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">订单管理</div>
        <div class="page-subtitle">跟踪订单状态、支付状态和发货进度</div>
      </div>
      <el-button @click="exportOrders">导出订单</el-button>
    </div>

    <div class="section-card" style="padding: 20px; margin-bottom: 16px;">
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索订单号/用户" style="max-width: 260px;" />
      </div>
      <el-table :data="filteredOrders">
        <el-table-column label="订单号" min-width="200">
          <template #default="{ row }">
            <span class="clickable-text" @click="openEditDialog(row)">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="user" label="用户" width="120" />
        <el-table-column prop="artwork" label="作品" min-width="160" />
        <el-table-column prop="amount" label="金额" width="120" />
        <el-table-column prop="status" label="订单状态" width="120" />
        <el-table-column prop="payStatus" label="支付状态" width="120" />
        <el-table-column prop="shipStatus" label="发货状态" width="120" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button link @click="openEditDialog(row)">编辑</el-button>
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

    <el-drawer v-model="detailVisible" title="订单详情" size="460px">
      <div v-if="currentOrder" style="display: grid; gap: 14px;">
        <div><strong>订单号：</strong>{{ currentOrder.orderNo }}</div>
        <div><strong>用户：</strong>{{ currentOrder.user }}</div>
        <div><strong>作品：</strong>{{ currentOrder.artwork }}</div>
        <div><strong>金额：</strong>{{ currentOrder.amount }}</div>
        <div><strong>订单状态：</strong>{{ currentOrder.status }}</div>
        <div><strong>支付状态：</strong>{{ currentOrder.payStatus }}</div>
        <div><strong>发货状态：</strong>{{ currentOrder.shipStatus }}</div>
        <div><strong>备注：</strong>{{ currentOrder.remark || '无' }}</div>
      </div>
    </el-drawer>

    <!-- 编辑订单对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑订单" width="500px" :close-on-click-modal="true">
      <div v-if="editForm.orderNo" style="display: grid; gap: 16px;">
        <div class="edit-info-row">
          <span class="edit-label">订单号：</span>
          <span>{{ editForm.orderNo }}</span>
        </div>
        <div class="edit-info-row">
          <span class="edit-label">用户：</span>
          <span>{{ editForm.user }}</span>
        </div>
        <div class="edit-info-row">
          <span class="edit-label">作品：</span>
          <span>{{ editForm.artwork }}</span>
        </div>
        <div class="edit-info-row">
          <span class="edit-label">金额：</span>
          <span>{{ editForm.amount }}</span>
        </div>
        <el-divider />
        <div class="form-field">
          <label>物流公司</label>
          <el-input v-model="editForm.company" placeholder="请输入物流公司" />
        </div>
        <div class="form-field">
          <label>物流单号</label>
          <el-input v-model="editForm.trackingNo" placeholder="请输入物流单号" />
        </div>
        <div class="form-field">
          <label>备注</label>
          <el-input v-model="editForm.remark" type="textarea" :rows="3" placeholder="运营备注" />
        </div>
      </div>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button @click="saveFromDialog">保存备注</el-button>
        <el-button type="primary" :loading="saving" @click="shipFromDialog">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '../../stores/admin'
import { downloadCsv } from '../../utils/download'

const adminStore = useAdminStore()
const { orders: ordersState } = storeToRefs(adminStore)
const keyword = ref('')
const detailVisible = ref(false)
const editDialogVisible = ref(false)
const currentOrder = ref(null)
const saving = ref(false)
const shipment = reactive({ company: '顺丰', trackingNo: '' })
const remark = reactive({ remark: '' })
const editForm = reactive({
  id: null,
  orderNo: '',
  user: '',
  artwork: '',
  amount: '',
  company: '顺丰',
  trackingNo: '',
  remark: ''
})
const orders = computed(() => ordersState.value)
const filteredOrders = computed(() => {
  const q = keyword.value.trim()
  if (!q) return orders.value
  return orders.value.filter((item) => (item.orderNo || '').includes(q) || (item.user || '').includes(q))
})

function selectOrder(row) {
  currentOrder.value = row
}

function openDetail(row) {
  selectOrder(row)
  detailVisible.value = true
}

function openEditDialog(row) {
  editForm.id = row.id
  editForm.orderNo = row.orderNo || ''
  editForm.user = row.user || ''
  editForm.artwork = row.artwork || ''
  editForm.amount = row.amount || ''
  editForm.company = row.company || '顺丰'
  editForm.trackingNo = row.trackingNo || ''
  editForm.remark = row.remark || ''
  editDialogVisible.value = true
}

function prefillShipment(row) {
  currentOrder.value = row
  shipment.company = '顺丰'
  shipment.trackingNo = ''
  remark.remark = row.remark || ''
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

function getOrderId(row) {
  return row?.id || extractOrderId(row)
}

async function shipFromDialog() {
  saving.value = true
  try {
    const id = getOrderId(editForm)
    if (!id) {
      ElMessage.warning('订单ID无效')
      return
    }
    await adminStore.saveOrderShipment(id, { company: editForm.company, trackingNo: editForm.trackingNo })
    if (editForm.remark) {
      await adminStore.saveOrderRemark(id, { remark: editForm.remark })
    }
    ElMessage.success('发货信息已更新')
    editDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '发货更新失败')
  } finally {
    saving.value = false
  }
}

async function saveFromDialog() {
  saving.value = true
  try {
    const id = getOrderId(editForm)
    if (!id) {
      ElMessage.warning('订单ID无效')
      return
    }
    await adminStore.saveOrderRemark(id, { remark: editForm.remark })
    ElMessage.success('备注已保存')
    editDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '备注保存失败')
  } finally {
    saving.value = false
  }
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

function exportOrders() {
  downloadCsv(
    'shiyiju-orders.csv',
    ['订单号', '用户', '作品', '金额', '订单状态', '支付状态', '发货状态'],
    filteredOrders.value.map((item) => [item.orderNo, item.user, item.artwork, item.amount, item.status, item.payStatus, item.shipStatus])
  )
  ElMessage.success('订单列表已导出')
}

onMounted(async () => {
  if (!ordersState.value.length) await adminStore.loadOrders()
})
</script>

<style scoped>
.clickable-text {
  cursor: pointer;
  color: var(--el-color-primary);
}
.clickable-text:hover {
  text-decoration: underline;
}
.edit-info-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.edit-label {
  color: var(--el-text-color-secondary);
  flex-shrink: 0;
}
.form-field {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}
.form-field label {
  width: 70px;
  flex-shrink: 0;
  color: var(--el-text-color-regular);
  font-size: 14px;
  line-height: 32px;
}
.form-field .el-input,
.form-field .el-select {
  flex: 1;
}
</style>
