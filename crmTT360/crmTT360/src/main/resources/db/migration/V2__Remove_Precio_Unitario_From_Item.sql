-- Flyway Migration Script: V2__Remove_Precio_Unitario_From_Item.sql
-- Description: Removes the incorrectly named 'precio_unitario' column from the 'item' table, if it exists.

-- Attempt to drop the column only if it exists to avoid errors in environments
-- where the schema might already be correct or the column was never added.
-- The specific syntax for IF EXISTS might vary slightly depending on the exact MySQL/MariaDB version,
-- but this is generally safer. If it causes issues, use the simpler ALTER TABLE command below.

-- Check if the column exists before trying to drop it (More robust approach)
-- This requires querying INFORMATION_SCHEMA, which might need specific privileges.
SET @exist := (SELECT COUNT(*) FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 'item' AND column_name = 'precio_unitario');
SET @sql = IF(@exist > 0, 'ALTER TABLE `item` DROP COLUMN `precio_unitario`', 'SELECT ''Column precio_unitario does not exist in item table.''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Simpler alternative (might fail if the column doesn't exist):
-- ALTER TABLE `item` DROP COLUMN `precio_unitario`;