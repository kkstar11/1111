-- Migration script to rename item_status column to status in the item table
-- This ensures consistency between database column name and Java field name

-- For MySQL/MariaDB:
ALTER TABLE item CHANGE COLUMN item_status status INT COMMENT '商品状态: 1-上架, 2-下架, 3-已售出';

-- Note: This migration should be run after deploying the code changes
-- The column is renamed from item_status to status to match the Java field naming
