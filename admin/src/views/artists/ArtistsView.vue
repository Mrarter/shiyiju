<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">艺术家管理</div>
        <div class="page-subtitle">维护艺术家资料、排序和作品关联</div>
      </div>
      <el-button type="primary" @click="startCreate">新建艺术家</el-button>
    </div>

    <div class="section-card" style="padding: 20px; margin-bottom: 16px;">
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索艺术家姓名/标签" style="max-width: 260px;" />
      </div>
      <el-table :data="filteredArtists">
        <el-table-column prop="name" label="艺术家姓名" min-width="160" />
        <el-table-column prop="city" label="城市" width="120" />
        <el-table-column prop="tags" label="标签" min-width="180" />
        <el-table-column prop="works" label="作品数" width="100" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button link type="primary" @click="startEdit(row)">编辑</el-button>
            <el-button link @click="changeStatus(row, 'ONLINE')">上线</el-button>
            <el-button link type="danger" @click="changeStatus(row, 'OFFLINE')">下线</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="section-card" style="padding: 24px;">
      <div class="page-title" style="font-size: 18px;">{{ editingId ? '编辑艺术家' : '新建艺术家' }}</div>
      <div class="form-grid" style="margin-top: 16px;">
        <el-input v-model="form.name" placeholder="艺术家名称" />
        <el-input v-model="form.tags" placeholder="风格标签，逗号分隔" />
        <el-input v-model.number="form.works" placeholder="作品数" />
        <el-select v-model="form.status" placeholder="状态">
          <el-option label="上线" value="ONLINE" />
          <el-option label="下线" value="OFFLINE" />
        </el-select>
      </div>
      <div style="margin-top: 16px;">
        <el-button @click="resetForm">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存艺术家</el-button>
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
const { artists: artistsState } = storeToRefs(adminStore)
const keyword = ref('')
const saving = ref(false)
const editingId = ref(null)
const form = reactive({
  name: '',
  tags: '',
  works: 0,
  status: 'ONLINE'
})

const artists = computed(() => artistsState.value)
const filteredArtists = computed(() => {
  const q = keyword.value.trim()
  if (!q) return artists.value
  return artists.value.filter((item) => (item.name || '').includes(q) || (item.tags || '').includes(q))
})

function resetForm() {
  editingId.value = null
  form.name = ''
  form.tags = ''
  form.works = 0
  form.status = 'ONLINE'
}

function startCreate() {
  resetForm()
}

function startEdit(row) {
  editingId.value = row.id
  form.name = row.name || ''
  form.tags = row.tags || ''
  form.works = Number(row.works || 0)
  form.status = normalizeArtistStatus(row.status)
}

function normalizeArtistStatus(status) {
  if (status === '上线' || status === 'ACTIVE' || status === 'ONLINE') return 'ONLINE'
  return 'OFFLINE'
}

async function submitForm() {
  if (!form.name.trim()) {
    ElMessage.warning('请输入艺术家名称')
    return
  }
  saving.value = true
  try {
    await adminStore.saveArtist(editingId.value, {
      name: form.name.trim(),
      tags: form.tags.trim(),
      works: Number(form.works || 0),
      status: form.status
    })
    ElMessage.success(editingId.value ? '艺术家已更新' : '艺术家已创建')
    resetForm()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function changeStatus(row, status) {
  try {
    const id = row.id
    if (!id) {
      ElMessage.warning('当前艺术家缺少ID，暂时无法更新状态')
      return
    }
    await adminStore.changeArtistStatus(id, status)
    ElMessage.success(status === 'ONLINE' ? '艺术家已上线' : '艺术家已下线')
  } catch (error) {
    ElMessage.error(error.message || '状态更新失败')
  }
}

onMounted(async () => {
  if (!artistsState.value.length) await adminStore.loadArtists()
})
</script>
