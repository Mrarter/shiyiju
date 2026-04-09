<template>
  <el-dialog
    v-model="dialogVisible"
    title="裁剪图片"
    width="800px"
    :close-on-click-modal="false"
    class="image-cropper-dialog"
    @opened="initCropper"
    @closed="destroyCropper"
  >
    <div class="cropper-container">
      <div class="cropper-wrapper">
        <img ref="imageRef" :src="imageUrl" alt="" />
      </div>
      <div class="cropper-info">
        <div class="info-item">
          <span class="label">宽高比：</span>
          <el-select v-model="currentAspectRatio" size="small" @change="handleRatioChange">
            <el-option :value="null" label="自由" />
            <el-option :value="1" label="1:1 方形" />
            <el-option :value="4/3" label="4:3" />
            <el-option :value="16/9" label="16:9" />
            <el-option :value="3/4" label="3:4 竖图" />
          </el-select>
        </div>
        <div class="info-item">
          <span class="label">缩放：</span>
          <el-button-group size="small">
            <el-button @click="zoom(0.1)">+</el-button>
            <el-button @click="zoom(-0.1)">-</el-button>
            <el-button @click="rotate(90)">90°</el-button>
            <el-button @click="reset">重置</el-button>
          </el-button-group>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button @click="handleCancel">选择新图片</el-button>
      <el-button type="primary" @click="handleConfirm">确认裁剪</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import Cropper from 'cropperjs'
import 'cropperjs/dist/cropper.css'

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
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const imageRef = ref(null)
const cropper = ref(null)
const currentAspectRatio = ref(null) // 默认自由格式

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

watch(() => props.aspectRatio, (val) => {
  currentAspectRatio.value = val
  if (cropper.value && val !== null) {
    cropper.value.setAspectRatio(val)
  } else if (cropper.value) {
    cropper.value.setAspectRatio(NaN)
  }
})

watch(() => props.imageUrl, () => {
  if (props.modelValue && props.imageUrl) {
    nextTick(() => {
      initCropper()
    })
  }
})

function initCropper() {
  if (!imageRef.value || !props.imageUrl) return

  if (cropper.value) {
    cropper.value.destroy()
    cropper.value = null
  }

  nextTick(() => {
    cropper.value = new Cropper(imageRef.value, {
      aspectRatio: props.aspectRatio || NaN,
      viewMode: 1,
      dragMode: 'move',
      modal: true,
      guides: true,
      center: true,
      highlight: true,
      background: true,
      responsive: true,
      autoCropArea: 0.8,
      movable: true,
      rotatable: true,
      scalable: true,
      zoomable: true,
      zoomOnTouch: true,
      zoomOnWheel: true,
      ready() {
        console.log('Cropper ready')
      }
    })
  })
}

function destroyCropper() {
  if (cropper.value) {
    cropper.value.destroy()
    cropper.value = null
  }
}

function handleRatioChange(ratio) {
  if (cropper.value) {
    cropper.value.setAspectRatio(ratio === null ? NaN : ratio)
  }
}

function zoom(ratio) {
  if (cropper.value) {
    cropper.value.zoom(ratio)
  }
}

function rotate(angle) {
  if (cropper.value) {
    cropper.value.rotate(angle)
  }
}

function reset() {
  if (cropper.value) {
    cropper.value.reset()
  }
}

function handleCancel() {
  emit('cancel')
}

function handleConfirm() {
  if (cropper.value) {
    const canvas = cropper.value.getCroppedCanvas({
      maxWidth: 4096,
      maxHeight: 4096,
      imageSmoothingEnabled: true,
      imageSmoothingQuality: 'high'
    })

    canvas.toBlob((blob) => {
      emit('confirm', {
        blob: blob,
        dataUrl: canvas.toDataURL('image/jpeg', 0.95)
      })
      dialogVisible.value = false
    }, 'image/jpeg', 0.95)
  }
}

defineExpose({
  zoom,
  rotate,
  reset
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
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}

.cropper-wrapper img {
  max-width: 100%;
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
</style>
