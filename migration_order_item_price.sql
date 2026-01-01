-- Migration to add item_price column to orders table
-- Date: 2026-01-01
-- Purpose: Store item price snapshot in orders to preserve historical pricing

-- Step 1: Add item_price column to orders table
ALTER TABLE orders ADD COLUMN item_price DECIMAL(10, 2) COMMENT '商品价格快照';

-- Step 2: Backfill existing orders with item prices from item table
-- This SQL has been executed to fix historical data
UPDATE orders o 
JOIN item i ON o.item_id = i.id 
SET o.item_price = i.price 
WHERE o.item_price IS NULL OR o.item_price = 0;

-- Step 3: Make item_price NOT NULL after backfill
ALTER TABLE orders MODIFY COLUMN item_price DECIMAL(10, 2) NOT NULL COMMENT '商品价格快照';
