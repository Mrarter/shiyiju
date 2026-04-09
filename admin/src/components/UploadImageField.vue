<template>
  <div class="upload-image-field">
    <div class="upload-preview" :class="{ 'has-image': modelValue }">
      <el-image
        v-if="modelValue"
        :src="modelValue"
        fit="contain"
        style="width: 100%; height: 100%;"
        preview-teleported
      />
      <div v-else class="upload-placeholder">{{ placeholder }}</div>
    </div>
    <div class="upload-actions">
      <el-button type="primary" plain :loading="uploading" @click="triggerUpload">
        {{ modelValue ? '更换图片' : '上传图片' }}
      </el-button>
      <el-button v-if="modelValue" link type="danger" @click="handleRemove">移除</el-button>
      <div class="upload-tip">{{ tip }}</div>
    </div>

    <!-- 隐藏的文件输入 -->
    <input
      ref="fileInputRef"
      type="file"
      accept="image/jpeg,image/jpg,image/png,image/webp,.jpg,.jpeg,.png,.webp"
      style="display: none;"
      @change="handleFileChange"
    />

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
const pendingFile = ref(null)
const fileInputRef = ref(null)
const cropperRef = ref(null)

const MAX_SIZE = 20 * 1024 * 1024 // 20MB

function triggerUpload() {
  fileInputRef.value?.click()
}

function handleFileChange(event) {
  const file = event.target.files?.[0]
  if (!file) return

  if (file.size > MAX_SIZE) {
    ElMessage.error('图片体积不能超过 20MB')
    return
  }

  if (props.enableCrop) {
    // 创建 blob URL 传给裁剪组件
    pendingImageUrl.value = URL.createObjectURL(file)
    pendingFile.value = file
    cropperVisible.value = true
  } else {
    uploadFile(file)
  }

  // 清空 input 以便重复选择同一文件
  event.target.value = ''
}

async function handleCropConfirm(cropData) {
  uploading.value = true
  try {
    const { blob } = cropData
    const file = new File([blob], 'cropped.jpg', { type: 'image/jpeg' })
    const result = await uploadAdminImage(file)
    emit('update:modelValue', result.url || '')
    ElMessage.success('图片上传成功')
  } catch (error) {
    ElMessage.error(error.message || '图片上传失败')
  } finally {
    uploading.value = false
    cropperVisible.value = false
    pendingImageUrl.value = ''
    pendingFile.value = null
  }
}

function handleCropCancel() {
  cropperVisible.value = false
  pendingImageUrl.value = ''
  pendingFile.value = null
}

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
  }
}

function handleRemove() {
  emit('update:modelValue', '')
}
</script>

<style scoped>
.upload-image-field {
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
  width: 100%;
  height: 100%;
  min-height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
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
