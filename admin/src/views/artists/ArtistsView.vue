<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">艺术家管理</div>
        <div class="page-subtitle">维护艺术家资料、排序和作品关联</div>
      </div>
      <el-button type="primary">新建艺术家</el-button>
    </div>

    <div class="section-card" style="padding: 20px;">
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
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button link type="primary">编辑</el-button>
            <el-button link @click="changeStatus(row, 'ONLINE')">上线</el-button>
            <el-button link type="danger" @click="changeStatus(row, 'OFFLINE')">下线</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '../../stores/admin'

const adminStore = useAdminStore()
const { artists: artistsState } = storeToRefs(adminStore)
const keyword = ref('')
const artists = computed(() => artistsState.value)
const filteredArtists = computed(() => {
  const q = keyword.value.trim()
  if (!q) return artists.value
  return artists.value.filter((item) => (item.name || '').includes(q) || (item.tags || '').includes(q))
})

async function changeStatus(row, status) {
  try {
    const id = row.id || row.artistId || extractArtistId(row)
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

function extractArtistId(row) {
  if (!row?.name) return null
  const map = {
    '林观山': 3001,
    '周岚': 3002,
    '陈河': 3003
  }
  return map[row.name] || null
}

onMounted(async () => {
  if (!artistsState.value.length) await adminStore.loadArtists()
})
</script>
