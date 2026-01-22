-- Create database
CREATE DATABASE IF NOT EXISTS connector;
USE connector;

-- Create routes table
CREATE TABLE IF NOT EXISTS routes (
  id VARCHAR(36) PRIMARY KEY COMMENT '路由ID',
  name VARCHAR(255) NOT NULL COMMENT '路由名称',
  channel VARCHAR(50) COMMENT '渠道',
  source_path VARCHAR(255) NOT NULL UNIQUE COMMENT '源路径',
  target_url VARCHAR(1024) NOT NULL COMMENT '目标URL',
  method VARCHAR(10) NOT NULL COMMENT 'HTTP方法',
  mapping_config JSON COMMENT '映射配置',
  status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
  version INT DEFAULT 1 COMMENT '版本号',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='路由配置表';

-- Create route_logs table
CREATE TABLE IF NOT EXISTS route_logs (
  id VARCHAR(36) PRIMARY KEY COMMENT '日志ID',
  route_id VARCHAR(36) COMMENT '关联的路由ID',
  request_path VARCHAR(255) NOT NULL COMMENT '请求路径',
  status_code INT COMMENT '响应状态码',
  latency_ms INT COMMENT '延迟时间(毫秒)',
  error_msg TEXT COMMENT '错误信息',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE SET NULL
) COMMENT='路由日志表';

-- Create config_history table
CREATE TABLE IF NOT EXISTS config_history (
  id VARCHAR(36) PRIMARY KEY COMMENT '历史记录ID',
  route_id VARCHAR(36) COMMENT '关联的路由ID',
  old_config JSON COMMENT '旧配置',
  new_config JSON COMMENT '新配置',
  changed_by VARCHAR(36) COMMENT '修改人',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
) COMMENT='配置历史表';
