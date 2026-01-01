# Order Item Price Fix Documentation

## Issue Description
Historical orders in the database had the `item_price` field set to NULL or 0, which caused incorrect price tracking for orders. The item price should be captured as a snapshot at the time of order creation to preserve historical pricing information.

## Root Cause
The backend order creation logic was not setting the `item_price` field when inserting new orders. The Order entity, mapper, and service implementation were missing the logic to capture and store the item price at order creation time.

## Solution Implemented

### 1. Database Schema Changes
- Added `item_price` column to the `orders` table with type `DECIMAL(10, 2) NOT NULL`
- The column stores a snapshot of the item price at the time of order creation

### 2. Data Migration (Already Executed)
The following SQL was executed to fix historical data:
```sql
UPDATE orders o 
JOIN item i ON o.item_id = i.id 
SET o.item_price = i.price 
WHERE o.item_price IS NULL OR o.item_price = 0;
```

### 3. Code Changes

#### Entity Layer
- **Order.java**: Added `itemPrice` field (BigDecimal) with getter and setter methods
- **OrderVO.java**: Added `itemPrice` field (BigDecimal) with getter and setter methods

#### Data Access Layer
- **OrderMapper.xml**: 
  - Updated `OrderMap` resultMap to include `item_price` column mapping
  - Updated `insert` statement to include `item_price` in the INSERT query

#### Service Layer
- **OrderServiceImpl.java**:
  - Updated `createOrder()` method to set `itemPrice` from the Item entity before inserting the order
  - Updated `toVO()` method to include `itemPrice` when converting Order to OrderVO

### 4. Documentation
- **migration_order_item_price.sql**: Migration script documenting the database changes
- **ORDER_ITEM_PRICE_FIX.md**: This document for tracking and future reference

## Impact
- **New Orders**: All new orders created after this fix will automatically have the `item_price` field populated with the correct price from the item at the time of order creation
- **Historical Orders**: All historical orders have been backfilled with the correct prices from their associated items
- **Data Integrity**: Order price information is now preserved as a snapshot, independent of future item price changes

## Testing Recommendations
1. Verify that new orders created through the API have the `item_price` field populated
2. Verify that the `item_price` in orders matches the item price at the time of creation
3. Verify that changing an item's price does not affect existing order prices (price snapshot behavior)

## Date Implemented
2026-01-01

## Related Files
- `/src/main/java/com/xianyu/entity/Order.java`
- `/src/main/java/com/xianyu/vo/OrderVO.java`
- `/src/main/java/com/xianyu/service/impl/OrderServiceImpl.java`
- `/src/main/resources/mapper/OrderMapper.xml`
- `/orders_schema.sql`
- `/migration_order_item_price.sql`
