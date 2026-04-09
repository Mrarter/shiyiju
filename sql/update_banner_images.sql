-- 更新 Banner 图片 URL
-- 执行: mysql -u root -p shiyiju < update_banner_images.sql

USE shiyiju;

-- 添加 image_url 列（如果不存在）
ALTER TABLE admin_operation_config ADD COLUMN IF NOT EXISTS `image_url` VARCHAR(500) DEFAULT NULL COMMENT 'Banner图片URL';

-- 更新现有的 Banner 数据
UPDATE admin_operation_config SET image_url = 'https://picsum.photos/seed/banner1/750/400' WHERE id = 1;
UPDATE admin_operation_config SET image_url = 'https://picsum.photos/seed/banner2/750/400' WHERE id = 2;
UPDATE admin_operation_config SET image_url = 'https://picsum.photos/seed/banner3/750/400' WHERE id = 3;

-- 验证结果
SELECT id, title, type, image_url FROM admin_operation_config;
