-- 艺术家扩展字段迁移 v2
-- 添加：简介、毕业院校、获奖经历、年龄

ALTER TABLE `artist_profile`
ADD COLUMN `bio` TEXT DEFAULT NULL COMMENT '艺术家简介' AFTER `background_image_url`,
ADD COLUMN `graduated_from` VARCHAR(255) DEFAULT NULL COMMENT '毕业院校' AFTER `bio`,
ADD COLUMN `awards` TEXT DEFAULT NULL COMMENT '获奖经历' AFTER `graduated_from`,
ADD COLUMN `age` INT DEFAULT NULL COMMENT '艺术家年龄' AFTER `awards`;
