# 后台图片裁剪上传功能

## 功能概述

为后台管理系统新增图片上传裁剪功能，支持选择图片后进行裁剪处理。

## 新增文件

| 文件 | 说明 |
|------|------|
| `server/.../common/util/ImageCropUtil.java` | 后端图片裁剪工具类 |
| `admin/src/components/ImageCropper.vue` | 图片裁剪弹窗组件 |
| `admin/src/components/CropUploadField.vue` | 支持裁剪的上传组件 |

## API 接口

### 上传图片（支持裁剪）

```
POST /admin/v1/uploads/images/crop
Content-Type: multipart/form-data
```

**参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 图片文件 |
| cropX | Integer | 否 | 裁剪起点X坐标 |
| cropY | Integer | 否 | 裁剪起点Y坐标 |
| cropW | Integer | 否 | 裁剪宽度 |
| cropH | Integer | 否 | 裁剪高度 |
| scale | Double | 否 | 缩放比例 |

**说明：** 裁剪参数为可选，不传则不裁剪。

## 组件使用

### 1. 基础用法（启用裁剪）

```vue
<template>
  <CropUploadField v-model="formData.coverUrl" />
</template>

<script setup>
import { ref } from 'vue'
import CropUploadField from '@/components/CropUploadField.vue'

const formData = ref({
  coverUrl: ''
})
</script>
```

### 2. 指定裁剪比例

```vue
<template>
  <!-- 1:1 方形裁剪 -->
  <CropUploadField 
    v-model="formData.avatar" 
    :crop-ratio="1"
    tip="建议上传 1:1 比例的头像图片"
  />
  
  <!-- 16:9 横向裁剪 -->
  <CropUploadField 
    v-model="formData.banner" 
    :crop-ratio="16/9"
    tip="建议上传 16:9 比例的横幅图片"
  />
</template>
```

### 3. 禁用裁剪功能

```vue
<template>
  <CropUploadField 
    v-model="formData.image" 
    :enable-crop="false"
    tip="不启用裁剪，直接上传原图"
  />
</template>
```

### 4. 完整表单示例

```vue
<template>
  <el-form :model="formData" label-width="100px">
    <el-form-item label="作品封面">
      <CropUploadField 
        v-model="formData.coverUrl"
        :crop-ratio="4/3"
        tip="建议尺寸 800x600，支持 JPG/PNG"
      />
    </el-form-item>
    
    <el-form-item label="作品图片">
      <CropUploadField 
        v-model="formData.imageUrl"
        :crop-ratio="null"
        tip="自由比例，支持任意尺寸"
      />
    </el-form-item>
    
    <el-form-item label="艺术家头像">
      <CropUploadField 
        v-model="formData.avatar"
        :crop-ratio="1"
        tip="请上传 1:1 正方形图片"
      />
    </el-form-item>
  </el-form>
</template>

<script setup>
import { reactive } from 'vue'
import CropUploadField from '@/components/CropUploadField.vue'

const formData = reactive({
  coverUrl: '',
  imageUrl: '',
  avatar: ''
})
</script>
```

## 支持的裁剪比例

| 比例 | 说明 | 适用场景 |
|------|------|----------|
| `null` | 自由比例 | 任意图片，不限制宽高 |
| `1` | 1:1 方形 | 头像、图标、方形缩略图 |
| `4/3` | 4:3 | 作品封面、新闻图片 |
| `16/9` | 16:9 | 横版Banner、横向图片 |
| `3/4` | 3:4 | 竖版图片、人物肖像 |

## 裁剪操作说明

1. **选择图片**：点击「选择图片」按钮上传图片
2. **调整裁剪框**：拖动裁剪框选择要保留的区域
3. **缩放**：使用 +/- 按钮或滚轮缩放图片
4. **旋转**：点击 90° 按钮旋转图片
5. **重置**：点击「重置」恢复初始状态
6. **确认裁剪**：点击「确认裁剪」完成上传

## 后端处理流程

```
1. 接收图片文件和裁剪参数
2. 如果有裁剪参数：
   a. 如果有 scale 参数，先缩放图片
   b. 根据 cropX/cropY/cropW/cropH 裁剪图片
3. 如果是 HEIF/HEIC 格式，转换为 JPEG
4. 保存到服务器
5. 返回图片 URL
```

## 注意事项

1. 支持的图片格式：JPG、JPEG、PNG、WEBP
2. 最大文件大小：20MB
3. 裁剪参数为相对于显示尺寸的坐标
4. 服务端会按比例计算实际裁剪区域
