-- =========================================================
-- 项目：拾艺局 小程序
-- 版本：V1.0
-- 用途：MySQL 8.0 建表脚本（可直接作为开发起始版本）
-- 字符集：utf8mb4
-- 说明：
-- 1. 本脚本覆盖用户、艺术家、作品、订单、分销、证书、托管、转售、拼团、盲盒、独家发行等核心模块
-- 2. 金额统一使用 DECIMAL(12,2)
-- 3. 比例统一使用 DECIMAL(8,4)，如 0.1500 = 15%
-- 4. 时间统一使用 DATETIME
-- =========================================================

CREATE DATABASE IF NOT EXISTS `shiyiju` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `shiyiju`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
-- 1. 基础用户体系
-- =========================================================

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT NULL COMMENT '配置值',
  `config_group` VARCHAR(50) DEFAULT 'default' COMMENT '配置分组',
  `remark` VARCHAR(255) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_no` VARCHAR(32) NOT NULL COMMENT '用户编号',
  `nickname` VARCHAR(100) NOT NULL COMMENT '微信昵称',
  `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像',
  `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `gender` TINYINT DEFAULT 0 COMMENT '0未知 1男 2女',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ENABLED' COMMENT 'ENABLED/DISABLED/DELETED',
  `register_source` VARCHAR(30) NOT NULL DEFAULT 'WECHAT_MINIAPP' COMMENT '注册来源',
  `invitation_code_used` VARCHAR(50) DEFAULT NULL COMMENT '注册时使用的邀请码',
  `last_login_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_no` (`user_no`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户主表';

DROP TABLE IF EXISTS `user_wechat_auth`;
CREATE TABLE `user_wechat_auth` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `openid` VARCHAR(100) NOT NULL,
  `unionid` VARCHAR(100) DEFAULT NULL,
  `session_key` VARCHAR(255) DEFAULT NULL,
  `app_id` VARCHAR(64) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_unionid` (`unionid`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_user_wechat_auth_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信授权表';

DROP TABLE IF EXISTS `user_role_relation`;
CREATE TABLE `user_role_relation` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `role_code` VARCHAR(30) NOT NULL COMMENT 'ARTIST/DISTRIBUTOR/COLLECTOR/ADMIN',
  `is_default` TINYINT NOT NULL DEFAULT 0,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_code`),
  KEY `idx_role_code` (`role_code`),
  CONSTRAINT `fk_user_role_relation_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关系表';

DROP TABLE IF EXISTS `shipping_address`;
CREATE TABLE `shipping_address` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `receiver_name` VARCHAR(100) NOT NULL,
  `receiver_mobile` VARCHAR(20) NOT NULL,
  `province` VARCHAR(50) NOT NULL,
  `city` VARCHAR(50) NOT NULL,
  `district` VARCHAR(50) NOT NULL,
  `detail_address` VARCHAR(255) NOT NULL,
  `postal_code` VARCHAR(20) DEFAULT NULL,
  `is_default` TINYINT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_shipping_address_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- =========================================================
-- 2. 邀请码 / 冷启动 / 团队关系
-- =========================================================

DROP TABLE IF EXISTS `invitation_code`;
CREATE TABLE `invitation_code` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(50) NOT NULL COMMENT '邀请码',
  `code_type` VARCHAR(30) NOT NULL COMMENT 'PLATFORM/ARTIST/DISTRIBUTOR',
  `owner_user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '归属用户',
  `max_use_count` INT NOT NULL DEFAULT 1,
  `used_count` INT NOT NULL DEFAULT 0,
  `effective_start_at` DATETIME DEFAULT NULL,
  `effective_end_at` DATETIME DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/EXPIRED/DISABLED',
  `remark` VARCHAR(255) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_owner_user_id` (`owner_user_id`),
  CONSTRAINT `fk_invitation_code_owner_user_id` FOREIGN KEY (`owner_user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀请码表';

DROP TABLE IF EXISTS `invitation_record`;
CREATE TABLE `invitation_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `invitation_code_id` BIGINT UNSIGNED NOT NULL,
  `inviter_user_id` BIGINT UNSIGNED DEFAULT NULL,
  `invitee_user_id` BIGINT UNSIGNED NOT NULL,
  `bind_artist_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '可绑定来源艺术家',
  `bind_distributor_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '可绑定来源艺荐官',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_invitee_user_id` (`invitee_user_id`),
  KEY `idx_invitation_code_id` (`invitation_code_id`),
  KEY `idx_inviter_user_id` (`inviter_user_id`),
  CONSTRAINT `fk_invitation_record_code_id` FOREIGN KEY (`invitation_code_id`) REFERENCES `invitation_code` (`id`),
  CONSTRAINT `fk_invitation_record_inviter_user_id` FOREIGN KEY (`inviter_user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_invitation_record_invitee_user_id` FOREIGN KEY (`invitee_user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀请码使用记录表';

DROP TABLE IF EXISTS `distributor_profile`;
CREATE TABLE `distributor_profile` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `display_name` VARCHAR(100) NOT NULL COMMENT '艺荐官展示名',
  `bio` VARCHAR(500) DEFAULT NULL,
  `team_level` INT NOT NULL DEFAULT 1 COMMENT '团队层级，最多3',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  CONSTRAINT `fk_distributor_profile_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='艺荐官档案表';

DROP TABLE IF EXISTS `distributor_team_relation`;
CREATE TABLE `distributor_team_relation` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `distributor_id` BIGINT UNSIGNED NOT NULL COMMENT '当前艺荐官ID',
  `parent_distributor_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '直属上级艺荐官ID',
  `top_distributor_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '团队顶级ID',
  `team_depth` INT NOT NULL DEFAULT 1 COMMENT '1/2/3 层',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_distributor_id` (`distributor_id`),
  KEY `idx_parent_distributor_id` (`parent_distributor_id`),
  KEY `idx_top_distributor_id` (`top_distributor_id`),
  CONSTRAINT `fk_distributor_team_relation_distributor_id` FOREIGN KEY (`distributor_id`) REFERENCES `distributor_profile` (`id`),
  CONSTRAINT `fk_distributor_team_relation_parent_distributor_id` FOREIGN KEY (`parent_distributor_id`) REFERENCES `distributor_profile` (`id`),
  CONSTRAINT `fk_distributor_team_relation_top_distributor_id` FOREIGN KEY (`top_distributor_id`) REFERENCES `distributor_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='艺荐官团队关系表';

-- =========================================================
-- 3. 艺术家体系
-- =========================================================

DROP TABLE IF EXISTS `artist_level`;
CREATE TABLE `artist_level` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `level_code` VARCHAR(30) NOT NULL COMMENT 'NEW/POTENTIAL/FAMOUS/SIGNED/COLLECTION',
  `level_name` VARCHAR(50) NOT NULL COMMENT '新锐艺术家/潜力艺术家/成名艺术家/签约艺术家/典藏艺术家',
  `level_rank` INT NOT NULL,
  `price_growth_factor` DECIMAL(8,4) NOT NULL DEFAULT 1.0000 COMMENT '价格增长系数',
  `sort_weight` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
  `is_front_visible` TINYINT NOT NULL DEFAULT 1,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level_code` (`level_code`),
  UNIQUE KEY `uk_level_rank` (`level_rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='艺术家等级表';

DROP TABLE IF EXISTS `artist_profile`;
CREATE TABLE `artist_profile` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `artist_name` VARCHAR(100) NOT NULL COMMENT '艺术家名称',
  `real_name` VARCHAR(100) DEFAULT NULL,
  `level_id` BIGINT UNSIGNED DEFAULT NULL,
  `slogan` VARCHAR(255) DEFAULT NULL,
  `bio` TEXT DEFAULT NULL,
  `style_tags` VARCHAR(255) DEFAULT NULL COMMENT '风格标签，逗号分隔',
  `avatar_url` VARCHAR(500) DEFAULT NULL,
  `background_image_url` VARCHAR(500) DEFAULT NULL,
  `follower_count` INT NOT NULL DEFAULT 0,
  `work_count` INT NOT NULL DEFAULT 0,
  `sale_count` INT NOT NULL DEFAULT 0,
  `total_sale_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `is_signed` TINYINT NOT NULL DEFAULT 0 COMMENT '是否签约艺术家',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_level_id` (`level_id`),
  KEY `idx_artist_name` (`artist_name`),
  CONSTRAINT `fk_artist_profile_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_artist_profile_level_id` FOREIGN KEY (`level_id`) REFERENCES `artist_level` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='艺术家档案表';

DROP TABLE IF EXISTS `artist_follow`;
CREATE TABLE `artist_follow` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `artist_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '关注者ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_artist_user` (`artist_id`,`user_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_artist_follow_artist_id` FOREIGN KEY (`artist_id`) REFERENCES `artist_profile` (`id`),
  CONSTRAINT `fk_artist_follow_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='艺术家关注表';

-- =========================================================
-- 4. 作品 / 上架 / 定价
-- =========================================================

DROP TABLE IF EXISTS `artwork`;
CREATE TABLE `artwork` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `artwork_no` VARCHAR(50) NOT NULL COMMENT '作品编号',
  `artist_id` BIGINT UNSIGNED NOT NULL,
  `title` VARCHAR(200) NOT NULL COMMENT '作品名称',
  `category` VARCHAR(30) NOT NULL COMMENT 'OIL_PAINTING/CHINESE_PAINTING/OTHER',
  `style` VARCHAR(100) DEFAULT NULL,
  `width_cm` DECIMAL(10,2) DEFAULT NULL,
  `height_cm` DECIMAL(10,2) DEFAULT NULL,
  `depth_cm` DECIMAL(10,2) DEFAULT NULL,
  `material` VARCHAR(100) DEFAULT NULL,
  `creation_year` VARCHAR(20) DEFAULT NULL,
  `description` TEXT DEFAULT NULL,
  `base_price` DECIMAL(12,2) NOT NULL COMMENT '初始价格',
  `current_price` DECIMAL(12,2) NOT NULL COMMENT '当前价格',
  `price_status` VARCHAR(20) NOT NULL DEFAULT 'DYNAMIC' COMMENT 'FIXED/DYNAMIC',
  `online_days` INT NOT NULL DEFAULT 0 COMMENT '在线天数，可定时任务更新',
  `favorite_count` INT NOT NULL DEFAULT 0 COMMENT '被收藏/关注人数',
  `view_count` INT NOT NULL DEFAULT 0,
  `sale_mode` VARCHAR(30) NOT NULL DEFAULT 'NORMAL' COMMENT 'NORMAL/GROUP/BLIND_BOX/EXCLUSIVE',
  `support_group_buy` TINYINT NOT NULL DEFAULT 0,
  `support_resale` TINYINT NOT NULL DEFAULT 1,
  `resale_min_hold_days` INT NOT NULL DEFAULT 7,
  `inventory_total` INT NOT NULL DEFAULT 1,
  `inventory_available` INT NOT NULL DEFAULT 1,
  `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PUBLISHED/OFF_SHELF/COLLECTED/SOLD_OUT',
  `published_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_artwork_no` (`artwork_no`),
  KEY `idx_artist_id` (`artist_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sale_mode` (`sale_mode`),
  CONSTRAINT `fk_artwork_artist_id` FOREIGN KEY (`artist_id`) REFERENCES `artist_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作品主表';

DROP TABLE IF EXISTS `artwork_media`;
CREATE TABLE `artwork_media` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `media_type` VARCHAR(20) NOT NULL DEFAULT 'IMAGE' COMMENT 'IMAGE/VIDEO',
  `media_url` VARCHAR(500) NOT NULL,
  `sort_no` INT NOT NULL DEFAULT 1,
  `is_main` TINYINT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_artwork_id` (`artwork_id`),
  CONSTRAINT `fk_artwork_media_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作品媒体表';

DROP TABLE IF EXISTS `artwork_price_rule`;
CREATE TABLE `artwork_price_rule` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `online_days_weight` DECIMAL(8,4) NOT NULL DEFAULT 0.0000 COMMENT '在线天数权重',
  `follow_count_weight` DECIMAL(8,4) NOT NULL DEFAULT 0.0000 COMMENT '关注人数权重',
  `artist_float_min` DECIMAL(8,4) NOT NULL DEFAULT -0.0050 COMMENT '艺术家调价下限',
  `artist_float_max` DECIMAL(8,4) NOT NULL DEFAULT 0.0050 COMMENT '艺术家调价上限',
  `platform_max_growth_rate` DECIMAL(8,4) NOT NULL DEFAULT 0.3000 COMMENT '平台涨幅上限',
  `manual_adjust_rate` DECIMAL(8,4) NOT NULL DEFAULT 0.0000 COMMENT '人工调整比例',
  `formula_remark` VARCHAR(500) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_artwork_id` (`artwork_id`),
  CONSTRAINT `fk_artwork_price_rule_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作品动态定价规则表';

DROP TABLE IF EXISTS `artwork_sort_rule`;
CREATE TABLE `artwork_sort_rule` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `time_weight` INT NOT NULL DEFAULT 0,
  `heat_weight` INT NOT NULL DEFAULT 0,
  `manual_weight` INT NOT NULL DEFAULT 0,
  `artist_level_weight` INT NOT NULL DEFAULT 0,
  `final_weight` INT NOT NULL DEFAULT 0 COMMENT '可冗余存储用于排序',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_artwork_id` (`artwork_id`),
  CONSTRAINT `fk_artwork_sort_rule_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作品排序权重表';

DROP TABLE IF EXISTS `user_artwork_favorite`;
CREATE TABLE `user_artwork_favorite` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_artwork` (`user_id`,`artwork_id`),
  KEY `idx_artwork_id` (`artwork_id`),
  CONSTRAINT `fk_user_artwork_favorite_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_user_artwork_favorite_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作品收藏关注表';

-- =========================================================
-- 5. 订单 / 支付 / 物流 / 证书
-- =========================================================

DROP TABLE IF EXISTS `trade_order`;
CREATE TABLE `trade_order` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(50) NOT NULL,
  `buyer_user_id` BIGINT UNSIGNED NOT NULL COMMENT '买家/藏家',
  `order_type` VARCHAR(30) NOT NULL COMMENT 'PRIMARY/RESALE/GROUP/BLIND_BOX/CERTIFICATE',
  `order_status` VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT' COMMENT '待支付/已支付/待发货/已发货/已完成/已取消/退款中/已退款',
  `payment_status` VARCHAR(30) NOT NULL DEFAULT 'UNPAID',
  `delivery_type` VARCHAR(30) NOT NULL DEFAULT 'ARTWORK' COMMENT 'ARTWORK/CERTIFICATE/CUSTODY_TRANSFER',
  `address_id` BIGINT UNSIGNED DEFAULT NULL,
  `goods_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `freight_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `discount_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `pay_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `remark` VARCHAR(500) DEFAULT NULL,
  `paid_at` DATETIME DEFAULT NULL,
  `cancelled_at` DATETIME DEFAULT NULL,
  `completed_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_buyer_user_id` (`buyer_user_id`),
  KEY `idx_order_status` (`order_status`),
  CONSTRAINT `fk_trade_order_buyer_user_id` FOREIGN KEY (`buyer_user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_trade_order_address_id` FOREIGN KEY (`address_id`) REFERENCES `shipping_address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

DROP TABLE IF EXISTS `trade_order_item`;
CREATE TABLE `trade_order_item` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT UNSIGNED NOT NULL,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `artist_id` BIGINT UNSIGNED NOT NULL,
  `item_type` VARCHAR(30) NOT NULL DEFAULT 'ARTWORK' COMMENT 'ARTWORK/CERTIFICATE/BLIND_BOX_SHARE',
  `sku_no` VARCHAR(50) DEFAULT NULL,
  `item_title` VARCHAR(200) NOT NULL,
  `cover_url` VARCHAR(500) DEFAULT NULL,
  `unit_price` DECIMAL(12,2) NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  `subtotal_amount` DECIMAL(12,2) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_artwork_id` (`artwork_id`),
  CONSTRAINT `fk_trade_order_item_order_id` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`),
  CONSTRAINT `fk_trade_order_item_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`),
  CONSTRAINT `fk_trade_order_item_artist_id` FOREIGN KEY (`artist_id`) REFERENCES `artist_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT UNSIGNED NOT NULL,
  `payment_no` VARCHAR(50) NOT NULL,
  `payment_channel` VARCHAR(30) NOT NULL DEFAULT 'WECHAT_PAY',
  `transaction_no` VARCHAR(100) DEFAULT NULL COMMENT '第三方流水号',
  `amount` DECIMAL(12,2) NOT NULL,
  `payment_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/SUCCESS/FAIL/CLOSED/REFUND',
  `paid_at` DATETIME DEFAULT NULL,
  `callback_at` DATETIME DEFAULT NULL,
  `raw_callback` JSON DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_transaction_no` (`transaction_no`),
  CONSTRAINT `fk_payment_record_order_id` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

DROP TABLE IF EXISTS `shipment_order`;
CREATE TABLE `shipment_order` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT UNSIGNED NOT NULL,
  `shipment_no` VARCHAR(50) NOT NULL,
  `shipment_type` VARCHAR(30) NOT NULL DEFAULT 'ARTWORK' COMMENT 'ARTWORK/CERTIFICATE',
  `logistics_company` VARCHAR(100) DEFAULT NULL,
  `logistics_code` VARCHAR(50) DEFAULT NULL,
  `tracking_no` VARCHAR(100) DEFAULT NULL,
  `shipment_status` VARCHAR(30) NOT NULL DEFAULT 'TO_BE_SHIPPED' COMMENT '待发货/运输中/已签收/异常',
  `consigned_at` DATETIME DEFAULT NULL,
  `signed_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shipment_no` (`shipment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_tracking_no` (`tracking_no`),
  CONSTRAINT `fk_shipment_order_order_id` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发货单表';

DROP TABLE IF EXISTS `shipment_track`;
CREATE TABLE `shipment_track` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `shipment_id` BIGINT UNSIGNED NOT NULL,
  `track_time` DATETIME NOT NULL,
  `track_content` VARCHAR(500) NOT NULL,
  `location` VARCHAR(255) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_shipment_id` (`shipment_id`),
  KEY `idx_track_time` (`track_time`),
  CONSTRAINT `fk_shipment_track_shipment_id` FOREIGN KEY (`shipment_id`) REFERENCES `shipment_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹表';

DROP TABLE IF EXISTS `collection_certificate`;
CREATE TABLE `collection_certificate` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `certificate_no` VARCHAR(50) NOT NULL,
  `certificate_type` VARCHAR(30) NOT NULL DEFAULT 'ELECTRONIC' COMMENT 'ELECTRONIC/PAPER',
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `owner_user_id` BIGINT UNSIGNED NOT NULL COMMENT '当前持有人',
  `order_id` BIGINT UNSIGNED DEFAULT NULL,
  `share_ratio` DECIMAL(8,4) NOT NULL DEFAULT 1.0000 COMMENT '拼团持有比例',
  `owner_display_mode` VARCHAR(20) NOT NULL DEFAULT 'PRIVATE' COMMENT 'PUBLIC/PRIVATE',
  `certificate_status` VARCHAR(20) NOT NULL DEFAULT 'VALID' COMMENT 'VALID/TRANSFERRED/VOID',
  `certificate_file_url` VARCHAR(500) DEFAULT NULL,
  `issued_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_certificate_no` (`certificate_no`),
  KEY `idx_artwork_id` (`artwork_id`),
  KEY `idx_owner_user_id` (`owner_user_id`),
  CONSTRAINT `fk_collection_certificate_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`),
  CONSTRAINT `fk_collection_certificate_owner_user_id` FOREIGN KEY (`owner_user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_collection_certificate_order_id` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏证书表';

DROP TABLE IF EXISTS `artwork_ownership_flow`;
CREATE TABLE `artwork_ownership_flow` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `from_user_id` BIGINT UNSIGNED DEFAULT NULL,
  `to_user_id` BIGINT UNSIGNED NOT NULL,
  `flow_type` VARCHAR(30) NOT NULL COMMENT 'PRIMARY_BUY/RESALE/SHARE_TRANSFER/CUSTODY_TRANSFER',
  `order_id` BIGINT UNSIGNED DEFAULT NULL,
  `flow_remark` VARCHAR(255) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_artwork_id` (`artwork_id`),
  KEY `idx_to_user_id` (`to_user_id`),
  CONSTRAINT `fk_artwork_ownership_flow_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`),
  CONSTRAINT `fk_artwork_ownership_flow_from_user_id` FOREIGN KEY (`from_user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_artwork_ownership_flow_to_user_id` FOREIGN KEY (`to_user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_artwork_ownership_flow_order_id` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作品流转记录表';

-- =========================================================
-- 6. 托管系统
-- =========================================================

DROP TABLE IF EXISTS `custody_order`;
CREATE TABLE `custody_order` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `custody_no` VARCHAR(50) NOT NULL,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `owner_user_id` BIGINT UNSIGNED NOT NULL,
  `custody_mode` VARCHAR(30) NOT NULL COMMENT 'PLATFORM/ARTIST',
  `billing_mode` VARCHAR(30) NOT NULL COMMENT 'PERCENT_MONTH/FIXED_MONTH/FIXED_YEAR',
  `billing_rate` DECIMAL(8,4) DEFAULT NULL COMMENT '百分比费率，默认1%/月',
  `billing_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '固定费用',
  `start_at` DATETIME NOT NULL,
  `end_at` DATETIME DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/EXPIRED/CANCELLED',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_custody_no` (`custody_no`),
  KEY `idx_artwork_id` (`artwork_id`),
  KEY `idx_owner_user_id` (`owner_user_id`),
  CONSTRAINT `fk_custody_order_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`),
  CONSTRAINT `fk_custody_order_owner_user_id` FOREIGN KEY (`owner_user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='托管订单表';

DROP TABLE IF EXISTS `custody_fee_record`;
CREATE TABLE `custody_fee_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `custody_order_id` BIGINT UNSIGNED NOT NULL,
  `billing_period` VARCHAR(20) NOT NULL COMMENT '2026-04',
  `amount` DECIMAL(12,2) NOT NULL,
  `payment_status` VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
  `paid_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_custody_period` (`custody_order_id`,`billing_period`),
  CONSTRAINT `fk_custody_fee_record_custody_order_id` FOREIGN KEY (`custody_order_id`) REFERENCES `custody_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='托管费用记录表';

-- =========================================================
-- 7. 分销 / 佣金
-- =========================================================

DROP TABLE IF EXISTS `distributor_share_record`;
CREATE TABLE `distributor_share_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `distributor_id` BIGINT UNSIGNED NOT NULL,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `share_url` VARCHAR(500) DEFAULT NULL,
  `share_poster_url` VARCHAR(500) DEFAULT NULL,
  `share_pv` INT NOT NULL DEFAULT 0,
  `share_uv` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_distributor_id` (`distributor_id`),
  KEY `idx_artwork_id` (`artwork_id`),
  CONSTRAINT `fk_distributor_share_record_distributor_id` FOREIGN KEY (`distributor_id`) REFERENCES `distributor_profile` (`id`),
  CONSTRAINT `fk_distributor_share_record_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='艺荐官分享记录表';

DROP TABLE IF EXISTS `commission_record`;
CREATE TABLE `commission_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT UNSIGNED NOT NULL,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '佣金归属用户',
  `distributor_id` BIGINT UNSIGNED DEFAULT NULL,
  `commission_type` VARCHAR(30) NOT NULL COMMENT 'DIRECT_L1/DIRECT_L2/TEAM_L1/TEAM_L2/TEAM_L3/ARTIST_RESALE/PLATFORM',
  `commission_rate` DECIMAL(8,4) NOT NULL,
  `commission_amount` DECIMAL(12,2) NOT NULL,
  `settlement_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/SETTLED/CANCELLED',
  `settled_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_distributor_id` (`distributor_id`),
  CONSTRAINT `fk_commission_record_order_id` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`),
  CONSTRAINT `fk_commission_record_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`),
  CONSTRAINT `fk_commission_record_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_commission_record_distributor_id` FOREIGN KEY (`distributor_id`) REFERENCES `distributor_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金记录表';

DROP TABLE IF EXISTS `wallet_account`;
CREATE TABLE `wallet_account` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `available_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `frozen_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `total_income_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `total_withdraw_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  CONSTRAINT `fk_wallet_account_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包账户表';

DROP TABLE IF EXISTS `wallet_flow`;
CREATE TABLE `wallet_flow` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `wallet_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `biz_type` VARCHAR(30) NOT NULL COMMENT 'COMMISSION/SALE/CUSTODY_FEE/WITHDRAW/REFUND',
  `biz_no` VARCHAR(50) DEFAULT NULL,
  `direction` VARCHAR(10) NOT NULL COMMENT 'IN/OUT',
  `amount` DECIMAL(12,2) NOT NULL,
  `balance_after` DECIMAL(12,2) NOT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_wallet_id` (`wallet_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_wallet_flow_wallet_id` FOREIGN KEY (`wallet_id`) REFERENCES `wallet_account` (`id`),
  CONSTRAINT `fk_wallet_flow_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包流水表';

-- =========================================================
-- 8. 转售（二级流通）
-- =========================================================

DROP TABLE IF EXISTS `resale_listing`;
CREATE TABLE `resale_listing` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `listing_no` VARCHAR(50) NOT NULL,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `seller_user_id` BIGINT UNSIGNED NOT NULL,
  `certificate_id` BIGINT UNSIGNED DEFAULT NULL,
  `listing_type` VARCHAR(30) NOT NULL DEFAULT 'FULL' COMMENT 'FULL/SHARE',
  `share_ratio` DECIMAL(8,4) NOT NULL DEFAULT 1.0000,
  `transfer_mode` VARCHAR(30) NOT NULL DEFAULT 'PHYSICAL' COMMENT 'PHYSICAL/CUSTODY_TRANSFER',
  `listing_price` DECIMAL(12,2) NOT NULL,
  `min_price` DECIMAL(12,2) DEFAULT NULL,
  `max_price` DECIMAL(12,2) DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'LISTED' COMMENT 'LISTED/SOLD/CANCELLED/EXPIRED',
  `listed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sold_at` DATETIME DEFAULT NULL,
  `expired_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_listing_no` (`listing_no`),
  KEY `idx_artwork_id` (`artwork_id`),
  KEY `idx_seller_user_id` (`seller_user_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_resale_listing_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`),
  CONSTRAINT `fk_resale_listing_seller_user_id` FOREIGN KEY (`seller_user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_resale_listing_certificate_id` FOREIGN KEY (`certificate_id`) REFERENCES `collection_certificate` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转售挂单表';

DROP TABLE IF EXISTS `resale_trade`;
CREATE TABLE `resale_trade` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `listing_id` BIGINT UNSIGNED NOT NULL,
  `order_id` BIGINT UNSIGNED NOT NULL,
  `seller_user_id` BIGINT UNSIGNED NOT NULL,
  `buyer_user_id` BIGINT UNSIGNED NOT NULL,
  `trade_price` DECIMAL(12,2) NOT NULL,
  `platform_fee_rate` DECIMAL(8,4) NOT NULL DEFAULT 0.0500,
  `platform_fee_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `artist_fee_rate` DECIMAL(8,4) NOT NULL DEFAULT 0.0500,
  `artist_fee_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `settlement_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `trade_status` VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_listing_id` (`listing_id`),
  KEY `idx_seller_user_id` (`seller_user_id`),
  KEY `idx_buyer_user_id` (`buyer_user_id`),
  CONSTRAINT `fk_resale_trade_listing_id` FOREIGN KEY (`listing_id`) REFERENCES `resale_listing` (`id`),
  CONSTRAINT `fk_resale_trade_order_id` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`),
  CONSTRAINT `fk_resale_trade_seller_user_id` FOREIGN KEY (`seller_user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_resale_trade_buyer_user_id` FOREIGN KEY (`buyer_user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转售成交表';

-- =========================================================
-- 9. 拼团收藏（份额持有）
-- =========================================================

DROP TABLE IF EXISTS `group_buy_campaign`;
CREATE TABLE `group_buy_campaign` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `campaign_no` VARCHAR(50) NOT NULL,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `initiator_user_id` BIGINT UNSIGNED NOT NULL,
  `target_member_count` INT NOT NULL DEFAULT 2,
  `min_share_ratio` DECIMAL(8,4) NOT NULL DEFAULT 0.1000,
  `current_share_ratio` DECIMAL(8,4) NOT NULL DEFAULT 0.0000,
  `campaign_status` VARCHAR(20) NOT NULL DEFAULT 'RECRUITING' COMMENT 'RECRUITING/SUCCESS/FAILED/CANCELLED',
  `expire_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_campaign_no` (`campaign_no`),
  KEY `idx_artwork_id` (`artwork_id`),
  KEY `idx_initiator_user_id` (`initiator_user_id`),
  CONSTRAINT `fk_group_buy_campaign_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`),
  CONSTRAINT `fk_group_buy_campaign_initiator_user_id` FOREIGN KEY (`initiator_user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拼团活动表';

DROP TABLE IF EXISTS `group_buy_member`;
CREATE TABLE `group_buy_member` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `campaign_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `order_id` BIGINT UNSIGNED DEFAULT NULL,
  `share_ratio` DECIMAL(8,4) NOT NULL,
  `amount` DECIMAL(12,2) NOT NULL,
  `member_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/PAID/JOINED/REFUNDED',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_campaign_user` (`campaign_id`,`user_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_group_buy_member_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `group_buy_campaign` (`id`),
  CONSTRAINT `fk_group_buy_member_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_group_buy_member_order_id` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拼团成员表';

-- =========================================================
-- 10. 盲盒系统
-- =========================================================

DROP TABLE IF EXISTS `blind_box`;
CREATE TABLE `blind_box` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `box_no` VARCHAR(50) NOT NULL,
  `box_name` VARCHAR(100) NOT NULL,
  `box_type` VARCHAR(30) NOT NULL COMMENT 'ARTIST/THEME/NEWCOMER',
  `cover_url` VARCHAR(500) DEFAULT NULL,
  `sale_price` DECIMAL(12,2) NOT NULL,
  `guaranteed_value` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `max_value` DECIMAL(12,2) DEFAULT NULL,
  `stock_total` INT NOT NULL DEFAULT 0,
  `stock_available` INT NOT NULL DEFAULT 0,
  `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PUBLISHED/OFF_SHELF/SOLD_OUT',
  `start_at` DATETIME DEFAULT NULL,
  `end_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_box_no` (`box_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盲盒主表';

DROP TABLE IF EXISTS `blind_box_item`;
CREATE TABLE `blind_box_item` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `blind_box_id` BIGINT UNSIGNED NOT NULL,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `probability` DECIMAL(8,4) NOT NULL COMMENT '概率，例如0.1000',
  `sort_no` INT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_blind_box_id` (`blind_box_id`),
  KEY `idx_artwork_id` (`artwork_id`),
  CONSTRAINT `fk_blind_box_item_blind_box_id` FOREIGN KEY (`blind_box_id`) REFERENCES `blind_box` (`id`),
  CONSTRAINT `fk_blind_box_item_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盲盒作品池表';

DROP TABLE IF EXISTS `blind_box_open_record`;
CREATE TABLE `blind_box_open_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `blind_box_id` BIGINT UNSIGNED NOT NULL,
  `order_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `artwork_id` BIGINT UNSIGNED NOT NULL COMMENT '开出的作品',
  `opened_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_blind_box_id` (`blind_box_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_blind_box_open_record_blind_box_id` FOREIGN KEY (`blind_box_id`) REFERENCES `blind_box` (`id`),
  CONSTRAINT `fk_blind_box_open_record_order_id` FOREIGN KEY (`order_id`) REFERENCES `trade_order` (`id`),
  CONSTRAINT `fk_blind_box_open_record_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_blind_box_open_record_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盲盒开启记录表';

-- =========================================================
-- 11. 独家发行 / 首发
-- =========================================================

DROP TABLE IF EXISTS `exclusive_release`;
CREATE TABLE `exclusive_release` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `release_no` VARCHAR(50) NOT NULL,
  `artwork_id` BIGINT UNSIGNED NOT NULL,
  `artist_id` BIGINT UNSIGNED NOT NULL,
  `release_title` VARCHAR(200) NOT NULL,
  `release_type` VARCHAR(30) NOT NULL DEFAULT 'EXCLUSIVE' COMMENT 'EXCLUSIVE/LIMITED/FIRST_RELEASE',
  `release_total_qty` INT NOT NULL DEFAULT 1,
  `appointment_enabled` TINYINT NOT NULL DEFAULT 0,
  `white_list_enabled` TINYINT NOT NULL DEFAULT 0,
  `sale_start_at` DATETIME DEFAULT NULL,
  `sale_end_at` DATETIME DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/UPCOMING/ON_SALE/ENDED/CANCELLED',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_release_no` (`release_no`),
  KEY `idx_artwork_id` (`artwork_id`),
  KEY `idx_artist_id` (`artist_id`),
  CONSTRAINT `fk_exclusive_release_artwork_id` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`),
  CONSTRAINT `fk_exclusive_release_artist_id` FOREIGN KEY (`artist_id`) REFERENCES `artist_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='独家发行表';

DROP TABLE IF EXISTS `exclusive_release_appointment`;
CREATE TABLE `exclusive_release_appointment` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `release_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'BOOKED' COMMENT 'BOOKED/CANCELLED/QUALIFIED/BOUGHT',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_release_user` (`release_id`,`user_id`),
  CONSTRAINT `fk_exclusive_release_appointment_release_id` FOREIGN KEY (`release_id`) REFERENCES `exclusive_release` (`id`),
  CONSTRAINT `fk_exclusive_release_appointment_user_id` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='独家发行预约表';

-- =========================================================
-- 12. 初始化建议数据（可按需保留）
-- =========================================================

INSERT INTO `artist_level` (`level_code`, `level_name`, `level_rank`, `price_growth_factor`, `sort_weight`, `is_front_visible`, `status`) VALUES
('NEW', '新锐艺术家', 1, 1.0000, 10, 1, 'ACTIVE'),
('POTENTIAL', '潜力艺术家', 2, 1.1000, 20, 1, 'ACTIVE'),
('FAMOUS', '成名艺术家', 3, 1.2000, 30, 1, 'ACTIVE'),
('SIGNED', '签约艺术家', 4, 1.3500, 40, 1, 'ACTIVE'),
('COLLECTION', '典藏艺术家', 5, 1.5000, 50, 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE `level_name` = VALUES(`level_name`), `price_growth_factor` = VALUES(`price_growth_factor`), `sort_weight` = VALUES(`sort_weight`), `status` = VALUES(`status`);

INSERT INTO `sys_config` (`config_key`, `config_value`, `config_group`, `remark`) VALUES
('platform.commission.rate', '0.1000', 'trade', '平台默认抽成 10%'),
('distribution.total.max.rate', '0.3000', 'distribution', '分销总比例不高于 30%'),
('distribution.level.max', '2', 'distribution', '分销层级最多 2 级'),
('distribution.team.level.max', '3', 'distribution', '团队层级最多 3 级'),
('artwork.price.float.min', '-0.0050', 'pricing', '艺术家调价下限 -0.5%'),
('artwork.price.float.max', '0.0050', 'pricing', '艺术家调价上限 +0.5%'),
('custody.default.percent.month', '0.0100', 'custody', '托管默认 1%/月'),
('resale.min.hold.days', '7', 'resale', '最短持有天数 7 天')
ON DUPLICATE KEY UPDATE `config_value` = VALUES(`config_value`), `remark` = VALUES(`remark`);

SET FOREIGN_KEY_CHECKS = 1;
