-- =========================================================
-- 项目：拾艺局 小程序
-- 用途：图片兼容性和存储优化
-- 说明：确保数据库中各种图片URL格式兼容
-- =========================================================

USE shiyiju;
SET NAMES utf8mb4;

-- =========================================================
-- 1. 图片存储配置表
-- =========================================================

CREATE TABLE IF NOT EXISTS `sys_image_config` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` VARCHAR(500) DEFAULT NULL COMMENT '配置值',
  `config_type` VARCHAR(20) NOT NULL DEFAULT 'STORAGE' COMMENT 'STORAGE/CDN/PLACEHOLDER',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片存储配置表';

-- 初始化默认配置
INSERT INTO `sys_image_config` (`config_key`, `config_value`, `config_type`, `remark`) VALUES
  ('storage.local.path', '~/.shiyiju/uploads', 'STORAGE', '本地图片存储根目录'),
  ('storage.server.base_url', 'http://localhost:8080', 'STORAGE', '服务器基础URL'),
  ('cdn.enabled', 'false', 'CDN', '是否启用CDN'),
  ('cdn.domain', '', 'CDN', 'CDN域名'),
  ('placeholder.default', 'https://picsum.photos/seed/default/400/500', 'PLACEHOLDER', '默认占位图'),
  ('placeholder.artwork', 'https://picsum.photos/seed/artwork/400/500', 'PLACEHOLDER', '作品占位图'),
  ('placeholder.avatar', 'https://ui-avatars.com/api/?name=User&background=c9a96d&color=fff&size=128&rounded=true', 'PLACEHOLDER', '头像占位图')
ON DUPLICATE KEY UPDATE
  `config_value` = VALUES(`config_value`),
  `remark` = VALUES(`remark`);

-- =========================================================
-- 2. 图片媒体表（增强版，统一管理所有图片）
-- =========================================================

CREATE TABLE IF NOT EXISTS `media_asset` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `asset_key` VARCHAR(100) NOT NULL COMMENT '资源标识',
  `asset_type` VARCHAR(30) NOT NULL COMMENT 'IMAGE/VIDEO/DOCUMENT',
  `original_filename` VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
  `stored_filename` VARCHAR(255) DEFAULT NULL COMMENT '存储文件名',
  `file_path` VARCHAR(500) DEFAULT NULL COMMENT '文件路径',
  `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
  `mime_type` VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型',
  `width` INT DEFAULT NULL COMMENT '图片宽度',
  `height` INT DEFAULT NULL COMMENT '图片高度',
  `thumbnail_path` VARCHAR(500) DEFAULT NULL COMMENT '缩略图路径',
  `cdn_url` VARCHAR(500) DEFAULT NULL COMMENT 'CDN地址',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/DELETED',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_asset_key` (`asset_key`),
  KEY `idx_asset_type` (`asset_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体资源表';

-- =========================================================
-- 3. 图片格式兼容性检测（可选，用于历史数据迁移）
-- =========================================================

-- 检查哪些作品的封面URL可能是无效的
-- SELECT a.id, a.title, am.media_url 
-- FROM artwork a
-- LEFT JOIN artwork_media am ON a.id = am.artwork_id AND am.is_main = 1
-- WHERE am.media_url IS NULL OR am.media_url = '' OR am.media_url NOT LIKE 'http%';

-- =========================================================
-- 4. 数据修复脚本
-- =========================================================

-- 修复 artist_profile 表中可能为空的 avatar_url
-- UPDATE artist_profile SET avatar_url = (
--   SELECT config_value FROM sys_image_config WHERE config_key = 'placeholder.avatar'
-- ) WHERE avatar_url IS NULL OR avatar_url = '';

-- 修复 admin_operation_config 中相对路径的 image_url
-- 假设服务器地址是 http://localhost:8080
-- UPDATE admin_operation_config SET image_url = CONCAT('http://localhost:8080', image_url)
-- WHERE image_url LIKE '/%' AND image_url NOT LIKE 'http%';

-- =========================================================
-- 5. 验证查询
-- =========================================================

SELECT '=== 图片配置表 ===' as info;
SELECT * FROM sys_image_config;

SELECT '=== 媒体资源表记录数 ===' as info;
SELECT COUNT(*) as total_count FROM media_asset WHERE status = 'ACTIVE';

SELECT '=== 作品封面检查 ===' as info;
-- 显示一些作品封面示例
-- SELECT id, title, 
--   CASE 
--     WHEN cover_url LIKE 'http%' THEN '外部URL'
--     WHEN cover_url LIKE '/%' THEN '相对路径'
--     WHEN cover_url IS NULL OR cover_url = '' THEN '空值'
--     ELSE '其他'
--   END as url_type,
--   LEFT(cover_url, 50) as url_preview
-- FROM artwork 
-- LIMIT 10;
