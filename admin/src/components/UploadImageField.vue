<template>
  <div class="upload-image-field">
    <div class="upload-preview">
      <el-image
        v-if="modelValue"
        :src="modelValue"
        fit="cover"
        style="width: 100%; height: 100%;"
        preview-teleported
      />
      <div v-else class="upload-placeholder">{{ placeholder }}</div>
    </div>
    <div class="upload-actions">
      <el-upload
        :show-file-list="false"
        accept="image/jpeg,image/jpg,image/png,image/webp,image/gif,.jpg,.jpeg,.png,.webp,.gif"
        :http-request="handleUpload"
      >
        <el-button type="primary" plain :loading="uploading">{{ modelValue ? '更换图片' : '上传图片' }}</el-button>
      </el-upload>
      <el-button v-if="modelValue" link type="danger" @click="$emit('update:modelValue', '')">移除</el-button>
      <div class="upload-tip">{{ tip }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadAdminImage } from '../api/admin'

defineProps({
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
    default: '支持 JPG/JPEG、PNG、WEBP、GIF（大图）'
  }
})

const emit = defineEmits(['update:modelValue'])

const uploading = ref(false)

async function handleUpload(options) {
  uploading.value = true
  try {
    const result = await uploadAdminImage(options.file)
    emit('update:modelValue', result.url || '')
    ElMessage.success('图片上传成功')
    options.onProgress?.({ percent: 100 })
    options.onSuccess?.(result)
  } catch (error) {
    options.onError?.(error)
    ElMessage.error(error.message || '图片上传失败')
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.upload-image-field {
  display: flex;
  gap: 16px;
  align-items: center;
}

.upload-preview {
  width: 120px;
  height: 120px;
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid var(--el-border-color);
  background: var(--el-fill-color-light);
  flex-shrink: 0;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.upload-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.upload-tip {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
</style>
