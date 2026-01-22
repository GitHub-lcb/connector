-- 更新 route_logs 表，添加新的字段
ALTER TABLE `route_logs` 
ADD COLUMN `channel` VARCHAR(50) COMMENT '渠道',
ADD COLUMN `original_params` LONGTEXT COMMENT '原始入参',
ADD COLUMN `transformed_params` LONGTEXT COMMENT '转换后入参',
ADD COLUMN `response_data` LONGTEXT COMMENT '响应数据';
