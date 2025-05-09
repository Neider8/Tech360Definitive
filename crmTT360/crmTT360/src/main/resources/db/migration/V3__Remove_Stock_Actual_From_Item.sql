-- src/main/resources/db/migration/V3__Remove_Stock_Actual_From_Item.sql
-- Description: Elimina la columna obsoleta/incorrecta 'stock_actual' de la tabla 'item'.

-- Intenta eliminar la columna solo si existe para evitar errores si ya fue eliminada.
-- Adaptar sintaxis si es necesario para tu versión específica de MySQL/MariaDB.
SET @exist := (SELECT COUNT(*) FROM information_schema.columns
               WHERE table_schema = DATABASE() AND table_name = 'item' AND column_name = 'stock_actual');
SET @sql = IF(@exist > 0, 'ALTER TABLE `item` DROP COLUMN `stock_actual`', 'SELECT ''Columna stock_actual no existe en la tabla item.''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- NOTA: Si el comando anterior da problemas, puedes usar el ALTER TABLE directo,
-- pero fallará si la columna no existe. Comenta el bloque anterior y descomenta la siguiente línea:
-- ALTER TABLE `item` DROP COLUMN `stock_actual`;