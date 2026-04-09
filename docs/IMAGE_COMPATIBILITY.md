# 图片兼容性优化说明

## 概述

本文档记录了拾艺局项目中图片存储和显示的兼容性优化方案。

## 优化内容

### 1. 后端 - ImageUrlUtil 工具类

**文件**: `server/src/main/java/com/shiyiju/common/util/ImageUrlUtil.java`

功能:
- 统一处理图片URL标准化
- 支持相对路径自动补全服务器地址
- 空值自动返回占位图
- 支持多种图片来源（本地/CDN/外部URL）
- 提供占位图生成功能

核心方法:
```java
// 标准化单个URL
String url = imageUrlUtil.normalize("/uploads/images/20260410/xxx.jpg");
// 结果: http://localhost:8080/uploads/images/20260410/xxx.jpg

// 带占位图的标准化
String url = imageUrlUtil.normalize(null, "https://placeholder.com/default.jpg");
// 结果: https://placeholder.com/default.jpg

// 批量标准化
String[] urls = imageUrlUtil.normalizeAll(url1, url2, url3);

// 检查URL有效性
boolean valid = imageUrlUtil.isValidImageUrl(url);

// 获取占位图
String placeholder = imageUrlUtil.getPlaceholderUrl("artwork123");
```

### 2. 前端 - imageUrl.js 工具模块

**文件**: `miniapp/utils/imageUrl.js`

功能:
- 微信小程序端统一的图片URL处理
- 支持相对路径/绝对路径/空值处理
- 提供各类占位图生成函数
- 批量处理图片字段

使用示例:
```javascript
const { 
  normalizeImageUrl,      // 标准化单个URL
  normalizeImageUrls,     // 批量标准化
  getPlaceholderUrl,      // 获取占位图
  getArtistAvatarPlaceholder,  // 艺术家头像占位图
  getArtworkCoverPlaceholder, // 作品封面占位图
  getBannerPlaceholder       // Banner占位图
} = require('../../utils/imageUrl')

// 标准化作品封面
const coverUrl = normalizeImageUrl(data.coverUrl, getArtworkCoverPlaceholder(id));

// 批量处理API响应
const normalizedData = normalizeImageFields(apiResponse, ['coverUrl', 'avatarUrl']);
```

### 3. 数据库优化

**文件**: `sql/migration_image_compatibility.sql`

新增内容:
- `sys_image_config` 表：存储图片服务器/CDN/占位图配置
- `media_asset` 表：统一的媒体资源管理表
- 数据修复脚本示例

### 4. 已更新组件

| 组件 | 文件 | 更新内容 |
|------|------|----------|
| 后端作品服务 | `ArtworkService.java` | 注入ImageUrlUtil，标准化图片URL |
| 小程序首页 | `home/index.js` | 使用统一图片处理工具 |
| 小程序详情页 | `artwork/detail.js` | 使用统一图片处理工具 |

## 图片URL格式支持

系统支持以下格式的图片URL:

| 格式 | 示例 | 处理方式 |
|------|------|----------|
| 完整HTTP URL | `https://cdn.example.com/img.jpg` | 直接返回 |
| 完整HTTP URL | `http://localhost:8080/img.jpg` | 直接返回 |
| 相对路径 | `/uploads/images/xxx.jpg` | 拼接服务器基础URL |
| 协议相对 | `//cdn.example.com/img.jpg` | 补全https: |
| 空值 | null / "" | 返回占位图 |

## 占位图策略

| 类型 | 占位图URL |
|------|-----------|
| 默认 | `https://picsum.photos/seed/default/400/500` |
| 作品封面 | `https://picsum.photos/seed/art{id}/400/500` |
| 艺术家头像 | `https://ui-avatars.com/api/?name={name}&background=c9a96d&color=fff&size=128` |
| Banner | `https://picsum.photos/seed/banner{index}/750/400` |

## 使用建议

### 前端开发
1. 优先使用 `normalizeImageUrl()` 处理所有图片URL
2. 为不同类型的图片使用对应的占位图生成函数
3. API响应数据可使用 `normalizeImageFields()` 批量处理

### 后端开发
1. 在Service层注入 `ImageUrlUtil`
2. 所有返回给前端的数据，图片字段应先通过 `normalize()` 处理
3. 配置信息存储在 `sys_image_config` 表中

### 数据库
1. 运行迁移脚本 `migration_image_compatibility.sql` 创建配置表
2. 定期检查无效的图片URL并修复
3. 考虑启用CDN加速图片加载

## 配置说明

在 `application.yml` 中配置服务器信息:
```yaml
server:
  port: 8080
  servlet:
    context-path: /
```

在数据库 `sys_image_config` 表中配置图片策略:
```sql
-- 查看配置
SELECT * FROM sys_image_config;

-- 更新服务器地址
UPDATE sys_image_config SET config_value = 'https://your-domain.com' 
WHERE config_key = 'storage.server.base_url';
```

## 常见问题

**Q: 图片在开发环境正常，生产环境不显示？**
A: 检查 `sys_image_config` 表中的 `storage.server.base_url` 是否配置正确指向生产服务器地址。

**Q: 某些图片加载失败？**
A: 使用 `isValidImageUrl()` 检查URL格式，确保是有效的HTTP(S) URL或相对路径。

**Q: 如何添加新的占位图类型？**
A: 在 `imageUrl.js` 中添加新的生成函数，在 `ImageUrlUtil.java` 中添加对应方法。
