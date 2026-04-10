<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">艺术家管理</div>
        <div class="page-subtitle">维护艺术家资料、排序和作品关联</div>
      </div>
      <el-button type="primary" @click="startCreate">新建艺术家</el-button>
    </div>

    <div class="section-card" style="padding: 20px;">
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索艺术家姓名/标签" style="max-width: 260px;" />
      </div>
      <el-table ref="tableRef" :data="filteredArtists" :key="tableKey" row-key="id">
        <el-table-column label="头像" width="110">
          <template #default="{ row }">
            <el-avatar v-if="row.avatarUrl" :src="getImageUrl(row.avatarUrl)" :size="44" class="clickable-avatar" @click="startEdit(row)" />
            <span v-else class="clickable-text" @click="startEdit(row)">未上传</span>
          </template>
        </el-table-column>
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

    <!-- 新建/编辑艺术家对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑艺术家' : '新建艺术家'"
      width="600px"
      :close-on-click-modal="true"
      destroy-on-close
    >
      <div style="margin-bottom: 16px;">
        <CropUploadField
          v-model="form.avatarUrl"
          placeholder="艺术家头像"
          :crop-ratio="1"
          tip="建议上传 1:1 正方形头像图，最大 20MB"
        />
      </div>
      <div class="form-grid">
        <div class="form-field">
          <div class="form-label">艺术家姓名</div>
          <el-input v-model="form.name" placeholder="请输入艺术家真实姓名或艺名" />
        </div>
        <div class="form-field">
          <div class="form-label">所在城市</div>
          <el-input v-model="form.city" placeholder="如：杭州、上海" />
        </div>
        <div class="form-field">
          <div class="form-label">风格标签</div>
          <el-input v-model="form.tags" placeholder="多个用逗号分隔，如：油画,当代,抽象" />
        </div>
        <div class="form-field">
          <div class="form-label">状态</div>
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
            <el-option label="上线（在平台展示）" value="ONLINE" />
            <el-option label="下线（暂停展示）" value="OFFLINE" />
          </el-select>
        </div>
      </div>
      <div class="form-grid" style="margin-top: 16px;">
        <div class="form-field">
          <div class="form-label">作品数量</div>
          <el-input-number v-model="form.works" :min="0" placeholder="已收录作品数量" />
        </div>
        <div class="form-field">
          <div class="form-label">排序权重</div>
          <el-input-number v-model="form.sort" :min="0" placeholder="数字越小越靠前" />
        </div>
        <div class="form-field">
          <div class="form-label">年龄</div>
          <el-input-number v-model="form.age" :min="0" :max="120" placeholder="艺术家年龄" />
        </div>
        <div class="form-field">
          <div class="form-label">毕业院校</div>
          <el-input v-model="form.graduatedFrom" placeholder="如：中央美术学院" />
        </div>
      </div>
      <div style="margin-top: 16px;">
        <div class="form-label">艺术家简介</div>
        <el-input v-model="form.bio" type="textarea" :rows="3" placeholder="请输入艺术家简介、创作理念等" />
      </div>
      <div style="margin-top: 16px;">
        <div class="form-label">获奖经历</div>
        <el-input v-model="form.awards" type="textarea" :rows="2" placeholder="请输入主要获奖经历，如：2020年获XX奖" />
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存艺术家</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '../../stores/admin'
import CropUploadField from '../../components/CropUploadField.vue'
import { getImageUrl } from '../../utils/imageUrl'

const adminStore = useAdminStore()
const { artists: artistsState } = storeToRefs(adminStore)
const keyword = ref('')
const saving = ref(false)
const editingId = ref(null)
const dialogVisible = ref(false)
const tableKey = ref(0)
const tableRef = ref(null)
const form = reactive({
  name: '',
  city: '',
  tags: '',
  avatarUrl: '',
  works: 0,
  sort: 0,
  status: 'ONLINE',
  bio: '',
  graduatedFrom: '',
  awards: '',
  age: null
})

// 未保存内容暂存键名
const STORAGE_KEY = 'admin_artist_draft'
const EXPIRE_TIME = 5 * 60 * 1000 // 5分钟

// 保存草稿到 sessionStorage
function saveDraft() {
  const draft = {
    id: editingId.value,
    data: { ...form },
    savedAt: Date.now()
  }
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(draft))
}

// 从 sessionStorage 恢复草稿
function restoreDraft() {
  const saved = sessionStorage.getItem(STORAGE_KEY)
  if (!saved) return false
  try {
    const draft = JSON.parse(saved)
    // 检查是否过期
    if (Date.now() - draft.savedAt > EXPIRE_TIME) {
      sessionStorage.removeItem(STORAGE_KEY)
      return false
    }
    // 恢复数据
    editingId.value = draft.id
    Object.assign(form, draft.data)
    return true
  } catch {
    sessionStorage.removeItem(STORAGE_KEY)
    return false
  }
}

// 清除草稿
function clearDraft() {
  sessionStorage.removeItem(STORAGE_KEY)
}

// 监听弹窗关闭前，保存草稿
watch(dialogVisible, (val) => {
  if (!val) {
    // 检查表单是否有内容
    const hasContent = form.name || form.city || form.tags || form.avatarUrl
    if (hasContent && !saving.value) {
      saveDraft()
    }
  }
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
  form.city = ''
  form.tags = ''
  form.avatarUrl = ''
  form.works = 0
  form.sort = 0
  form.status = 'ONLINE'
  form.bio = ''
  form.graduatedFrom = ''
  form.awards = ''
  form.age = null
}

function startCreate() {
  resetForm()
  // 尝试恢复草稿
  if (restoreDraft()) {
    ElMessage.info('已恢复未保存的内容')
  }
  dialogVisible.value = true
}

function startEdit(row) {
  editingId.value = row.id
  form.name = row.name || ''
  form.city = row.city || ''
  form.tags = row.tags || ''
  form.avatarUrl = row.avatarUrl || ''
  form.works = Number(row.works || 0)
  form.sort = Number(row.sort || 0)
  form.status = normalizeArtistStatus(row.status)
  form.bio = row.bio || ''
  form.graduatedFrom = row.graduatedFrom || ''
  form.awards = row.awards || ''
  form.age = row.age || null
  dialogVisible.value = true
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
  if (!form.status) {
    form.status = 'ONLINE'
  }
  saving.value = true
  try {
    const payload = {
      name: form.name.trim(),
      city: form.city?.trim() || '',
      tags: form.tags?.trim() || '',
      avatarUrl: form.avatarUrl || '',
      works: Number(form.works ?? 0),
      sort: Number(form.sort ?? 0),
      status: form.status || 'ONLINE',
      bio: form.bio?.trim() || '',
      graduatedFrom: form.graduatedFrom?.trim() || '',
      awards: form.awards?.trim() || '',
      age: form.age ?? null
    }
    console.log('保存艺术家:', editingId.value ? '更新' : '新建', payload)
    const editId = editingId.value
    await adminStore.saveArtist(editId, payload)
    tableKey.value++ // 强制刷新表格
    await nextTick()
    ElMessage.success(editId ? '艺术家已更新' : '艺术家已创建')
    clearDraft()
    dialogVisible.value = false
    resetForm()
  } catch (error) {
    console.error('保存失败:', error)
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

<style scoped>
.clickable-avatar {
  cursor: pointer;
  transition: opacity 0.2s;
}
.clickable-avatar:hover {
  opacity: 0.8;
}
.clickable-text {
  cursor: pointer;
  color: var(--el-color-primary);
}
.clickable-text:hover {
  text-decoration: underline;
}
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.form-label {
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}
</style>
