-- 添加订单冗余字段用于快速展示
-- 如果订单包含多个商品，这些字段存储第一个商品的信息

ALTER TABLE `trade_order` 
ADD COLUMN IF NOT EXISTS `item_title` VARCHAR(200) DEFAULT NULL COMMENT '冗余字段：首个商品标题' AFTER `remark`,
ADD COLUMN IF NOT EXISTS `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '冗余字段：首个商品封面图' AFTER `item_title`;

-- 为 item_title 和 cover_url 添加索引（可选，用于列表查询优化）
-- ALTER TABLE `trade_order` ADD INDEX `idx_item_title` (`item_title`);
-- ALTER TABLE `trade_order` ADD INDEX `idx_cover_url` (`cover_url`);
