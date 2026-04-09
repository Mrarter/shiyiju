<template>
  <div class="crop-upload-field">
    <div class="upload-preview" :class="{ 'has-image': modelValue }">
      <el-image
        v-if="modelValue"
        :src="modelValue"
        fit="contain"
        style="width: 100%; height: 100%;"
        preview-teleported
      />
      <div v-else class="upload-placeholder">
        <el-icon :size="32"><Picture /></el-icon>
        <span>暂无图片</span>
      </div>
    </div>
    <div class="upload-actions">
      <el-upload
        :show-file-list="false"
        accept="image/jpeg,image/jpg,image/png,image/webp,.jpg,.jpeg,.png,.webp"
        :before-upload="beforeUpload"
        :http-request="handleSelect"
      >
        <el-button type="primary" plain :loading="uploading">{{ modelValue ? '更换图片' : '选择图片' }}</el-button>
      </el-upload>
      <el-button 
        v-if="enableCrop && modelValue" 
        type="warning" 
        plain
        @click="openCropper"
      >
        裁剪图片
      </el-button>
      <el-button v-if="modelValue" link type="danger" @click="handleRemove">
        移除
      </el-button>
      <div class="upload-tip">{{ tip }}</div>
    </div>

    <!-- 图片裁剪弹窗 -->
    <ImageCropper
      ref="cropperRef"
      v-model="cropperVisible"
      :image-url="pendingImageUrl"
      :aspect-ratio="cropRatio"
      @confirm="handleCropConfirm"
      @cancel="handleCropCancel"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { uploadAdminImage } from '../api/admin'
import ImageCropper from './ImageCropper.vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '暂无图片'
  },
  tip: {
    type: String,
    default: '支持 JPG/PNG/WEBP，建议尺寸 800x800，最大 20MB'
  },
  enableCrop: {
    type: Boolean,
    default: true
  },
  cropRatio: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:modelValue'])

const uploading = ref(false)
const cropperVisible = ref(false)
const pendingImageUrl = ref('')
const cropperRef = ref(null)

const MAX_SIZE = 20 * 1024 * 1024 // 20MB

function beforeUpload(file) {
  if (file.size > MAX_SIZE) {
    ElMessage.error('图片体积不能超过 20MB')
    return false
  }
  return true
}

// 选择图片后
function handleSelect(options) {
  const file = options.file
  
  if (props.enableCrop) {
    // 创建 blob URL 传给裁剪组件
    pendingImageUrl.value = URL.createObjectURL(file)
    cropperVisible.value = true
  } else {
    uploadFile(file)
  }
}

// 打开裁剪弹窗（已有图片的裁剪）
function openCropper() {
  if (props.modelValue) {
    // 如果是 blob URL，直接使用；否则创建新的 blob URL
    if (props.modelValue.startsWith('blob:')) {
      pendingImageUrl.value = props.modelValue
    } else {
      // 已有图片是服务器 URL，需要获取 blob
      pendingImageUrl.value = props.modelValue // 假设是完整 URL
    }
    cropperVisible.value = true
  }
}

// 裁剪确认
async function handleCropConfirm(cropData) {
  uploading.value = true
  try {
    const { blob } = cropData
    
    // cropper.js 已经处理好了裁剪，直接上传裁剪后的 blob
    const file = new File([blob], 'cropped.jpg', { type: 'image/jpeg' })
    const result = await uploadAdminImage(file)
    
    emit('update:modelValue', result.url || '')
    ElMessage.success('图片上传成功')
    cropperVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '图片上传失败')
  } finally {
    uploading.value = false
  }
}

// 裁剪取消
function handleCropCancel() {
  cropperVisible.value = false
  pendingImageUrl.value = ''
}

// 直接上传文件
async function uploadFile(file) {
  uploading.value = true
  try {
    const result = await uploadAdminImage(file)
    emit('update:modelValue', result.url || '')
    ElMessage.success('图片上传成功')
  } catch (error) {
    ElMessage.error(error.message || '图片上传失败')
  } finally {
    uploading.value = false
    pendingImageUrl.value = ''
  }
}

// 移除图片
function handleRemove() {
  emit('update:modelValue', '')
}
</script>

<style scoped>
.crop-upload-field {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.upload-preview {
  min-width: 120px;
  min-height: 120px;
  max-width: 300px;
  height: auto;
  aspect-ratio: auto;
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid var(--el-border-color);
  background: var(--el-fill-color-light);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-preview.has-image {
  border-color: var(--el-color-primary-light-5);
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  padding: 16px;
}

.upload-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.upload-tip {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  max-width: 240px;
}
</style>
