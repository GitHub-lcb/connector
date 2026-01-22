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
