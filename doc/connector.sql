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
  channel VARCHAR(50) COMMENT '渠道',
  original_params LONGTEXT COMMENT '原始入参',
  transformed_params LONGTEXT COMMENT '转换后入参',
  response_data LONGTEXT COMMENT '响应数据';
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

-- ----------------------------
-- Connector Module Menu Scripts
-- ----------------------------

-- 1. Connector Management (Directory)
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (301, '连接器管理', 1, 0, 100, '/connector', NULL, NULL, NULL, NULL, 'LinkOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 2. Route Configuration (Menu)
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (302, '路由配置', 2, 301, 1, '/connector/route-list', '/business/connector/route-list.vue', NULL, NULL, NULL, 'NodeIndexOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 3. Request Logs (Menu)
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (303, '请求日志', 2, 301, 2, '/connector/route-log-list', '/business/connector/route-log-list.vue', NULL, NULL, NULL, 'FileTextOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 4. Route Tester (Menu)
INSERT INTO `t_menu` (`menu_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (304, '路由测试', 2, 301, 3, '/connector/route-test', '/business/connector/route-test.vue', NULL, NULL, NULL, 'ExperimentOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- ----------------------------
-- Assign Menus to Admin Role (Role ID: 1)
-- ----------------------------
INSERT INTO `t_role_menu` (`role_id`, `menu_id`, `create_time`, `update_time`) VALUES (1, 300, NOW(), NOW());
INSERT INTO `t_role_menu` (`role_id`, `menu_id`, `create_time`, `update_time`) VALUES (1, 301, NOW(), NOW());
INSERT INTO `t_role_menu` (`role_id`, `menu_id`, `create_time`, `update_time`) VALUES (1, 302, NOW(), NOW());
INSERT INTO `t_role_menu` (`role_id`, `menu_id`, `create_time`, `update_time`) VALUES (1, 303, NOW(), NOW());

