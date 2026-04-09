<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">首页运营</div>
        <div class="page-subtitle">管理 Banner、热门藏品、正在升值和推荐艺术家</div>
      </div>
      <el-button type="primary" @click="startCreate">新增推荐位</el-button>
    </div>

    <div class="section-card" style="padding: 20px;">
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索标题/关联对象" style="max-width: 260px;" />
      </div>
      <el-table :data="filteredItems">
        <el-table-column label="图片" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrl"
              :src="row.imageUrl"
              fit="cover"
              style="width: 72px; height: 48px; border-radius: 10px;"
              preview-teleported
            />
            <span v-else>未上传</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="target" label="关联对象" min-width="140" />
        <el-table-column prop="sortNo" label="权重" width="80" sortable>
          <template #default="{ row }">
            <el-input-number
              v-model="row.sortNo"
              :min="0"
              :max="999"
              size="small"
              controls-position="right"
              style="width: 70px;"
              @change="handleSortChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="160" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status !== 'ENABLED'" link type="success" @click="handleToggleStatus(row, 'ENABLED')">上线</el-button>
            <el-button v-if="row.status === 'ENABLED'" link type="warning" @click="handleToggleStatus(row, 'DISABLED')">下线</el-button>
            <el-button link type="primary" @click="startEdit(row)">编辑</el-button>
            <el-button link @click="previewOperation(row)">预览</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新建/编辑推荐位对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑推荐位' : '新增推荐位'"
      width="640px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div style="margin-bottom: 16px;">
        <UploadImageField v-model="form.imageUrl" placeholder="Banner / 推荐图" tip="建议上传横图，保存后会用于后台配置" />
      </div>
      <div class="form-grid">
        <el-input v-model="form.title" placeholder="标题" />
        <el-input v-model="form.target" placeholder="关联对象/说明" />
        <el-select v-model="form.type" placeholder="类型">
          <el-option label="Banner" value="BANNER" />
          <el-option label="热门藏品" value="HOT" />
          <el-option label="正在升值" value="GROWTH" />
          <el-option label="推荐艺术家" value="ARTIST" />
          <el-option label="公告" value="NOTICE" />
        </el-select>
        <el-select v-model="form.status" placeholder="状态">
          <el-option label="上线" value="ENABLED" />
          <el-option label="草稿" value="DRAFT" />
          <el-option label="下线" value="DISABLED" />
        </el-select>
      </div>
      <div style="margin-top: 16px;">
        <div style="margin-bottom: 4px; color: #666; font-size: 14px;">排序权重</div>
        <el-input-number v-model="form.sortNo" :min="0" :max="999" placeholder="值越小越靠前" />
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存并发布</el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" title="推荐位预览" width="520px">
      <div v-if="previewItem" style="display: grid; gap: 16px;">
        <el-image
          v-if="previewItem.imageUrl"
          :src="previewItem.imageUrl"
          fit="cover"
          style="width: 100%; height: 220px; border-radius: 14px;"
          preview-teleported
        />
        <div><strong>标题：</strong>{{ previewItem.title }}</div>
        <div><strong>类型：</strong>{{ previewItem.type }}</div>
        <div><strong>关联对象：</strong>{{ previewItem.target || '无' }}</div>
        <div><strong>状态：</strong>{{ previewItem.status }}</div>
        <div><strong>排序：</strong>{{ previewItem.sortNo ?? '-' }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '../../stores/admin'
import UploadImageField from '../../components/UploadImageField.vue'

const adminStore = useAdminStore()
const { operations } = storeToRefs(adminStore)
const keyword = ref('')
const saving = ref(false)
const editingId = ref(null)
const dialogVisible = ref(false)
const previewVisible = ref(false)
const previewItem = ref(null)
const form = reactive({
  title: '',
  type: 'BANNER',
  target: '',
  imageUrl: '',
  status: 'ENABLED',
  sortNo: 10
})

const operationItems = computed(() => operations.value)
const filteredItems = computed(() => {
  const q = keyword.value.trim()
  if (!q) return operationItems.value
  return operationItems.value.filter((item) => (item.title || '').includes(q) || (item.target || '').includes(q))
})

function getStatusType(status) {
  const map = {
    ENABLED: 'success',
    DRAFT: 'info',
    DISABLED: 'danger'
  }
  return map[status] || 'info'
}

function getStatusText(status) {
  const map = {
    ENABLED: '已上线',
    DRAFT: '草稿',
    DISABLED: '已下线'
  }
  return map[status] || status
}

async function handleToggleStatus(row, newStatus) {
  try {
    await adminStore.updateOperation(row.id, {
      title: row.title,
      type: row.type,
      target: row.target,
      imageUrl: row.imageUrl,
      status: newStatus,
      sortNo: row.sortNo || 0
    })
    row.status = newStatus
    ElMessage.success(newStatus === 'ENABLED' ? '已上线' : '已下线')
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

async function handleSortChange(row) {
  try {
    await adminStore.updateOperation(row.id, {
      title: row.title,
      type: row.type,
      target: row.target,
      imageUrl: row.imageUrl,
      status: row.status,
      sortNo: row.sortNo || 0
    })
    ElMessage.success('权重已更新')
  } catch (error) {
    ElMessage.error(error.message || '更新权重失败')
  }
}

function resetForm() {
  editingId.value = null
  form.title = ''
  form.type = 'BANNER'
  form.target = ''
  form.imageUrl = ''
  form.status = 'ENABLED'
  form.sortNo = 10
}

function startCreate() {
  resetForm()
  dialogVisible.value = true
}

function fillFrom(row) {
  form.title = row.title || ''
  form.type = normalizeType(row.type)
  form.target = row.target || ''
  form.imageUrl = row.imageUrl || ''
  form.status = normalizeStatus(row.status)
  form.sortNo = row.sortNo || 10
}

function startEdit(row) {
  editingId.value = row.id
  fillFrom(row)
  dialogVisible.value = true
}

function previewOperation(row) {
  previewItem.value = row
  previewVisible.value = true
}

function normalizeType(type) {
  const map = {
    Banner: 'BANNER',
    热门藏品: 'HOT',
    正在升值: 'GROWTH',
    推荐艺术家: 'ARTIST',
    公告: 'NOTICE'
  }
  return map[type] || type || 'BANNER'
}

function normalizeStatus(status) {
  const map = {
    启用: 'ENABLED',
    草稿: 'DRAFT',
    停用: 'DISABLED'
  }
  return map[status] || status || 'ENABLED'
}

async function submitForm() {
  saving.value = true
  try {
    const payload = {
      title: form.title,
      type: form.type,
      target: form.target,
      imageUrl: form.imageUrl,
      status: form.status,
      sortNo: Number(form.sortNo || 0)
    }
    if (editingId.value) {
      await adminStore.updateOperation(editingId.value, payload)
      ElMessage.success('推荐位已更新')
    } else {
      await adminStore.createOperation(payload)
      ElMessage.success('推荐位已创建')
    }
    dialogVisible.value = false
    resetForm()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  if (!operations.value.length) await adminStore.loadOperations()
})
</script>
