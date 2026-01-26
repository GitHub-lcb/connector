ALTER TABLE `t_connector_route` ADD COLUMN `script_type` VARCHAR(50) DEFAULT NULL COMMENT 'Script Type: groovy, python';
ALTER TABLE `t_connector_route` ADD COLUMN `script_content` TEXT DEFAULT NULL COMMENT 'Script Content';
