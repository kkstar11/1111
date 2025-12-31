-- Migration script to rename item_location column to location in the item table
-- This ensures consistency between database column name and Java field name

-- For MySQL/MariaDB:
ALTER TABLE item CHANGE COLUMN item_location location VARCHAR(255) COMMENT '商品位置/交易地点';

-- Note: This migration should be run after deploying the code changes
-- The column is renamed from item_location to location to match the Java field naming
