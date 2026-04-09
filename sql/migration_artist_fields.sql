-- 艺术家管理扩展字段迁移
-- 执行此 SQL 添加城市和排序字段

ALTER TABLE `artist_profile` 
ADD COLUMN `city` VARCHAR(100) DEFAULT NULL COMMENT '艺术家所在城市' AFTER `artist_name`,
ADD COLUMN `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序权重，数字越小越靠前' AFTER `status`;

-- 为现有艺术家设置默认值
UPDATE `artist_profile` SET `city` = '', `sort_no` = `id`;

-- 创建索引优化排序查询
CREATE INDEX idx_sort_no ON artist_profile(sort_no);
