<template>
  <el-dialog
    v-model="dialogVisible"
    title="裁剪图片"
    width="800px"
    :close-on-click-modal="false"
    class="image-cropper-dialog"
  >
    <div class="cropper-container">
      <div class="cropper-wrapper" ref="wrapperRef">
        <canvas ref="canvasRef" @mousedown="startCrop" @mousemove="updateCrop" @mouseup="endCrop" @wheel="handleZoom"></canvas>
      </div>
      <div class="cropper-info">
        <div class="info-item">
          <span class="label">裁剪形状：</span>
          <el-select v-model="currentAspectRatio" size="small" @change="handleRatioChange">
            <el-option :value="0" label="自由形状" />
            <el-option :value="1" label="1:1 正方形" />
            <el-option :value="1.333" label="4:3 横图" />
            <el-option :value="1.777" label="16:9 宽屏" />
            <el-option :value="1.875" label="7.5:4 Banner" />
            <el-option :value="0.75" label="3:4 竖图" />
          </el-select>
        </div>
        <div class="crop-area-info" v-if="cropArea.width > 0">
          <span>{{ cropArea.width }} × {{ cropArea.height }}</span>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确认裁剪</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  imageUrl: {
    type: String,
    default: ''
  },
  aspectRatio: {
    type: Number,
    default: 0 // 0 = 自由形状
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const canvasRef = ref(null)
const currentAspectRatio = ref(props.aspectRatio) // 使用传入的比例，默认自由形状

const image = ref(null)
const scale = ref(1)
const offsetX = ref(0)
const offsetY = ref(0)
const isDragging = ref(false)
const dragStartX = ref(0)
const dragStartY = ref(0)
const imageStartX = ref(0)
const imageStartY = ref(0)

const cropArea = ref({ x: 0, y: 0, width: 0, height: 0 })
const isResizing = ref(false)
const resizeHandle = ref('')
const cropStartArea = ref({})

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

watch(() => props.imageUrl, (url) => {
  console.log('[Cropper] imageUrl changed:', url ? url.substring(0, 100) + '...' : 'empty')
  if (url) {
    nextTick(() => {
      loadImage(url)
    })
  }
})

watch(() => props.modelValue, (isOpen) => {
  console.log('[Cropper] dialog opened:', isOpen)
  if (isOpen && props.imageUrl) {
    nextTick(() => {
      loadImage(props.imageUrl)
    })
  }
})

function loadImage(url) {
  console.log('[Cropper] Loading image:', url.substring(0, 80) + '...')
  
  // 等待 dialog 打开
  setTimeout(() => {
    nextTick(() => {
      if (!canvasRef.value) {
        console.error('[Cropper] Canvas not ready')
        return
      }
      
      // 设置 canvas 尺寸
      const wrapper = canvasRef.value.parentElement
      canvasRef.value.width = wrapper.clientWidth
      canvasRef.value.height = 450
      
      const img = new Image()
      img.onload = () => {
        console.log('[Cropper] Image loaded, size:', img.width, 'x', img.height)
        image.value = img
        resetView()
        drawCanvas()
      }
      img.onerror = (e) => {
        console.error('[Cropper] Image load error:', e)
        ElMessage.error('图片加载失败')
      }
      img.src = url
    })
  }, 200)
}

function resetView() {
  if (!canvasRef.value || !image.value) return
  
  const canvas = canvasRef.value
  const ctx = canvas.getContext('2d')
  const container = canvas.parentElement
  
  canvas.width = container.clientWidth
  canvas.height = 450
  
  // 计算缩放比例使图片适应画布
  const scaleX = canvas.width / image.value.width
  const scaleY = canvas.height / image.value.height
  scale.value = Math.min(scaleX, scaleY) * 0.9
  
  // 居中图片
  offsetX.value = (canvas.width - image.value.width * scale.value) / 2
  offsetY.value = (canvas.height - image.value.height * scale.value) / 2
  
  // 初始化裁剪区域
  initCropArea()
}

function initCropArea() {
  if (!canvasRef.value || !image.value) return
  
  const canvas = canvasRef.value
  const imgW = image.value.width * scale.value
  const imgH = image.value.height * scale.value
  
  // 根据比例计算裁剪区域
  const maxW = Math.min(imgW * 0.8, canvas.width - 40)
  let cropW = maxW
  let cropH = maxW
  
  if (currentAspectRatio.value > 0) {
    // 有固定比例时，使用比例计算
    cropH = cropW / currentAspectRatio.value
    if (cropH > imgH * 0.8) {
      cropH = imgH * 0.8
      cropW = cropH * currentAspectRatio.value
    }
  }
  
  // 居中裁剪区域
  const areaX = (canvas.width - cropW) / 2
  const areaY = (canvas.height - cropH) / 2
  
  cropArea.value = {
    x: areaX,
    y: areaY,
    width: cropW,
    height: cropH
  }
}

function handleRatioChange(ratio) {
  currentAspectRatio.value = ratio
  if (ratio > 0 && image.value) {
    // 调整裁剪区域高度以匹配比例
    cropArea.value.height = cropArea.value.width / ratio
  }
  drawCanvas()
}

function drawCanvas() {
  if (!canvasRef.value || !image.value) return
  
  const canvas = canvasRef.value
  const ctx = canvas.getContext('2d')
  
  // 清空画布
  ctx.fillStyle = '#1a1a1a'
  ctx.fillRect(0, 0, canvas.width, canvas.height)
  
  // 绘制图片
  ctx.save()
  ctx.translate(offsetX.value, offsetY.value)
  ctx.scale(scale.value, scale.value)
  ctx.drawImage(image.value, 0, 0)
  ctx.restore()
  
  // 绘制裁剪区域
  const { x, y, width, height } = cropArea.value
  
  // 半透明遮罩
  ctx.fillStyle = 'rgba(0, 0, 0, 0.5)'
  ctx.fillRect(0, 0, canvas.width, canvas.height)
  
  // 清除裁剪区域
  ctx.clearRect(x, y, width, height)
  ctx.save()
  ctx.translate(offsetX.value, offsetY.value)
  ctx.scale(scale.value, scale.value)
  
  // 绘制裁剪区域内的图片
  const sx = (x - offsetX.value) / scale.value
  const sy = (y - offsetY.value) / scale.value
  const sw = width / scale.value
  const sh = height / scale.value
  ctx.drawImage(image.value, sx, sy, sw, sh, sx, sy, sw, sh)
  ctx.restore()
  
  // 绘制裁剪边框
  ctx.strokeStyle = '#ffffff'
  ctx.lineWidth = 2
  ctx.strokeRect(x, y, width, height)
  
  // 绘制网格线
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.3)'
  ctx.lineWidth = 1
  for (let i = 1; i < 3; i++) {
    ctx.beginPath()
    ctx.moveTo(x + width * i / 3, y)
    ctx.lineTo(x + width * i / 3, y + height)
    ctx.stroke()
    ctx.beginPath()
    ctx.moveTo(x, y + height * i / 3)
    ctx.lineTo(x + width, y + height * i / 3)
    ctx.stroke()
  }
}

