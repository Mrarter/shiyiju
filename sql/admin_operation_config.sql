USE `shiyiju`;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `admin_operation_config` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(120) NOT NULL,
  `type` VARCHAR(30) NOT NULL COMMENT 'BANNER/HOT/GROWTH/ARTIST/NOTICE',
  `target` VARCHAR(255) DEFAULT NULL,
  `image_url` VARCHAR(500) DEFAULT NULL COMMENT 'Banner图片URL',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
  `sort_no` INT NOT NULL DEFAULT 0,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运营后台首页配置表';

INSERT INTO `admin_operation_config` (`id`, `title`, `type`, `target`, `image_url`, `status`, `sort_no`)
VALUES
  (1, '春季主视觉 Banner', 'BANNER', '首页主视觉', 'https://picsum.photos/seed/banner1/750/400', 'ENABLED', 10),
  (2, '热门藏品推荐', 'HOT', '作品 8 个', 'https://picsum.photos/seed/banner2/750/400', 'ENABLED', 20),
  (3, '推荐艺术家', 'ARTIST', '艺术家 6 位', 'https://picsum.photos/seed/banner3/750/400', 'DRAFT', 30)
ON DUPLICATE KEY UPDATE
  `title` = VALUES(`title`),
  `type` = VALUES(`type`),
  `target` = VALUES(`target`),
  `image_url` = VALUES(`image_url`),
  `status` = VALUES(`status`),
  `sort_no` = VALUES(`sort_no`);
