-- Migration script to rename student_user table to users
-- This aligns the table name with the standard naming convention

-- Step 1: Rename the table
ALTER TABLE student_user RENAME TO users;

-- Step 2: Remove real_name and avatar_url columns (if they exist)
-- Note: Use IF EXISTS syntax for MySQL 8.0.23+, otherwise check manually
ALTER TABLE users DROP COLUMN IF EXISTS real_name;
ALTER TABLE users DROP COLUMN IF EXISTS avatar_url;

-- Note: This migration should be run carefully in production
-- Make sure to backup the database before running this script
-- All foreign keys and indexes will be preserved during the rename operation