function startCrop(e) {
  const rect = canvasRef.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  
  // 检查是否点击了裁剪区域的调整手柄
  const handleSize = 10
  const { x: cx, y: cy, width: cw, height: ch } = cropArea.value
  
  // 四角调整手柄
  if (Math.abs(x - cx) < handleSize && Math.abs(y - cy) < handleSize) {
    isResizing.value = true
    resizeHandle.value = 'tl'
    cropStartArea.value = { ...cropArea.value }
    cropStartArea.value.mouseX = x
    cropStartArea.value.mouseY = y
    return
  }
  if (Math.abs(x - (cx + cw)) < handleSize && Math.abs(y - cy) < handleSize) {
    isResizing.value = true
    resizeHandle.value = 'tr'
    cropStartArea.value = { ...cropArea.value }
    cropStartArea.value.mouseX = x
    cropStartArea.value.mouseY = y
    return
  }
  if (Math.abs(x - cx) < handleSize && Math.abs(y - (cy + ch)) < handleSize) {
    isResizing.value = true
    resizeHandle.value = 'bl'
    cropStartArea.value = { ...cropArea.value }
    cropStartArea.value.mouseX = x
    cropStartArea.value.mouseY = y
    return
  }
  if (Math.abs(x - (cx + cw)) < handleSize && Math.abs(y - (cy + ch)) < handleSize) {
    isResizing.value = true
    resizeHandle.value = 'br'
    cropStartArea.value = { ...cropArea.value }
    cropStartArea.value.mouseX = x
    cropStartArea.value.mouseY = y
    return
  }
  
  // 检查是否在裁剪区域内（移动）
  if (x >= cx && x <= cx + cw && y >= cy && y <= cy + ch) {
    isDragging.value = true
    dragStartX.value = x
    dragStartY.value = y
    imageStartX.value = cropArea.value.x
    imageStartY.value = cropArea.value.y
  } else {
    // 点击外部移动图片
    isDragging.value = true
    dragStartX.value = x
    dragStartY.value = y
    imageStartX.value = offsetX.value
    imageStartY.value = offsetY.value
  }
}

