-- Migration script to add audit status for item management
-- This updates the item table to support admin audit workflow
-- Status values:
--   0 = 待审核 (Pending audit) - NEW
--   1 = 上架 (On sale/Approved)
--   2 = 下架 (Off sale)
--   3 = 已售出 (Sold)
--   4 = 审核驳回 (Rejected) - NEW

-- Update column comment to reflect new status values
ALTER TABLE item MODIFY COLUMN status INT COMMENT '商品状态: 0-待审核, 1-上架, 2-下架, 3-已售出, 4-审核驳回';

-- Optional: Add a check constraint to enforce valid status values
-- Uncomment the following line if you want database-level validation:
-- ALTER TABLE item ADD CONSTRAINT chk_item_status CHECK (status IN (0, 1, 2, 3, 4));

-- Note: Existing items may have status=1 (on sale). 
-- New items will be created with status=0 (pending) by default.
-- Admins can approve (change to 1) or reject (change to 4) pending items.
