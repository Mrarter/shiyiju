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
        <el-input placeholder="搜索作品名/艺术家" style="max-width: 260px;" />
        <el-select placeholder="状态" style="width: 140px;"><el-option label="全部" value="all" /></el-select>
      </div>
      <el-table :data="artworks">
        <el-table-column prop="name" label="作品名" min-width="180" />
        <el-table-column prop="artist" label="艺术家" width="140" />
        <el-table-column prop="price" label="价格" width="120" />
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="tag" label="推荐标签" width="140" />
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button link type="primary">编辑</el-button>
            <el-button link>上架</el-button>
            <el-button link type="danger">下架</el-button>
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
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useAdminStore } from '../../stores/admin'

const adminStore = useAdminStore()
const { artworks: artworksState } = storeToRefs(adminStore)
const artworks = computed(() => artworksState.value)

onMounted(async () => {
  if (!artworksState.value.length) await adminStore.loadArtworks()
})
</script>
