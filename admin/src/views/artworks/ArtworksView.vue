<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">作品管理</div>
        <div class="page-subtitle">管理作品资料、价格、库存和上架状态</div>
      </div>
      <el-button type="primary">新建作品</el-button>
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
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button link type="primary">编辑</el-button>
            <el-button link @click="changeStatus(row, 'ONLINE')">上架</el-button>
            <el-button link type="danger" @click="changeStatus(row, 'OFFLINE')">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="section-card" style="padding: 24px;">
      <div class="page-title" style="font-size: 18px;">作品编辑示例</div>
      <div class="form-grid" style="margin-top: 16px;">
        <el-input placeholder="作品名称" />
        <el-input placeholder="作品编号" />
        <el-select placeholder="所属艺术家"><el-option label="林观山" value="1" /></el-select>
        <el-select placeholder="状态"><el-option label="上架" value="1" /></el-select>
        <el-input placeholder="发售价格" />
        <el-input placeholder="库存" />
      </div>
      <el-input type="textarea" :rows="5" placeholder="作品描述" style="margin-top: 16px;" />
      <div style="margin-top: 16px;">
        <el-button>取消</el-button>
        <el-button type="primary">保存并上架</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '../../stores/admin'

const adminStore = useAdminStore()
const { artworks: artworksState } = storeToRefs(adminStore)
const keyword = ref('')
const artworks = computed(() => artworksState.value)
const filteredArtworks = computed(() => {
  const q = keyword.value.trim()
  if (!q) return artworks.value
  return artworks.value.filter((item) => (item.name || '').includes(q) || (item.artist || '').includes(q))
})

async function changeStatus(row, status) {
  try {
    const id = row.id || row.artworkId || extractArtworkId(row)
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

function extractArtworkId(row) {
  if (!row?.name) return null
  const map = {
    '春山可望': 4001,
    '潮汐笔记': 4002,
    '园林记忆': 4003
  }
  return map[row.name] || null
}

onMounted(async () => {
  if (!artworksState.value.length) await adminStore.loadArtworks()
})
</script>
