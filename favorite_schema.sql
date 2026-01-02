-- Favorite table for student second-hand trading platform
-- Table name: favorite
-- Fields: user_id, item_id, create_time as per requirements
CREATE TABLE IF NOT EXISTS `favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '收藏记录主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `item_id` BIGINT NOT NULL COMMENT '商品ID',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  UNIQUE KEY `uk_user_item` (`user_id`, `item_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_item_id` (`item_id`),
  INDEX `idx_create_time` (`create_time`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_favorite_item` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';
