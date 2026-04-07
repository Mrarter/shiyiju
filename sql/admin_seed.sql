USE `shiyiju`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO artist_level (level_code, level_name, level_rank, price_growth_factor, sort_weight, is_front_visible, status)
VALUES
  ('SIGNED', '签约艺术家', 1, 1.0000, 100, 1, 'ACTIVE'),
  ('POTENTIAL', '潜力艺术家', 2, 1.1000, 80, 1, 'ACTIVE'),
  ('FAMOUS', '成名艺术家', 3, 1.2500, 120, 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE
  level_name = VALUES(level_name),
  price_growth_factor = VALUES(price_growth_factor),
  sort_weight = VALUES(sort_weight),
  is_front_visible = VALUES(is_front_visible),
  status = VALUES(status);

INSERT INTO user_account (id, user_no, nickname, avatar_url, mobile, gender, status, register_source, invitation_code_used, last_login_at)
VALUES
  (1001, 'SYJ10021', '木木', 'https://example.com/avatar-mumu.jpg', '13800000001', 2, 'ENABLED', 'WECHAT_MINIAPP', NULL, NOW()),
  (1002, 'SYJ10022', '阿泽', 'https://example.com/avatar-aze.jpg', '13800000002', 1, 'ENABLED', 'WECHAT_MINIAPP', NULL, NOW()),
  (1003, 'SYJ10023', 'Suki', 'https://example.com/avatar-suki.jpg', NULL, 0, 'DISABLED', 'WECHAT_MINIAPP', NULL, NOW()),
  (2001, 'SYJ20001', '艺术家林观山', 'https://example.com/artist-lin.jpg', NULL, 1, 'ENABLED', 'ADMIN_CREATE', NULL, NOW()),
  (2002, 'SYJ20002', '艺术家周岚', 'https://example.com/artist-zhou.jpg', NULL, 2, 'ENABLED', 'ADMIN_CREATE', NULL, NOW()),
  (2003, 'SYJ20003', '艺术家陈河', 'https://example.com/artist-chen.jpg', NULL, 1, 'ENABLED', 'ADMIN_CREATE', NULL, NOW())
ON DUPLICATE KEY UPDATE
  nickname = VALUES(nickname),
  avatar_url = VALUES(avatar_url),
  mobile = VALUES(mobile),
  gender = VALUES(gender),
  status = VALUES(status),
  last_login_at = VALUES(last_login_at);

INSERT INTO artist_profile (id, user_id, artist_name, real_name, level_id, slogan, bio, style_tags, avatar_url, background_image_url, follower_count, work_count, sale_count, total_sale_amount, is_signed, status)
VALUES
  (3001, 2001, '林观山', '林观山', (SELECT id FROM artist_level WHERE level_code = 'SIGNED' LIMIT 1), '把山水放进当代空间', '长期创作当代山水与城市风景，关注传统笔意与现代构成的融合。', '油画,当代,山水', 'https://example.com/artist-lin.jpg', 'https://example.com/bg-lin.jpg', 1280, 12, 8, 128000.00, 1, 'ACTIVE'),
  (3002, 2002, '周岚', '周岚', (SELECT id FROM artist_level WHERE level_code = 'POTENTIAL' LIMIT 1), '颜色是情绪的第二语言', '以版画与综合材料为主，作品带有明显的都市呼吸感。', '版画,新锐,综合材料', 'https://example.com/artist-zhou.jpg', 'https://example.com/bg-zhou.jpg', 860, 8, 4, 68000.00, 0, 'ACTIVE'),
  (3003, 2003, '陈河', '陈河', (SELECT id FROM artist_level WHERE level_code = 'FAMOUS' LIMIT 1), '在园林里写时间', '以水墨和园林题材见长，擅长沉静气质的空间叙事。', '水墨,园林,山水', 'https://example.com/artist-chen.jpg', 'https://example.com/bg-chen.jpg', 1520, 15, 10, 165000.00, 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE
  artist_name = VALUES(artist_name),
  level_id = VALUES(level_id),
  slogan = VALUES(slogan),
  bio = VALUES(bio),
  style_tags = VALUES(style_tags),
  avatar_url = VALUES(avatar_url),
  background_image_url = VALUES(background_image_url),
  follower_count = VALUES(follower_count),
  work_count = VALUES(work_count),
  sale_count = VALUES(sale_count),
  total_sale_amount = VALUES(total_sale_amount),
  is_signed = VALUES(is_signed),
  status = VALUES(status);

INSERT INTO artwork (id, artwork_no, artist_id, title, category, style, width_cm, height_cm, depth_cm, material, creation_year, description, base_price, current_price, price_status, online_days, favorite_count, view_count, sale_mode, support_group_buy, support_resale, resale_min_hold_days, inventory_total, inventory_available, status, published_at)
VALUES
  (4001, 'ART202604001', 3001, '春山可望', 'PAINTING', '当代山水', 80.00, 120.00, 3.00, '布面油画', '2025', '以温润的绿色层次重构山水的空间呼吸。', 10800.00, 12800.00, 'DYNAMIC', 15, 126, 826, 'NORMAL', 0, 1, 7, 1, 1, 'PUBLISHED', NOW()),
  (4002, 'ART202604002', 3002, '潮汐笔记', 'PRINT', '都市抽象', 60.00, 90.00, 2.00, '综合版画', '2025', '通过肌理和色层记录城市中情绪起伏的轨迹。', 7200.00, 8600.00, 'DYNAMIC', 12, 88, 530, 'NORMAL', 0, 1, 7, 2, 2, 'PUBLISHED', NOW()),
  (4003, 'ART202604003', 3003, '园林记忆', 'INK', '园林水墨', 68.00, 100.00, 2.00, '纸本水墨', '2024', '把园林中的折返、借景与留白做成时间性的叙事。', 13800.00, 16500.00, 'DYNAMIC', 21, 144, 920, 'NORMAL', 0, 1, 7, 1, 0, 'COLLECTED', NOW())
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  category = VALUES(category),
  style = VALUES(style),
  material = VALUES(material),
  description = VALUES(description),
  base_price = VALUES(base_price),
  current_price = VALUES(current_price),
  online_days = VALUES(online_days),
  favorite_count = VALUES(favorite_count),
  view_count = VALUES(view_count),
  inventory_total = VALUES(inventory_total),
  inventory_available = VALUES(inventory_available),
  status = VALUES(status),
  published_at = VALUES(published_at);

INSERT INTO artwork_media (artwork_id, media_type, media_url, sort_no, is_main)
VALUES
  (4001, 'IMAGE', 'https://example.com/art-4001-main.jpg', 1, 1),
  (4002, 'IMAGE', 'https://example.com/art-4002-main.jpg', 1, 1),
  (4003, 'IMAGE', 'https://example.com/art-4003-main.jpg', 1, 1);

INSERT INTO trade_order (id, order_no, buyer_user_id, order_type, order_status, payment_status, delivery_type, address_id, goods_amount, freight_amount, discount_amount, pay_amount, remark, paid_at, completed_at)
VALUES
  (5001, 'SYJ202604070001', 1001, 'PRIMARY', 'WAIT_DELIVER', 'PAID', 'ARTWORK', NULL, 12800.00, 0.00, 0.00, 12800.00, '后台演示订单1', NOW(), NULL),
  (5002, 'SYJ202604070002', 1002, 'PRIMARY', 'PENDING_PAYMENT', 'UNPAID', 'ARTWORK', NULL, 8600.00, 0.00, 0.00, 8600.00, '后台演示订单2', NULL, NULL),
  (5003, 'SYJ202604060018', 1003, 'PRIMARY', 'COMPLETED', 'PAID', 'ARTWORK', NULL, 16500.00, 0.00, 0.00, 16500.00, '后台演示订单3', NOW(), NOW())
ON DUPLICATE KEY UPDATE
  order_status = VALUES(order_status),
  payment_status = VALUES(payment_status),
  pay_amount = VALUES(pay_amount),
  remark = VALUES(remark),
  paid_at = VALUES(paid_at),
  completed_at = VALUES(completed_at);

INSERT INTO trade_order_item (order_id, artwork_id, artist_id, item_type, sku_no, item_title, cover_url, unit_price, quantity, subtotal_amount)
VALUES
  (5001, 4001, 3001, 'ARTWORK', 'ART202604001', '春山可望', 'https://example.com/art-4001-main.jpg', 12800.00, 1, 12800.00),
  (5002, 4002, 3002, 'ARTWORK', 'ART202604002', '潮汐笔记', 'https://example.com/art-4002-main.jpg', 8600.00, 1, 8600.00),
  (5003, 4003, 3003, 'ARTWORK', 'ART202604003', '园林记忆', 'https://example.com/art-4003-main.jpg', 16500.00, 1, 16500.00);

INSERT INTO shipment_order (order_id, shipment_no, shipment_type, logistics_company, logistics_code, tracking_no, shipment_status, consigned_at, signed_at)
VALUES
  (5001, 'SHIP202604070001', 'ARTWORK', '顺丰', 'SF', 'SF1234567890', 'TO_BE_SHIPPED', NULL, NULL),
  (5003, 'SHIP202604060018', 'ARTWORK', '顺丰', 'SF', 'SF0987654321', 'SIGNED', NOW(), NOW())
ON DUPLICATE KEY UPDATE
  logistics_company = VALUES(logistics_company),
  logistics_code = VALUES(logistics_code),
  tracking_no = VALUES(tracking_no),
  shipment_status = VALUES(shipment_status),
  consigned_at = VALUES(consigned_at),
  signed_at = VALUES(signed_at);

SET FOREIGN_KEY_CHECKS = 1;
