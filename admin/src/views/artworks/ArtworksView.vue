<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">作品管理</div>
        <div class="page-subtitle">管理作品资料、价格、库存和上架状态</div>
      </div>
      <div class="header-actions">
        <el-button @click="showBatchUploadDialog">
          <el-icon><Upload /></el-icon> 批量上传
        </el-button>
        <el-button type="primary" @click="showDialog">新建作品</el-button>
      </div>
    </div>

    <div class="section-card" style="padding: 20px;">
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索作品名/艺术家" style="max-width: 260px;" />
        <div class="toolbar-spacer"></div>
        <el-button
          v-if="selectedArtworks.length > 0"
          type="warning"
          plain
          @click="batchDownload"
        >
          <el-icon><Download /></el-icon> 下载选中 ({{ selectedArtworks.length }})
        </el-button>
        <el-button
          v-if="selectedArtworks.length > 0"
          type="success"
          plain
          @click="batchPublish"
        >
          <el-icon><Upload /></el-icon> 批量上架 ({{ selectedArtworks.length }})
        </el-button>
        <el-button
          v-if="selectedArtworks.length > 0"
          type="info"
          plain
          @click="clearSelection"
        >
          清除选择
        </el-button>
      </div>
      <el-table
        ref="tableRef"
        :data="filteredArtworks"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="adminWeight" label="权重" width="100" align="center">
          <template #default="{ row }">
            <div v-if="editingWeightId === row.id" class="weight-edit-cell">
              <el-input-number
                v-model="editingWeightValue"
                :min="0"
                :max="9999"
                size="small"
                controls-position="right"
                style="width: 70px;"
                @blur="saveWeight(row)"
                @keyup.enter="saveWeight(row)"
              />
            </div>
            <el-tag
              v-else
              type="warning"
              size="small"
              style="cursor: pointer; min-width: 40px; text-align: center;"
              @click="startEditWeight(row)"
            >
              {{ row.adminWeight || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="封面" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.coverUrl"
              :src="row.coverUrl"
              fit="cover"
              style="width: 64px; height: 64px; border-radius: 10px; cursor: pointer;"
              preview-teleported
              @click="startEdit(row)"
            />
            <span v-else class="clickable-text" @click="startEdit(row)">未上传</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="作品名" min-width="180">
          <template #default="{ row }">
            <span class="clickable-text" @click="startEdit(row)">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="艺术家" width="140">
          <template #default="{ row }">
            <span class="clickable-text" @click="showArtistDetail(row)">{{ row.artist }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="类别" width="100">
          <template #default="{ row }">
            {{ formatCategory(row.category) }}
          </template>
        </el-table-column>
        <el-table-column label="尺寸" width="120">
          <template #default="{ row }">
            <span v-if="row.widthCm && row.heightCm">{{ row.widthCm }}×{{ row.heightCm }}cm</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="creationYear" label="创作年份" width="100" />
        <el-table-column prop="price" label="价格" width="120" />
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="tag" label="标签" width="100" />
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button link type="primary" @click="startEdit(row)">编辑</el-button>
            <el-button link @click="changeStatus(row, 'ONLINE')">上架</el-button>
            <el-button link type="danger" @click="changeStatus(row, 'OFFLINE')">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新建/编辑作品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑作品' : '新建作品'"
      width="720px"
      :close-on-click-modal="true"
      destroy-on-close
    >
      <div style="margin-bottom: 16px;">
        <div class="form-label">作品封面</div>
        <CropUploadField 
          v-model="form.coverUrl" 
          placeholder="作品封面图片" 
          :crop-ratio="4/3"
          tip="建议上传 4:3 比例的作品封面图，支持裁剪调整"
        />
      </div>
      <div class="form-grid">
        <div class="form-field">
          <div class="form-label">作品名称</div>
          <el-input v-model="form.name" placeholder="请输入作品名称，如《静谧的山谷》" />
        </div>
        <div class="form-field">
          <div class="form-label">所属艺术家</div>
          <el-select
            v-model="form.artistId"
            placeholder="请选择或输入艺术家名称"
            style="width: 100%;"
            filterable
            allow-create
            default-first-option
            :reserve-keyword="false"
          >
            <el-option
              v-for="artist in artists"
              :key="artist.id"
              :label="artist.name"
              :value="artist.id"
            />
          </el-select>
        </div>
        <div class="form-field">
          <div class="form-label">作品类别</div>
          <el-select v-model="form.category" placeholder="请选择作品类别" style="width: 100%;">
            <el-option label="绘画（油画、丙烯等）" value="PAINTING" />
            <el-option label="版画（丝网、木刻等）" value="PRINT" />
            <el-option label="水墨（国画、书法等）" value="INK" />
            <el-option label="雕塑（青铜、陶瓷等）" value="SCULPTURE" />
            <el-option label="综合（装置、摄影等）" value="OTHER" />
          </el-select>
        </div>
        <div class="form-field">
          <div class="form-label">状态</div>
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
            <el-option label="草稿（暂不上架）" value="DRAFT" />
            <el-option label="上架（公开销售）" value="PUBLISHED" />
            <el-option label="下架（暂停销售）" value="OFF_SHELF" />
          </el-select>
        </div>
      </div>
      <div class="form-grid" style="margin-top: 16px;">
        <div class="form-field">
          <div class="form-label">发售价格</div>
          <el-input v-model.number="form.price" placeholder="请输入发售价格，单位：元" />
        </div>
        <div class="form-field">
          <div class="form-label">可售库存</div>
          <el-input v-model.number="form.stock" placeholder="请输入可售库存数量" />
        </div>
        <div class="form-field">
          <div class="form-label">材质</div>
          <el-input v-model="form.material" placeholder="如：布面油画、铜版画" />
        </div>
        <div class="form-field">
          <div class="form-label">创作年份</div>
          <el-input v-model.number="form.creationYear" placeholder="如：2025" />
        </div>
      </div>
      <div class="form-grid" style="margin-top: 16px;">
        <div class="form-field">
          <div class="form-label">尺寸预设</div>
          <el-select v-model="form.sizePreset" placeholder="请选择常见尺寸" style="width: 100%;" @change="onSizePresetChange">
            <el-option label="自定义尺寸" value="custom" />
            <el-option label="10×10cm" :value="10" />
            <el-option label="20×20cm" :value="20" />
            <el-option label="30×30cm" :value="30" />
            <el-option label="40×40cm" :value="40" />
            <el-option label="50×50cm" :value="50" />
            <el-option label="60×60cm" :value="60" />
            <el-option label="80×80cm" :value="80" />
            <el-option label="100×100cm" :value="100" />
            <el-option label="30×40cm" value="30x40" />
            <el-option label="40×50cm" value="40x50" />
            <el-option label="50×60cm" value="50x60" />
            <el-option label="60×80cm" value="60x80" />
            <el-option label="70×100cm" value="70x100" />
            <el-option label="100×120cm" value="100x120" />
            <el-option label="120×160cm" value="120x160" />
            <el-option label="雕塑（小）/ 约20cm高" value="sculpture_small" />
            <el-option label="雕塑（中）/ 约40cm高" value="sculpture_mid" />
            <el-option label="雕塑（大）/ 约80cm高" value="sculpture_large" />
          </el-select>
        </div>
        <div class="form-field">
          <div class="form-label">自定义尺寸</div>
          <div v-if="form.sizePreset === 'custom'" class="custom-size-inputs">
            <el-input-number v-model="form.widthCm" :min="1" :max="1000" placeholder="宽" />
            <span class="size-separator">×</span>
            <el-input-number v-model="form.heightCm" :min="1" :max="1000" placeholder="高" />
            <span class="size-separator">×</span>
            <el-input-number v-model="form.depthCm" :min="1" :max="1000" placeholder="深" />
            <span class="size-unit">cm</span>
          </div>
          <span v-else class="size-preview">
            {{ form.widthCm || '-' }}×{{ form.heightCm || '-' }}{{ form.depthCm ? '×' + form.depthCm : '' }}cm
          </span>
        </div>
        <div class="form-field">
          <div class="form-label">推荐权重</div>
          <el-input-number v-model="form.adminWeight" :min="0" :max="9999" placeholder="数字越大越靠前" />
        </div>
      </div>
      <div style="margin-top: 16px;">
        <div class="form-label">作品描述</div>
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="请输入作品描述，介绍作品特点、创作背景等"
        />
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存作品</el-button>
      </template>
    </el-dialog>

    <!-- 艺术家详情对话框 -->
    <el-dialog
      v-model="artistDialogVisible"
      title="艺术家详情"
      width="480px"
      :close-on-click-modal="true"
    >
      <div v-if="selectedArtist" class="artist-detail">
        <div class="artist-header">
          <el-avatar :src="selectedArtist.avatarUrl" :size="80">
            {{ selectedArtist.name?.charAt(0) }}
          </el-avatar>
          <div class="artist-info">
            <div class="artist-name">{{ selectedArtist.name }}</div>
            <div class="artist-tags">
              <el-tag v-if="selectedArtist.city" size="small">{{ selectedArtist.city }}</el-tag>
              <el-tag v-if="selectedArtist.tags" size="small" type="info">{{ selectedArtist.tags }}</el-tag>
              <el-tag size="small" :type="selectedArtist.status === 'ACTIVE' || selectedArtist.status === '上线' ? 'success' : 'info'">
                {{ selectedArtist.status === 'ACTIVE' || selectedArtist.status === '上线' ? '上线' : '下线' }}
              </el-tag>
            </div>
          </div>
        </div>
        <el-divider />
        <div class="detail-grid">
          <div class="detail-item">
            <span class="detail-label">艺术家ID</span>
            <span class="detail-value">{{ selectedArtist.id }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">作品数量</span>
            <span class="detail-value">{{ selectedArtist.works || 0 }} 件</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">排序权重</span>
            <span class="detail-value">{{ selectedArtist.sort || selectedArtist.id }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">入驻时间</span>
            <span class="detail-value">{{ selectedArtist.createdAt || '未知' }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="artistDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 批量上传对话框 -->
    <el-dialog
      v-model="batchUploadVisible"
      title="批量上传作品封面"
      width="680px"
      :close-on-click-modal="true"
    >
      <div class="batch-upload-tip">
        <el-alert type="info" :closable="false">
          <template #title>
            <span>上传说明：</span>
            <ul class="upload-tips-list">
              <li>选择多张图片同时上传，按顺序匹配到下方作品列表</li>
              <li>图片将按顺序分配给选中的作品（按作品ID排序）</li>
              <li>支持 JPG、PNG、WebP 格式，单张不超过 5MB</li>
            </ul>
          </template>
        </el-alert>
      </div>

      <div class="upload-target-section">
        <div class="section-title">目标作品（按ID顺序匹配图片）：</div>
        <div v-if="batchTargetArtworks.length > 0" class="target-list">
          <div v-for="(artwork, index) in batchTargetArtworks" :key="artwork.id" class="target-item">
            <span class="target-index">{{ index + 1 }}</span>
            <el-image
              v-if="artwork.coverUrl"
              :src="artwork.coverUrl"
              fit="cover"
              class="target-thumb"
            />
            <div v-else class="target-thumb target-thumb-empty">
              <el-icon><Picture /></el-icon>
            </div>
            <span class="target-name">{{ artwork.name || '未命名作品' }}</span>
            <span class="target-id">ID: {{ artwork.id }}</span>
            <div v-if="batchUploading && batchUrls[index]" class="target-new">
              <el-icon class="is-loading"><Loading /></el-icon>
              <el-image :src="batchUrls[index]" fit="cover" class="target-thumb target-thumb-new" />
            </div>
          </div>
        </div>
        <div v-else class="empty-targets">
          <el-empty description="请先在列表中选择要上传的作品" :image-size="60" />
        </div>
      </div>

      <div class="upload-area" v-if="batchTargetArtworks.length > 0">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :limit="batchTargetArtworks.length"
          :multiple="true"
          :accept="'.jpg,.jpeg,.png,.webp,.JPG,.JPEG,.PNG,.WEBP'"
          :file-list="batchFileList"
          list-type="picture-card"
          :on-change="handleBatchFileChange"
          :on-remove="handleBatchFileRemove"
        >
          <el-icon><Plus /></el-icon>
          <template #tip>
            <div class="el-upload__tip">点击上传，或拖拽图片到此处，支持裁剪调整</div>
          </template>
        </el-upload>
        
        <!-- 批量裁剪控制 -->
        <div v-if="batchFileList.length > 0" class="batch-crop-controls">
          <div class="form-label">批量裁剪比例</div>
          <el-radio-group v-model="batchCropRatio" size="small">
            <el-radio-button :value="null">自由</el-radio-button>
            <el-radio-button :value="1">1:1 方形</el-radio-button>
            <el-radio-button :value="4/3">4:3 横图</el-radio-button>
            <el-radio-button :value="3/4">3:4 竖图</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <template #footer>
        <el-button @click="batchUploadVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="batchUploading"
          :disabled="batchFileList.length === 0"
          @click="executeBatchUpload"
        >
          上传并更新 {{ batchFileList.length }} 张图片
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { Download, Plus, Picture, Upload } from '@element-plus/icons-vue'
import { useAdminStore } from '../../stores/admin'
import CropUploadField from '../../components/CropUploadField.vue'

const adminStore = useAdminStore()
const { artworks: artworksState, artists: artistsState } = storeToRefs(adminStore)
const keyword = ref('')
const saving = ref(false)
const editingId = ref(null)
const editingWeightId = ref(null)
const editingWeightValue = ref(0)
const dialogVisible = ref(false)
const artistDialogVisible = ref(false)
const selectedArtist = ref(null)
const tableRef = ref(null)
const selectedArtworks = ref([])

// 批量上传相关
const batchUploadVisible = ref(false)
const batchFileList = ref([])
const batchUrls = ref({})
const batchUploading = ref(false)
const batchCropRatio = ref(4/3) // 默认 4:3 比例
const uploadRef = ref(null)
const form = reactive({
  name: '',
  artistId: null,
  category: 'PAINTING',
  status: 'PUBLISHED',
  price: 0,
  stock: 1,
  material: '',
  creationYear: new Date().getFullYear(),
  widthCm: null,
  heightCm: null,
  depthCm: null,
  adminWeight: 1,
  tag: '',
  description: '',
  coverUrl: '',
  sizePreset: 'custom'
})

// 尺寸预设映射
const sizePresetMap = {
  '10': { width: 10, height: 10, depth: null },
  '20': { width: 20, height: 20, depth: null },
  '30': { width: 30, height: 30, depth: null },
  '40': { width: 40, height: 40, depth: null },
  '50': { width: 50, height: 50, depth: null },
  '60': { width: 60, height: 60, depth: null },
  '80': { width: 80, height: 80, depth: null },
  '100': { width: 100, height: 100, depth: null },
  '30x40': { width: 30, height: 40, depth: null },
  '40x50': { width: 40, height: 50, depth: null },
  '50x60': { width: 50, height: 60, depth: null },
  '60x80': { width: 60, height: 80, depth: null },
  '70x100': { width: 70, height: 100, depth: null },
  '100x120': { width: 100, height: 120, depth: null },
  '120x160': { width: 120, height: 160, depth: null },
  'sculpture_small': { width: 15, height: 20, depth: 15 },
  'sculpture_mid': { width: 25, height: 40, depth: 25 },
  'sculpture_large': { width: 40, height: 80, depth: 40 }
}

function onSizePresetChange(value) {
  if (value === 'custom') {
    // 保持用户输入的自定义值
    return
  }
  const preset = sizePresetMap[value]
  if (preset) {
    form.widthCm = preset.width
    form.heightCm = preset.height
    form.depthCm = preset.depth
  }
}

// 未保存内容暂存键名
const STORAGE_KEY = 'admin_artwork_draft'
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
    const hasContent = form.name || form.artistId || form.price || form.coverUrl
    if (hasContent && !saving.value) {
      saveDraft()
    }
  }
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
  form.category = 'PAINTING'
  form.status = 'PUBLISHED'
  form.price = 0
  form.stock = 1
  form.material = ''
  form.creationYear = new Date().getFullYear()
  form.widthCm = null
  form.heightCm = null
  form.depthCm = null
  form.adminWeight = 1
  form.tag = ''
  form.description = ''
  form.coverUrl = ''
  form.sizePreset = 'custom'
}

function showDialog() {
  resetForm()
  // 尝试恢复草稿
  if (restoreDraft()) {
    ElMessage.info('已恢复未保存的内容')
  }
  dialogVisible.value = true
}

function startEditWeight(row) {
  editingWeightId.value = row.id
  editingWeightValue.value = row.adminWeight || 0
}

async function saveWeight(row) {
  const newWeight = editingWeightValue.value
  editingWeightId.value = null
  
  if (newWeight === (row.adminWeight || 0)) {
    return // 没有变化
  }
  
  saving.value = true
  try {
    await adminStore.updateArtwork(row.id, { adminWeight: newWeight })
    row.adminWeight = newWeight
    ElMessage.success('权重已更新')
  } catch (err) {
    ElMessage.error('权重更新失败')
  } finally {
    saving.value = false
  }
}

function startEdit(row) {
  editingId.value = row.id
  form.name = row.name || ''
  // 优先通过艺术家名字查找，如果找不到则用第一个艺术家
  const matchedArtist = artists.value.find((a) => a.name === row.artist)
  form.artistId = matchedArtist?.id || (artists.value[0]?.id || null)
  form.category = row.category || 'PAINTING'
  form.status = normalizeArtworkStatus(row.status)
  form.price = parsePrice(row.price)
  form.stock = Number(row.stock || 0)
  form.material = row.material || ''
  form.creationYear = row.creationYear || new Date().getFullYear()
  form.widthCm = row.widthCm || null
  form.heightCm = row.heightCm || null
  form.depthCm = row.depthCm || null
  form.adminWeight = row.adminWeight || 1
  form.tag = row.tag || ''
  form.description = row.description || ''
  form.coverUrl = row.coverUrl || ''

  // 尝试匹配预设尺寸
  const width = row.widthCm
  const height = row.heightCm
  const matchedPreset = Object.entries(sizePresetMap).find(([key, val]) =>
    val.width === width && val.height === height && val.depth === row.depthCm
  )
  form.sizePreset = matchedPreset ? matchedPreset[0] : 'custom'

  dialogVisible.value = true
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

function showArtistDetail(row) {
  // 根据艺术家名称查找艺术家详情
  const artist = artists.value.find((a) => a.name === row.artist)
  if (artist) {
    selectedArtist.value = artist
  } else {
    // 如果找不到，返回基本信息
    selectedArtist.value = {
      id: row.artistId,
      name: row.artist,
      avatarUrl: '',
      city: '',
      tags: '',
      status: '',
      works: 0,
      sort: row.id
    }
  }
  artistDialogVisible.value = true
}

function formatCategory(category) {
  const map = {
    PAINTING: '绘画',
    PRINT: '版画',
    INK: '水墨',
    SCULPTURE: '雕塑',
    OTHER: '综合'
  }
  return map[category] || category || '-'
}

async function submitForm() {
  if (!form.name.trim()) {
    ElMessage.warning('请输入作品名称')
    return
  }
  if (!form.artistId) {
    ElMessage.warning('请选择或输入所属艺术家')
    return
  }
  const price = Number(form.price || 0)
  if (price <= 0) {
    ElMessage.warning('请输入有效价格')
    return
  }
  
  // 处理艺术家：如果输入的是字符串（手动输入的名称），需要查找或创建艺术家
  let artistId = form.artistId
  if (typeof artistId === 'string') {
    const artistName = artistId.trim()
    // 查找是否已有同名艺术家
    const existingArtist = artists.value.find(a => a.name === artistName)
    if (existingArtist) {
      artistId = existingArtist.id
    } else {
      // 创建新艺术家
      try {
        ElMessage.info(`正在创建新艺术家: ${artistName}`)
        await adminStore.createArtist({ name: artistName, status: 'ACTIVE' })
        // 重新加载艺术家列表
        await adminStore.loadArtists()
        // 获取新创建的艺术家ID
        const newArtist = artists.value.find(a => a.name === artistName)
        if (newArtist) {
          artistId = newArtist.id
        } else {
          ElMessage.error('艺术家创建失败，请重试')
          return
        }
      } catch (err) {
        console.error('创建艺术家失败:', err)
        ElMessage.error('艺术家创建失败，请重试')
        return
      }
    }
  }
  
  saving.value = true
  try {
    const payload = {
      name: form.name.trim(),
      artistId: artistId,
      category: form.category || 'PAINTING',
      price: price,
      stock: Number(form.stock ?? 0),
      status: form.status || 'DRAFT',
      material: form.material?.trim() || '',
      creationYear: form.creationYear || null,
      widthCm: form.widthCm ? Number(form.widthCm) : null,
      heightCm: form.heightCm ? Number(form.heightCm) : null,
      depthCm: form.depthCm ? Number(form.depthCm) : null,
      adminWeight: Number(form.adminWeight ?? 0),
      tag: form.tag?.trim() || '',
      description: form.description?.trim() || '',
      coverUrl: form.coverUrl || ''
    }
    console.log('保存作品:', editingId.value ? '更新' : '新建', payload)
    await adminStore.saveArtwork(editingId.value, payload)
    ElMessage.success(editingId.value ? '作品已更新' : '作品已创建')
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
      ElMessage.warning('当前作品缺少ID，暂时无法更新状态')
      return
    }
    await adminStore.changeArtworkStatus(id, status)
    ElMessage.success(status === 'ONLINE' ? '作品已上架' : '作品已下架')
  } catch (error) {
    ElMessage.error(error.message || '状态更新失败')
  }
}

// 批量上架功能
async function batchPublish() {
  if (selectedArtworks.value.length === 0) {
    ElMessage.warning('请先选择要上架的作品')
    return
  }
  
  const unpublishedWorks = selectedArtworks.value.filter(item => 
    item.status !== '上架' && item.status !== 'PUBLISHED'
  )
  
  if (unpublishedWorks.length === 0) {
    ElMessage.info('所选作品都已上架')
    return
  }
  
  try {
    const publishPromises = unpublishedWorks.map(work => 
      adminStore.changeArtworkStatus(work.id, 'ONLINE')
    )
    await Promise.all(publishPromises)
    ElMessage.success(`已上架 ${unpublishedWorks.length} 个作品`)
    clearSelection()
  } catch (error) {
    ElMessage.error(error.message || '批量上架失败')
  }
}

// 表格选择相关
function handleSelectionChange(selection) {
  selectedArtworks.value = selection
}

function clearSelection() {
  tableRef.value?.clearSelection()
}

// 批量下载功能
async function batchDownload() {
  if (selectedArtworks.value.length === 0) {
    ElMessage.warning('请先选择要下载的作品')
    return
  }

  const artworksWithImages = selectedArtworks.value.filter(item => item.coverUrl && item.coverUrl.startsWith('http'))
  if (artworksWithImages.length === 0) {
    ElMessage.warning('选中的作品都没有封面图片可供下载')
    return
  }

  ElMessage.info(`开始下载 ${artworksWithImages.length} 张封面图片...`)

  // 使用浏览器下载功能逐个下载
  let successCount = 0
  let failCount = 0

  for (const artwork of artworksWithImages) {
    try {
      const response = await fetch(artwork.coverUrl)
      const blob = await response.blob()
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${artwork.name || artwork.id}_封面.jpg`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
      successCount++
      // 添加小延迟避免浏览器阻止
      await new Promise(resolve => setTimeout(resolve, 300))
    } catch (error) {
      console.error(`下载 ${artwork.name} 失败:`, error)
      failCount++
    }
  }

  if (successCount > 0) {
    ElMessage.success(`成功下载 ${successCount} 张图片${failCount > 0 ? `，${failCount} 张失败` : ''}`)
  } else {
    ElMessage.error('下载失败，请重试')
  }
}

// 批量上传相关
const batchTargetArtworks = computed(() => {
  return selectedArtworks.value
    .filter(a => a.id)
    .sort((a, b) => a.id - b.id)
})

function showBatchUploadDialog() {
  if (selectedArtworks.value.length === 0) {
    ElMessage.warning('请先在列表中选择要上传封面的作品')
    return
  }
  batchFileList.value = []
  batchUrls.value = {}
  batchUploadVisible.value = true
}

function handleBatchFileChange(file, fileList) {
  batchFileList.value = fileList
  // 预览图片
  fileList.forEach((f, index) => {
    if (f.status === 'ready') {
      batchUrls.value[index] = URL.createObjectURL(f.raw)
    }
  })
}

function handleBatchFileRemove(file, fileList) {
  batchFileList.value = fileList
  // 清理预览URL
  const index = batchFileList.value.findIndex(f => f.uid === file.uid)
  if (index !== -1 && batchUrls.value[index]) {
    URL.revokeObjectURL(batchUrls.value[index])
    delete batchUrls.value[index]
  }
}

async function executeBatchUpload() {
  if (batchFileList.value.length === 0) {
    ElMessage.warning('请先选择要上传的图片')
    return
  }

  if (batchFileList.value.length > batchTargetArtworks.value.length) {
    ElMessage.warning(`图片数量(${batchFileList.value.length})不能超过目标作品数量(${batchTargetArtworks.value.length})`)
    return
  }

  batchUploading.value = true
  let successCount = 0
  let failCount = 0

  try {
    for (let i = 0; i < batchFileList.value.length; i++) {
      const file = batchFileList.value[i]
      const artwork = batchTargetArtworks.value[i]

      try {
        // 将文件转为 Base64
        const base64 = await fileToBase64(file.raw)
        // 调用上传接口（这里使用简单的 Base64 URL，实际项目中应该调用后端 API）
        const coverUrl = `data:${file.raw.type};base64,${base64}`

        // 更新作品封面
        await adminStore.saveArtwork(artwork.id, {
          ...artwork,
          coverUrl: coverUrl
        })

        successCount++
        batchUrls.value[i] = coverUrl
      } catch (error) {
        console.error(`上传 ${artwork.name} 封面失败:`, error)
        failCount++
      }
    }

    if (successCount > 0) {
      ElMessage.success(`成功更新 ${successCount} 张作品封面${failCount > 0 ? `，${failCount} 张失败` : ''}`)
      // 关闭弹窗并刷新列表
      setTimeout(() => {
        batchUploadVisible.value = false
        adminStore.loadArtworks()
      }, 1500)
    } else {
      ElMessage.error('上传失败，请重试')
    }
  } finally {
    batchUploading.value = false
  }
}

function fileToBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => {
      const base64 = reader.result.split(',')[1]
      resolve(base64)
    }
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}

onMounted(async () => {
  if (!artistsState.value.length) await adminStore.loadArtists()
  if (!artworksState.value.length) await adminStore.loadArtworks()
  if (!form.artistId) form.artistId = artists.value[0]?.id || null
})
</script>

<style scoped>
.header-actions {
  display: flex;
  gap: 12px;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.toolbar-spacer {
  flex: 1;
}
.clickable-text {
  cursor: pointer;
  color: var(--el-color-primary);
}
.clickable-text:hover {
  text-decoration: underline;
}
.artist-detail {
  padding: 8px 0;
}
.artist-header {
  display: flex;
  align-items: center;
  gap: 20px;
}
.artist-info {
  flex: 1;
}
.artist-name {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}
.artist-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.detail-label {
  font-size: 12px;
  color: #909399;
}
.detail-value {
  font-size: 14px;
  color: #303133;
}

/* 表单样式 */
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
.size-preview {
  font-size: 13px;
  color: #909399;
  line-height: 32px;
}

/* 批量上传样式 */
.batch-upload-tip {
  margin-bottom: 16px;
}
.upload-tips-list {
  margin: 8px 0 0 0;
  padding-left: 20px;
  font-size: 13px;
}
.upload-tips-list li {
  line-height: 1.8;
}
.upload-target-section {
  margin-bottom: 16px;
}
.section-title {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 12px;
}
.target-list {
  max-height: 280px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 8px;
}
.target-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-bottom: 1px solid #f0f0f0;
}
.target-item:last-child {
  border-bottom: none;
}
.target-index {
  width: 24px;
  height: 24px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  flex-shrink: 0;
}
.target-thumb {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}
.target-thumb-empty {
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 20px;
}
.target-thumb-new {
  border: 2px solid #67c23a;
}
.target-name {
  flex: 1;
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.target-id {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
}
.target-new {
  display: flex;
  align-items: center;
  gap: 8px;
}
.empty-targets {
  padding: 20px 0;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
}
.upload-area {
  margin-top: 16px;
}
.batch-crop-controls {
  margin-top: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}
.batch-crop-controls .form-label {
  margin-bottom: 8px;
}
.custom-size-inputs {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.size-separator {
  color: #606266;
  font-weight: 500;
}
.size-unit {
  color: #909399;
  font-size: 12px;
}
.weight-default {
  color: #c0c4cc;
}
.weight-edit-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
