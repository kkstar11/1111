-- Orders table for student second-hand trading platform
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '订单主键ID',
  `item_id` BIGINT NOT NULL COMMENT '商品ID',
  `buyer_id` BIGINT NOT NULL COMMENT '买家用户ID',
  `seller_id` BIGINT NOT NULL COMMENT '卖家用户ID',
  `status` INT NOT NULL DEFAULT 0 COMMENT '订单状态: 0-下单, 1-完成, 2-取消',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `finish_time` TIMESTAMP NULL DEFAULT NULL COMMENT '完成/取消时间',
  INDEX `idx_buyer_id` (`buyer_id`),
  INDEX `idx_seller_id` (`seller_id`),
  INDEX `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