function updateCrop(e) {
  const rect = canvasRef.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  
  if (isResizing.value) {
    const dx = x - cropStartArea.value.mouseX
    const dy = y - cropStartArea.value.mouseY
    const area = cropStartArea.value
    
    switch (resizeHandle.value) {
      case 'tl':
        cropArea.value.x = Math.max(0, Math.min(area.x + dx, area.x + area.width - 50))
        cropArea.value.y = Math.max(0, Math.min(area.y + dy, area.y + area.height - 50))
        cropArea.value.width = area.width - (cropArea.value.x - area.x)
        cropArea.value.height = area.height - (cropArea.value.y - area.y)
        break
      case 'tr':
        cropArea.value.y = Math.max(0, Math.min(area.y + dy, area.y + area.height - 50))
        cropArea.value.width = Math.max(50, area.width + dx)
        cropArea.value.height = area.height - (cropArea.value.y - area.y)
        break
      case 'bl':
        cropArea.value.x = Math.max(0, Math.min(area.x + dx, area.x + area.width - 50))
        cropArea.value.width = area.width - (cropArea.value.x - area.x)
        cropArea.value.height = Math.max(50, area.height + dy)
        break
      case 'br':
        cropArea.value.width = Math.max(50, area.width + dx)
        cropArea.value.height = Math.max(50, area.height + dy)
        break
    }
    
    // 保持比例
    if (currentAspectRatio.value > 0) {
      cropArea.value.height = cropArea.value.width / currentAspectRatio.value
    }
    
    drawCanvas()
  } else if (isDragging.value) {
    const dx = x - dragStartX.value
    const dy = y - dragStartY.value
    
    // 移动裁剪区域或图片
    if (x >= cropArea.value.x && x <= cropArea.value.x + cropArea.value.width &&
        y >= cropArea.value.y && y <= cropArea.value.y + cropArea.value.height) {
      cropArea.value.x = imageStartX.value + dx
      cropArea.value.y = imageStartY.value + dy
    } else {
      offsetX.value = imageStartX.value + dx
      offsetY.value = imageStartY.value + dy
    }
    drawCanvas()
  }
}

function endCrop() {
  isDragging.value = false
  isResizing.value = false
  resizeHandle.value = ''
}

function handleZoom(e) {
  e.preventDefault()
  const delta = e.deltaY > 0 ? -0.1 : 0.1
  scale.value = Math.max(0.1, Math.min(5, scale.value + delta))
  drawCanvas()
}

function handleCancel() {
  dialogVisible.value = false
  emit('cancel')
}

function handleConfirm() {
  if (!image.value) {
    ElMessage.error('请先选择图片')
    return
  }
  
  const { x, y, width, height } = cropArea.value
  
  // 计算原图坐标
  const sx = (x - offsetX.value) / scale.value
  const sy = (y - offsetY.value) / scale.value
  const sw = width / scale.value
  const sh = height / scale.value
  
  // 创建裁剪画布
  const cropCanvas = document.createElement('canvas')
  cropCanvas.width = sw
  cropCanvas.height = sh
  const ctx = cropCanvas.getContext('2d')
  ctx.drawImage(image.value, sx, sy, sw, sh, 0, 0, sw, sh)
  
  // 转为 blob
  cropCanvas.toBlob((blob) => {
    if (!blob) {
      ElMessage.error('裁剪失败')
      return
    }
    
    emit('confirm', {
      blob: blob,
      dataUrl: cropCanvas.toDataURL('image/jpeg', 0.95)
    })
    dialogVisible.value = false
  }, 'image/jpeg', 0.95)
}

onMounted(() => {
  if (props.imageUrl) {
    loadImage(props.imageUrl)
  }
})
</script>

<style scoped>
.image-cropper-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

.cropper-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cropper-wrapper {
  width: 100%;
  height: 450px;
  background: #1a1a1a;
  border-radius: 8px;
  overflow: hidden;
  cursor: crosshair;
}

.cropper-wrapper canvas {
  display: block;
}

.cropper-info {
  display: flex;
  gap: 24px;
  align-items: center;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item .label {
  color: var(--el-text-color-regular);
  font-size: 13px;
}

.crop-area-info {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
</style>
