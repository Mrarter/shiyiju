<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">作品管理</div>
        <div class="page-subtitle">管理作品资料、价格、库存和上架状态</div>
      </div>
      <el-button type="primary" @click="startCreate">新建作品</el-button>
    </div>

    <div class="section-card" style="padding: 20px; margin-bottom: 16px;">
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索作品名/艺术家" style="max-width: 260px;" />
      </div>
      <el-table :data="filteredArtworks">
        <el-table-column prop="name" label="作品名" min-width="180" />
        <el-table-column prop="artist" label="艺术家" width="140" />
        <el-table-column prop="price" label="价格" width="120" />
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="tag" label="推荐标签" width="140" />
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button link type="primary" @click="startEdit(row)">编辑</el-button>
            <el-button link @click="changeStatus(row, 'ONLINE')">上架</el-button>
            <el-button link type="danger" @click="changeStatus(row, 'OFFLINE')">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="section-card" style="padding: 24px;">
      <div class="page-title" style="font-size: 18px;">{{ editingId ? '编辑作品' : '新建作品' }}</div>
      <div class="form-grid" style="margin-top: 16px;">
        <el-input v-model="form.name" placeholder="作品名称" />
        <el-select v-model="form.artistId" placeholder="所属艺术家">
          <el-option
            v-for="artist in artists"
            :key="artist.id"
            :label="artist.name"
            :value="artist.id"
          />
        </el-select>
        <el-select v-model="form.status" placeholder="状态">
          <el-option label="草稿" value="DRAFT" />
          <el-option label="上架" value="PUBLISHED" />
          <el-option label="下架" value="OFF_SHELF" />
        </el-select>
        <el-input v-model.number="form.price" placeholder="发售价格" />
        <el-input v-model.number="form.stock" placeholder="库存" />
      </div>
      <el-input
        v-model="form.description"
        type="textarea"
        :rows="5"
        placeholder="作品描述"
        style="margin-top: 16px;"
      />
      <div style="margin-top: 16px;">
        <el-button @click="resetForm">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存作品</el-button>
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
const { artworks: artworksState, artists: artistsState } = storeToRefs(adminStore)
const keyword = ref('')
const saving = ref(false)
const editingId = ref(null)
const form = reactive({
  name: '',
  artistId: null,
  status: 'DRAFT',
  price: 0,
  stock: 1,
  description: ''
})

const artworks = computed(() => artworksState.value)
const artists = computed(() => artistsState.value)
const filteredArtworks = computed(() => {
  const q = keyword.value.trim()
  if (!q) return artworks.value
  return artworks.value.filter((item) => (item.name || '').includes(q) || (item.artist || '').includes(q))
})

function resetForm() {
  editingId.value = null
  form.name = ''
  form.artistId = artists.value[0]?.id || null
  form.status = 'DRAFT'
  form.price = 0
  form.stock = 1
  form.description = ''
}

function startCreate() {
  resetForm()
}

function startEdit(row) {
  editingId.value = row.id
  form.name = row.name || ''
  form.artistId = row.artistId || artists.value.find((artist) => artist.name === row.artist)?.id || null
  form.status = normalizeArtworkStatus(row.status)
  form.price = parsePrice(row.price)
  form.stock = Number(row.stock || 0)
  form.description = row.description || ''
}

function normalizeArtworkStatus(status) {
  const map = {
    上架: 'PUBLISHED',
    下架: 'OFF_SHELF',
    草稿: 'DRAFT',
    已收藏: 'COLLECTED',
    售罄: 'SOLD_OUT',
    PUBLISHED: 'PUBLISHED',
    OFF_SHELF: 'OFF_SHELF',
    DRAFT: 'DRAFT'
  }
  return map[status] || 'DRAFT'
}

function parsePrice(price) {
  if (typeof price === 'number') return price
  const numeric = Number(String(price || '').replace(/[^\d.]/g, ''))
  return Number.isFinite(numeric) ? numeric : 0
}

async function submitForm() {
  if (!form.name.trim()) {
    ElMessage.warning('请输入作品名称')
    return
  }
  if (!form.artistId) {
    ElMessage.warning('请选择所属艺术家')
    return
  }
  if (Number(form.price || 0) <= 0) {
    ElMessage.warning('请输入有效价格')
    return
  }
  saving.value = true
  try {
    await adminStore.saveArtwork(editingId.value, {
      name: form.name.trim(),
      artistId: form.artistId,
      price: Number(form.price),
      stock: Number(form.stock || 0),
      status: form.status,
      description: form.description.trim()
    })
    ElMessage.success(editingId.value ? '作品已更新' : '作品已创建')
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
      ElMessage.warning('当前作品缺少ID，暂时无法更新状态')
      return
    }
    await adminStore.changeArtworkStatus(id, status)
    ElMessage.success(status === 'ONLINE' ? '作品已上架' : '作品已下架')
  } catch (error) {
    ElMessage.error(error.message || '状态更新失败')
  }
}

onMounted(async () => {
  if (!artistsState.value.length) await adminStore.loadArtists()
  if (!artworksState.value.length) await adminStore.loadArtworks()
  if (!form.artistId) form.artistId = artists.value[0]?.id || null
})
</script>
