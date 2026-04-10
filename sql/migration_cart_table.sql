-- 用户购物车表
CREATE TABLE IF NOT EXISTS `user_cart` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `artwork_id` BIGINT NOT NULL COMMENT '作品ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '购买数量',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_artwork` (`user_id`, `artwork_id`, `deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_artwork_id` (`artwork_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户购物车表';
