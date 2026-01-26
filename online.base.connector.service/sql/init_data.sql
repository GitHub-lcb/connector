INSERT INTO `t_tenant` VALUES (1, '默认租户', 'Admin', '13800138000', 1, NULL, NOW(), NOW());
INSERT INTO `t_employee`(`employee_id`, `employee_uid`, `login_name`, `login_pwd`, `actual_name`, `avatar`, `gender`, `phone`, `department_id`, `position_id`, `email`, `disabled_flag`, `deleted_flag`, `administrator_flag`, `remark`, `update_time`, `create_time`) VALUES (1, 'cf1e361fd46741f5b2a09335cef50db8', 'admin', '$argon2id$v=19$m=16384,t=2,p=1$d9yQEAhck+haKxP2ZtXocg$NEnw3D2Ly8UbYpy2odATLA4ZflZ1FKJjWCuOGrVE4PM', '管理员', 'public/common/f36e59b20faa4720b225edf81d15727a_20250713220349.jpeg', 0, '13500000000', 1, 3, NULL, 0, 0, 1, NULL, NOW(), NOW());
INSERT INTO `t_mail_template` (`template_code`, `template_subject`, `template_content`, `template_type`, `disable_flag`, `update_time`, `create_time`) VALUES ('login_verification_code', '登录验证码', '<!DOCTYPE HTML>\r\n<html>\r\n<head>\r\n  <title>登录提醒</title>\r\n  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\r\n  <style>\r\n      * {\r\n          font-family: SimSun;\r\n          /* 4号字体 */\r\n          font-size: 18px;\r\n          /* 22磅行间距 */\r\n          line-height: 29px;\r\n      }\r\n\r\n      .main_font_size {\r\n          font-size: 12.0pt;\r\n      }\r\n\r\n      .mainContent {\r\n          line-height: 28px;\r\n      }\r\n\r\n      p {\r\n          margin: 0 auto;\r\n          text-align: justify;\r\n      }\r\n  </style>\r\n\r\n</head>\r\n<body>\r\n<div>\r\n  <div style=\"margin: 0px auto;width: 690px;\">\r\n    <div class=\"mainContent\">\r\n      <h1>验证码</h1>\r\n      <p>请在验证页面输入此验证码</p>\r\n      <p><b>${code}</b></p>\r\n      <p>验证码将于此电子邮件发出 5 分钟后过期。</p>\r\n      <p>如果你未曾提出此请求，可以忽略这封电子邮件。</p>\r\n    </div>\r\n\r\n  </div>\r\n</div>\r\n</body>\r\n</html>', 'freemarker', 0, NOW(), NOW());

INSERT INTO `t_role` VALUES (1,1, '管理员', NULL, '', NOW(), NOW());
INSERT INTO `t_role_employee` VALUES (1,1, 1, 1, NOW(), NOW());

TRUNCATE TABLE `t_menu`;
ALTER TABLE `t_menu` AUTO_INCREMENT = 1;
INSERT INTO `t_menu` (
  `menu_id`, `tenant_id`, `menu_name`, `menu_type`, `parent_id`, `sort`,
  `path`, `component`, `perms_type`, `api_perms`, `web_perms`,
  `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`,
  `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`,
  `create_time`, `update_user_id`, `update_time`
) VALUES
-- 第一部分：核心菜单（ID 1-10）
(1, 1, '系统设置', 1, 0, 6, '/setting', NULL, NULL, NULL, NULL, 'SettingOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(2, 1, '菜单管理', 2, 1, 1, '/menu/list', '/system/menu/menu-list.vue', NULL, NULL, NULL, 'CopyOutlined', NULL, 0, NULL, 1, 1, 0, 0, 2, NOW(), 1, NOW()),
(3, 1, '组织架构', 1, 0, 3, '/organization', NULL, NULL, NULL, NULL, 'UserSwitchOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(4, 1, '员工管理', 2, 3, 3, '/organization/employee', '/system/employee/index.vue', NULL, NULL, NULL, 'AuditOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(5, 1, '角色管理', 2, 3, 4, '/organization/role', '/system/role/index.vue', NULL, NULL, NULL, 'SlidersOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(6, 1, '商品管理', 1, 0, 3, '/goods', NULL, NULL, NULL, NULL, 'BarcodeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(7, 1, '商品管理', 2, 6, 1, '/erp/goods/list', '/business/erp/goods/goods-list.vue', NULL, NULL, NULL, 'AliwangwangOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW()),
(8, 1, '商品分类', 2, 6, 2, '/erp/catalog/goods', '/business/erp/catalog/goods-catalog.vue', NULL, NULL, NULL, 'ApartmentOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW()),
(9, 1, '自定义分组', 2, 6, 3, '/erp/catalog/custom', '/business/erp/catalog/custom-catalog.vue', NULL, NULL, NULL, 'AppstoreAddOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(10, 1, '参数配置', 2, 1, 3, '/config/config-list', '/support/config/config-list.vue', NULL, NULL, NULL, 'AntDesignOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 第二部分：功能按钮（关联上面的菜单ID）
(11, 1, '删除', 3, 2, NULL, NULL, NULL, 1, 'system:menu:batchDelete', 'system:menu:batchDelete', NULL, 2, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(12, 1, '添加部门', 3, 4, 1, NULL, NULL, 1, 'system:department:add', 'system:department:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(13, 1, '修改部门', 3, 4, 2, NULL, NULL, 1, 'system:department:update', 'system:department:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(14, 1, '删除部门', 3, 4, 3, NULL, NULL, 1, 'system:department:delete', 'system:department:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(15, 1, '添加员工', 3, 4, NULL, NULL, NULL, 1, 'system:employee:add', 'system:employee:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(16, 1, '编辑员工', 3, 4, NULL, NULL, NULL, 1, 'system:employee:update', 'system:employee:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(17, 1, '禁用启用员工', 3, 4, NULL, NULL, NULL, 1, 'system:employee:disabled', 'system:employee:disabled', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(18, 1, '调整员工部门', 3, 4, NULL, NULL, NULL, 1, 'system:employee:department:update', 'system:employee:department:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(19, 1, '重置密码', 3, 4, NULL, NULL, NULL, 1, 'system:employee:password:reset', 'system:employee:password:reset', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(20, 1, '删除员工', 3, 4, NULL, NULL, NULL, 1, 'system:employee:delete', 'system:employee:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 角色管理相关按钮（关联ID 5）
(21, 1, '添加角色', 3, 5, NULL, NULL, NULL, 1, 'system:role:add', 'system:role:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(22, 1, '删除角色', 3, 5, NULL, NULL, NULL, 1, 'system:role:delete', 'system:role:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(23, 1, '编辑角色', 3, 5, NULL, NULL, NULL, 1, 'system:role:update', 'system:role:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(24, 1, '更新数据范围', 3, 5, NULL, NULL, NULL, 1, 'system:role:dataScope:update', 'system:role:dataScope:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(25, 1, '批量移除员工', 3, 5, NULL, NULL, NULL, 1, 'system:role:employee:batch:delete', 'system:role:employee:batch:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(26, 1, '移除员工', 3, 5, NULL, NULL, NULL, 1, 'system:role:employee:delete', 'system:role:employee:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(27, 1, '添加员工', 3, 5, NULL, NULL, NULL, 1, 'system:role:employee:add', 'system:role:employee:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(28, 1, '修改权限', 3, 5, NULL, NULL, NULL, 1, 'system:role:menu:update', 'system:role:menu:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 菜单管理相关按钮（关联ID 2）
(29, 1, '添加', 3, 2, NULL, NULL, NULL, 1, 'system:menu:add', 'system:menu:add', NULL, 2, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(30, 1, '编辑', 3, 2, NULL, NULL, NULL, 1, 'system:menu:update', 'system:menu:update', NULL, 2, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 数据字典（关联ID 1）
(31, 1, '数据字典', 2, 1, 4, '/setting/dict', '/support/dict/index.vue', NULL, NULL, NULL, 'BarcodeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(32, 1, '查询', 3, 31, NULL, NULL, NULL, 1, 'support:dict:query', 'support:dict:query', NULL, 31, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(33, 1, '添加', 3, 31, NULL, NULL, NULL, 1, 'support:dict:add', 'support:dict:add', NULL, 31, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(34, 1, '更新', 3, 31, NULL, NULL, NULL, 1, 'support:dict:update', 'support:dict:update', NULL, 31, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(35, 1, '删除', 3, 31, NULL, NULL, NULL, 1, 'support:dict:delete', 'support:dict:delete', NULL, 31, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(36, 1, '启用/禁用', 3, 31, NULL, NULL, NULL, 1, 'support:dict:updateDisabled', 'support:dict:updateDisabled', NULL, 31, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(37, 1, '查询字典数据', 3, 31, NULL, NULL, NULL, 1, 'support:dictData:query', 'support:dictData:query', NULL, 31, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(38, 1, '添加字典数据', 3, 31, NULL, NULL, NULL, 1, 'support:dictData:add', 'support:dictData:add', NULL, 31, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(39, 1, '更新字典数据', 3, 31, NULL, NULL, NULL, 1, 'support:dictData:update', 'support:dictData:update', NULL, 31, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(40, 1, '删除字典数据', 3, 31, NULL, NULL, NULL, 1, 'support:dictData:delete', 'support:dictData:delete', NULL, 31, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(41, 1, '启用/禁用字典数据', 3, 31, NULL, NULL, NULL, 1, 'support:dictData:updateDisabled', 'support:dictData:updateDisabled', NULL, 31, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 参数配置按钮（关联ID 10）
(42, 1, '新建', 3, 10, NULL, NULL, NULL, 1, 'support:config:add', 'support:config:add', NULL, 10, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(43, 1, '编辑', 3, 10, NULL, NULL, NULL, 1, 'support:config:update', 'support:config:update', NULL, 10, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(44, 1, '查询', 3, 10, NULL, NULL, NULL, 1, 'support:config:query', 'support:config:query', NULL, 10, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 商品管理按钮（关联ID 7）
(45, 1, '查询', 3, 7, NULL, NULL, NULL, 1, 'goods:query', 'goods:query', NULL, 7, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(46, 1, '新建', 3, 7, NULL, NULL, NULL, 1, 'goods:add', 'goods:add', NULL, 7, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(47, 1, '批量删除', 3, 7, NULL, NULL, NULL, 1, 'goods:batchDelete', 'goods:batchDelete', NULL, 7, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(48, 1, '删除', 3, 7, NULL, NULL, NULL, 1, 'goods:delete', 'goods:delete', NULL, 7, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(49, 1, '修改', 3, 7, NULL, NULL, NULL, 1, 'goods:update', 'goods:update', NULL, 7, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(50, 1, '导出', 3, 7, NULL, NULL, NULL, 1, 'goods:exportGoods', 'goods:exportGoods', NULL, 7, 0, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW()),
(51, 1, '导入', 3, 7, 3, NULL, NULL, 1, 'goods:importGoods', 'goods:importGoods', NULL, 7, 0, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 商品分类按钮（关联ID 8）
(52, 1, '新建', 3, 8, NULL, NULL, NULL, 1, 'category:add', 'category:add', NULL, 8, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(53, 1, '查询', 3, 8, NULL, NULL, NULL, 1, 'category:tree', 'category:tree', NULL, 8, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(54, 1, '编辑', 3, 8, NULL, NULL, NULL, 1, 'category:update', 'category:update', NULL, 8, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(55, 1, '删除', 3, 8, NULL, NULL, NULL, 1, 'category:delete', 'category:delete', NULL, 8, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 自定义分组按钮（关联ID 9）
(56, 1, '新建', 3, 9, NULL, NULL, NULL, 1, 'category:add', 'custom:category:add', NULL, 8, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(57, 1, '查询', 3, 9, NULL, NULL, NULL, 1, 'category:tree', 'custom:category:tree', NULL, 8, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(58, 1, '编辑', 3, 9, NULL, NULL, NULL, 1, 'category:update', 'custom:category:update', NULL, 8, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(59, 1, '删除', 3, 9, NULL, NULL, NULL, 1, 'category:delete', 'custom:category:delete', NULL, 8, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 监控服务
(60, 1, '监控服务', 1, 0, 100, '/monitor', NULL, NULL, NULL, NULL, 'BarChartOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(61, 1, '心跳监控', 2, 60, 1, '/support/heart-beat/heart-beat-list', '/support/heart-beat/heart-beat-list.vue', 1, NULL, NULL, 'FallOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(62, 1, '数据库监控', 2, 60, 4, '/support/druid/index', NULL, NULL, NULL, NULL, 'ConsoleSqlOutlined', NULL, 1, 'http://localhost:1024/druid', 1, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 运维工具
(63, 1, '运维工具', 1, 0, 200, NULL, NULL, NULL, NULL, NULL, 'NodeCollapseOutlined', NULL, 0, NULL, 0, 1, 0, 1, 1, NOW(), 1, NOW()),
(64, 1, 'Reload', 2, 1, 12, '/hook', '/support/reload/reload-list.vue', NULL, NULL, NULL, 'ReloadOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(65, 1, '查看结果', 3, 64, NULL, NULL, NULL, 1, 'support:reload:result', 'support:reload:result', NULL, 64, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(66, 1, '查询', 3, 64, NULL, NULL, NULL, 1, 'support:reload:query', 'support:reload:query', NULL, 64, 0, NULL, 1, 1, 1, 0, 1, NOW(), 1, NOW()),

-- 单号管理
(67, 1, '单号管理', 2, 1, 6, '/support/serial-number/serial-number-list', '/support/serial-number/serial-number-list.vue', NULL, NULL, NULL, 'NumberOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(68, 1, '单号生成', 3, 67, NULL, NULL, NULL, 1, 'support:serialNumber:generate', 'support:serialNumber:generate', NULL, 67, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(69, 1, '生成记录', 3, 67, NULL, NULL, NULL, 1, 'support:serialNumber:record', 'support:serialNumber:record', NULL, 67, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 缓存管理
(70, 1, '缓存管理', 2, 1, 11, '/support/cache/cache-list', '/support/cache/cache-list.vue', NULL, NULL, NULL, 'BorderInnerOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(71, 1, '清除缓存', 3, 70, NULL, NULL, NULL, 1, 'support:cache:delete', 'support:cache:delete', NULL, 70, 0, NULL, 0, 1, 1, 0, 1, NOW(), 1, NOW()),
(72, 1, '获取缓存key', 3, 70, NULL, NULL, NULL, 1, 'support:cache:keys', 'support:cache:keys', NULL, 70, 0, NULL, 0, 1, 1, 0, 1, NOW(), 1, NOW()),

-- 功能Demo
(73, 1, '功能Demo', 1, 0, 1, NULL, NULL, NULL, NULL, NULL, 'BankOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(74, 1, '公告管理', 2, 73, 2, '/oa/notice/notice-list', '/business/oa/notice/notice-list.vue', NULL, NULL, NULL, 'SoundOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW()),
(75, 1, '公告详情', 2, 74, NULL, '/oa/notice/notice-detail', '/business/oa/notice/notice-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, NOW(), 1, NOW()),
(76, 1, '我的通知', 2, 74, NULL, '/oa/notice/notice-employee-list', '/business/oa/notice/notice-employee-list.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, NOW(), 1, NOW()),
(77, 1, '我的通知公告详情', 2, 74, NULL, '/oa/notice/notice-employee-detail', '/business/oa/notice/notice-employee-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, NOW(), 1, NOW()),
(78, 1, '查询', 3, 74, NULL, NULL, NULL, 1, 'oa:notice:query', 'oa:notice:query', NULL, 74, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(79, 1, '新建', 3, 74, NULL, NULL, NULL, 1, 'oa:notice:add', 'oa:notice:add', NULL, 74, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(80, 1, '编辑', 3, 74, NULL, NULL, NULL, 1, 'oa:notice:update', 'oa:notice:update', NULL, 74, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(81, 1, '删除', 3, 74, NULL, NULL, NULL, 1, 'oa:notice:delete', 'oa:notice:delete', NULL, 74, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 企业管理
(82, 1, '企业管理', 2, 73, 1, '/oa/enterprise/enterprise-list', '/business/oa/enterprise/enterprise-list.vue', NULL, NULL, NULL, 'ShopOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(83, 1, '企业详情', 2, 73, NULL, '/oa/enterprise/enterprise-detail', '/business/oa/enterprise/enterprise-detail.vue', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, 0, 0, 0, 1, NOW(), 1, NOW()),
(84, 1, '查询', 3, 82, NULL, NULL, NULL, 1, 'oa:enterprise:query', 'oa:enterprise:query', NULL, 82, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(85, 1, '新建', 3, 82, NULL, NULL, NULL, 1, 'oa:enterprise:add', 'oa:enterprise:add', NULL, 82, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(86, 1, '编辑', 3, 82, NULL, NULL, NULL, 1, 'oa:enterprise:update', 'oa:enterprise:update', NULL, 82, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(87, 1, '删除', 3, 82, NULL, NULL, NULL, 1, 'oa:enterprise:delete', 'oa:enterprise:delete', NULL, 82, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(88, 1, '查看详情', 3, 83, NULL, NULL, NULL, 1, 'oa:enterprise:detail', 'oa:enterprise:detail', NULL, 83, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(89, 1, '查询企业员工', 3, 83, NULL, NULL, NULL, 1, 'oa:enterprise:queryEmployee', 'oa:enterprise:queryEmployee', NULL, 83, 0, NULL, 0, 1, 0, 0, 75, NOW(), 75, NOW()),
(90, 1, '查询银行信息', 3, 83, NULL, NULL, NULL, 1, 'oa:bank:query', 'oa:bank:query', NULL, 83, 0, NULL, 0, 1, 0, 0, 75, NOW(), 75, NOW()),
(91, 1, '查询发票信息', 3, 83, NULL, NULL, NULL, 1, 'oa:invoice:query', 'oa:invoice:query', NULL, 83, 0, NULL, 0, 1, 0, 0, 75, NOW(), 75, NOW()),
(92, 1, '添加企业员工', 3, 83, NULL, NULL, NULL, 1, 'oa:enterprise:addEmployee', 'oa:enterprise:addEmployee', NULL, 83, 0, NULL, 0, 1, 0, 0, 75, NOW(), 75, NOW()),
(93, 1, '删除企业员工', 3, 83, NULL, NULL, NULL, 1, 'oa:enterprise:deleteEmployee', 'oa:enterprise:deleteEmployee', NULL, 83, 0, NULL, 0, 1, 0, 0, 75, NOW(), 75, NOW()),
(94, 1, '添加银行信息', 3, 83, NULL, NULL, NULL, 1, 'oa:bank:add', 'oa:bank:add', NULL, 83, 0, NULL, 0, 1, 0, 0, 75, NOW(), 75, NOW()),
(95, 1, '更新银行信息', 3, 83, NULL, NULL, NULL, 1, 'oa:bank:update', 'oa:bank:update', NULL, 83, 0, NULL, 0, 1, 0, 0, 75, NOW(), 75, NOW()),
(96, 1, '删除银行信息', 3, 83, NULL, NULL, NULL, 1, 'oa:bank:delete', 'oa:bank:delete', NULL, 83, 0, NULL, 0, 1, 0, 0, 75, NOW(), 75, NOW()),
(97, 1, '添加发票信息', 3, 83, NULL, NULL, NULL, 1, 'oa:invoice:add', 'oa:invoice:add', NULL, 83, 0, NULL, 0, 1, 0, 0, 75, NOW(), 75, NOW()),
(98, 1, '更新发票信息', 3, 83, NULL, NULL, NULL, 1, 'oa:invoice:update', 'oa:invoice:update', NULL, 83, 0, NULL, 0, 1, 0, 0, 75, NOW(), 75, NOW()),
(99, 1, '删除发票信息', 3, 83, NULL, NULL, NULL, 1, 'oa:invoice:delete', 'oa:invoice:delete', NULL, 83, 0, NULL, 0, 1, 0, 0, 75, NOW(), 75, NOW()),

-- 文档中心
(100, 1, '文档中心', 1, 0, 4, NULL, NULL, 1, NULL, NULL, 'FileSearchOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW()),
(101, 1, '帮助文档', 2, 100, 1, '/help-doc/help-doc-manage-list', '/support/help-doc/management/help-doc-manage-list.vue', NULL, NULL, NULL, 'FolderViewOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(102, 1, '查询', 3, 101, 11, NULL, NULL, 1, 'support:helpDoc:query', 'support:helpDoc:query', NULL, 101, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(103, 1, '新建', 3, 101, 12, NULL, NULL, 1, 'support:helpDoc:add', 'support:helpDoc:add', NULL, 101, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(104, 1, '新建目录', 3, 101, 1, NULL, NULL, 1, 'support:helpDocCatalog:addCategory', 'support:helpDocCatalog:addCategory', NULL, 101, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(105, 1, '修改目录', 3, 101, 2, NULL, NULL, 1, 'support:helpDocCatalog:update', 'support:helpDocCatalog:update', NULL, 101, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(106, 1, '更新', 3, 101, 13, NULL, NULL, 1, 'support:helpDoc:update', 'support:helpDoc:update', NULL, 101, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(107, 1, '删除', 3, 101, 14, NULL, NULL, 1, 'support:helpDoc:delete', 'support:helpDoc:delete', NULL, 101, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(108, 1, '意见反馈', 2, 100, 2, '/feedback/feedback-list', '/support/feedback/feedback-list.vue', NULL, NULL, NULL, 'CoffeeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(109, 1, '更新日志', 2, 100, 3, '/support/change-log/change-log-list', '/support/change-log/change-log-list.vue', NULL, NULL, NULL, 'HeartOutlined', NULL, 0, NULL, 0, 1, 0, 0, 44, NOW(), 1, NOW()),
(110, 1, '查询', 3, 109, NULL, NULL, NULL, 1, '', 'support:changeLog:query', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(111, 1, '新建', 3, 109, NULL, NULL, NULL, 1, 'support:changeLog:add', 'support:changeLog:add', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(112, 1, '批量删除', 3, 109, NULL, NULL, NULL, 1, 'support:changeLog:batchDelete', 'support:changeLog:batchDelete', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(113, 1, '删除', 3, 109, NULL, NULL, NULL, 1, 'support:changeLog:delete', 'support:changeLog:delete', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(114, 1, '更新', 3, 109, NULL, NULL, NULL, 1, 'support:changeLog:update', 'support:changeLog:update', NULL, 109, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(115, 1, 'knife4j文档', 2, 100, 4, '/knife4j', NULL, 1, NULL, NULL, 'FileWordOutlined', NULL, 1, 'http://localhost:1024/doc.html', 1, 1, 0, 0, 1, NOW(), 1, NOW()),
(116, 1, 'swagger文档', 2, 100, 5, '/swagger', 'http://localhost:1024/swagger-ui/index.html', 1, NULL, NULL, 'ApiOutlined', NULL, 1, 'http://localhost:1024/swagger-ui/index.html', 1, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 网络安全
(117, 1, '网络安全', 1, 0, 5, NULL, NULL, 1, NULL, NULL, 'SafetyCertificateOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW()),
(118, 1, '三级等保设置', 2, 117, 1, '/support/level3protect/level3-protect-config-index', '/support/level3protect/level3-protect-config-index.vue', 1, NULL, NULL, 'SafetyOutlined', NULL, 0, NULL, 1, 1, 0, 0, 44, NOW(), 44, NOW()),
(119, 1, '敏感数据脱敏', 2, 117, 3, '/support/level3protect/data-masking-list', '/support/level3protect/data-masking-list.vue', 1, NULL, NULL, 'FileProtectOutlined', NULL, 0, NULL, 1, 1, 0, 0, 44, NOW(), 44, NOW()),
(120, 1, '接口加解密', 2, 117, 2, '/support/api-encrypt', '/support/api-encrypt/api-encrypt-index.vue', 1, NULL, NULL, 'CodepenCircleOutlined', NULL, 0, NULL, 1, 1, 0, 0, 44, NOW(), 44, NOW()),
(121, 1, '登录失败锁定', 2, 117, 4, '/support/login-fail', '/support/login-fail/login-fail-list.vue', 1, NULL, NULL, 'LockOutlined', NULL, 0, NULL, 1, 1, 0, 0, 44, NOW(), 44, NOW()),
(122, 1, '登录登出记录', 2, 117, 5, '/support/login-log/login-log-list', '/support/login-log/login-log-list.vue', NULL, NULL, NULL, 'LoginOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 44, NOW()),
(123, 1, '查询', 3, 122, NULL, NULL, NULL, 1, 'support:loginLog:query', 'support:loginLog:query', NULL, 122, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(124, 1, '用户操作记录', 2, 117, 6, '/support/operate-log/operate-log-list', '/support/operate-log/operate-log-list.vue', NULL, NULL, NULL, 'VideoCameraOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 44, NOW()),
(125, 1, '查询', 3, 124, NULL, NULL, NULL, 1, 'support:operateLog:query', 'support:operateLog:query', NULL, 124, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(126, 1, '详情', 3, 124, NULL, NULL, NULL, 1, 'support:operateLog:detail', 'support:operateLog:detail', NULL, 124, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 其他功能
(127, 1, '组件演示', 2, 84, NULL, '/demonstration/index', '/support/demonstration/index.vue', NULL, NULL, NULL, 'ClearOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), NULL, NOW()),
(128, 1, '代码生成', 2, 0, 600, '/support/code-generator', '/support/code-generator/code-generator-list.vue', NULL, NULL, NULL, 'CoffeeOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(129, 1, '文件管理', 2, 1, 20, '/support/file/file-list', '/support/file/file-list.vue', NULL, NULL, NULL, 'FolderOpenOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(130, 1, '查询', 3, 129, NULL, NULL, NULL, 1, 'support:file:query', 'support:file:query', NULL, 129, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(131, 1, '定时任务', 2, 1, 25, '/job/list', '/support/job/job-list.vue', 1, NULL, NULL, 'AppstoreOutlined', NULL, 0, NULL, 1, 1, 0, 0, 2, NOW(), 2, NOW()),
(132, 1, '查询任务', 3, 131, NULL, NULL, NULL, 1, 'support:job:query', 'support:job:query', NULL, 131, 0, NULL, 1, 1, 0, 0, 2, NOW(), 2, NOW()),
(133, 1, '更新任务', 3, 131, NULL, NULL, NULL, 1, 'support:job:update', 'support:job:update', NULL, 131, 0, NULL, 1, 1, 0, 0, 2, NOW(), 2, NOW()),
(134, 1, '执行任务', 3, 131, NULL, NULL, NULL, 1, 'support:job:execute', 'support:job:execute', NULL, 131, 0, NULL, 1, 1, 0, 0, 2, NOW(), 2, NOW()),
(135, 1, '查询记录', 3, 131, NULL, NULL, NULL, 1, 'support:job:log:query', 'support:job:log:query', NULL, 131, 0, NULL, 1, 1, 0, 0, 2, NOW(), 2, NOW()),
(136, 1, '部门管理', 2, 3, 1, '/organization/department', '/system/department/department-list.vue', 1, NULL, NULL, 'ApartmentOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(137, 1, '职务管理', 2, 3, 2, '/organization/position', '/system/position/position-list.vue', 1, NULL, NULL, 'ApartmentOutlined', NULL, 0, NULL, 1, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 连接器管理（新增）
(138, 1, '连接器管理', 1, 0, 100, '/connector', NULL, NULL, NULL, NULL, 'LinkOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(139, 1, '路由配置', 2, 138, 1, '/connector/route-list', '/business/connector/route-list.vue', NULL, NULL, NULL, 'NodeIndexOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(140, 1, '请求日志', 2, 138, 2, '/connector/route-log-list', '/business/connector/route-log-list.vue', NULL, NULL, NULL, 'FileTextOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(141, 1, '路由测试', 2, 138, 3, '/connector/route-test', '/business/connector/route-test.vue', NULL, NULL, NULL, 'ExperimentOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(142, 1, '密钥管理', 2, 138, 4, '/connector/secret-key-list', '/business/connector/secret-key-list.vue', NULL, NULL, NULL, 'KeyOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(143, 1, '查询', 3, 142, 1, NULL, NULL, 1, 'connector:secret-key:query', 'connector:secret-key:query', NULL, 142, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(144, 1, '添加', 3, 142, 2, NULL, NULL, 1, 'connector:secret-key:add', 'connector:secret-key:add', NULL, 142, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(145, 1, '更新', 3, 142, 3, NULL, NULL, 1, 'connector:secret-key:update', 'connector:secret-key:update', NULL, 142, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(146, 1, '删除', 3, 142, 4, NULL, NULL, 1, 'connector:secret-key:delete', 'connector:secret-key:delete', NULL, 142, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),

-- 消息管理
(147, 1, '消息管理', 2, 1, 30, '/message', '/support/message/message-list.vue', 1, NULL, NULL, 'MailOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 租户管理
INSERT INTO `t_menu` (`menu_id`, `tenant_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (148, 1, '租户管理', 2, 1, 40, '/system/tenant', '/system/tenant/tenant-list.vue', NULL, NULL, NULL, 'TeamOutlined', NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- Buttons
-- Query
INSERT INTO `t_menu` (`menu_id`, `tenant_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (149, 1, '查询', 3, 148, 1, NULL, NULL, 1, 'system:tenant:query', 'system:tenant:query', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- Add
INSERT INTO `t_menu` (`menu_id`, `tenant_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (150, 1, '添加', 3, 148, 2, NULL, NULL, 1, 'system:tenant:add', 'system:tenant:add', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- Update
INSERT INTO `t_menu` (`menu_id`, `tenant_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (151, 1, '更新', 3, 148, 3, NULL, NULL, 1, 'system:tenant:update', 'system:tenant:update', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- Delete
INSERT INTO `t_menu` (`menu_id`, `tenant_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES (152, 1, '删除', 3, 148, 4, NULL, NULL, 1, 'system:tenant:delete', 'system:tenant:delete', NULL, NULL, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());


-- 批量插入 t_role_menu 数据（对应1-146号菜单，适配表结构）
INSERT INTO `t_role_menu` (
  `tenant_id`, `role_id`, `menu_id`
) VALUES
(1, 1, 1),
(1, 1, 2),
(1, 1, 3),
(1, 1, 4),
(1, 1, 5),
(1, 1, 6),
(1, 1, 7),
(1, 1, 8),
(1, 1, 9),
(1, 1, 10),
(1, 1, 11),
(1, 1, 12),
(1, 1, 13),
(1, 1, 14),
(1, 1, 15),
(1, 1, 16),
(1, 1, 17),
(1, 1, 18),
(1, 1, 19),
(1, 1, 20),
(1, 1, 21),
(1, 1, 22),
(1, 1, 23),
(1, 1, 24),
(1, 1, 25),
(1, 1, 26),
(1, 1, 27),
(1, 1, 28),
(1, 1, 29),
(1, 1, 30),
(1, 1, 31),
(1, 1, 32),
(1, 1, 33),
(1, 1, 34),
(1, 1, 35),
(1, 1, 36),
(1, 1, 37),
(1, 1, 38),
(1, 1, 39),
(1, 1, 40),
(1, 1, 41),
(1, 1, 42),
(1, 1, 43),
(1, 1, 44),
(1, 1, 45),
(1, 1, 46),
(1, 1, 47),
(1, 1, 48),
(1, 1, 49),
(1, 1, 50),
(1, 1, 51),
(1, 1, 52),
(1, 1, 53),
(1, 1, 54),
(1, 1, 55),
(1, 1, 56),
(1, 1, 57),
(1, 1, 58),
(1, 1, 59),
(1, 1, 60),
(1, 1, 61),
(1, 1, 62),
(1, 1, 63),
(1, 1, 64),
(1, 1, 65),
(1, 1, 66),
(1, 1, 67),
(1, 1, 68),
(1, 1, 69),
(1, 1, 70),
(1, 1, 71),
(1, 1, 72),
(1, 1, 73),
(1, 1, 74),
(1, 1, 75),
(1, 1, 76),
(1, 1, 77),
(1, 1, 78),
(1, 1, 79),
(1, 1, 80),
(1, 1, 81),
(1, 1, 82),
(1, 1, 83),
(1, 1, 84),
(1, 1, 85),
(1, 1, 86),
(1, 1, 87),
(1, 1, 88),
(1, 1, 89),
(1, 1, 90),
(1, 1, 91),
(1, 1, 92),
(1, 1, 93),
(1, 1, 94),
(1, 1, 95),
(1, 1, 96),
(1, 1, 97),
(1, 1, 98),
(1, 1, 99),
(1, 1, 100),
(1, 1, 101),
(1, 1, 102),
(1, 1, 103),
(1, 1, 104),
(1, 1, 105),
(1, 1, 106),
(1, 1, 107),
(1, 1, 108),
(1, 1, 109),
(1, 1, 110),
(1, 1, 111),
(1, 1, 112),
(1, 1, 113),
(1, 1, 114),
(1, 1, 115),
(1, 1, 116),
(1, 1, 117),
(1, 1, 118),
(1, 1, 119),
(1, 1, 120),
(1, 1, 121),
(1, 1, 122),
(1, 1, 123),
(1, 1, 124),
(1, 1, 125),
(1, 1, 126),
(1, 1, 127),
(1, 1, 128),
(1, 1, 129),
(1, 1, 130),
(1, 1, 131),
(1, 1, 132),
(1, 1, 133),
(1, 1, 134),
(1, 1, 135),
(1, 1, 136),
(1, 1, 137),
(1, 1, 138),
(1, 1, 139),
(1, 1, 140),
(1, 1, 141),
(1, 1, 142),
(1, 1, 143),
(1, 1, 144),
(1, 1, 145),
(1, 1, 146),
(1, 1, 147);
-- Assign to Role 1 (Admin)
INSERT INTO `t_role_menu` (`tenant_id`, `role_id`, `menu_id`, `create_time`, `update_time`) VALUES (1, 1, 148, NOW(), NOW());
INSERT INTO `t_role_menu` (`tenant_id`, `role_id`, `menu_id`, `create_time`, `update_time`) VALUES (1, 1, 149, NOW(), NOW());
INSERT INTO `t_role_menu` (`tenant_id`, `role_id`, `menu_id`, `create_time`, `update_time`) VALUES (1, 1, 150, NOW(), NOW());
INSERT INTO `t_role_menu` (`tenant_id`, `role_id`, `menu_id`, `create_time`, `update_time`) VALUES (1, 1, 151, NOW(), NOW());
INSERT INTO `t_role_menu` (`tenant_id`, `role_id`, `menu_id`, `create_time`, `update_time`) VALUES (1, 1, 152, NOW(), NOW());

-- 路由配置按钮 (关联ID 139)
INSERT INTO `t_menu` (`menu_id`, `tenant_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES
(153, 1, '查询', 3, 139, 1, NULL, NULL, 1, 'connector:route:query', 'connector:route:query', NULL, 139, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(154, 1, '添加', 3, 139, 2, NULL, NULL, 1, 'connector:route:add', 'connector:route:add', NULL, 139, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(155, 1, '更新', 3, 139, 3, NULL, NULL, 1, 'connector:route:update', 'connector:route:update', NULL, 139, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(156, 1, '删除', 3, 139, 4, NULL, NULL, 1, 'connector:route:delete', 'connector:route:delete', NULL, 139, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(157, 1, '批量删除', 3, 139, 5, NULL, NULL, 1, 'connector:route:batchDelete', 'connector:route:batchDelete', NULL, 139, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(158, 1, '导入', 3, 139, 6, NULL, NULL, 1, 'connector:route:import', 'connector:route:import', NULL, 139, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(159, 1, '导出', 3, 139, 7, NULL, NULL, 1, 'connector:route:export', 'connector:route:export', NULL, 139, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 请求日志按钮 (关联ID 140)
INSERT INTO `t_menu` (`menu_id`, `tenant_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES
(160, 1, '查询', 3, 140, 1, NULL, NULL, 1, 'connector:routeLog:query', 'connector:routeLog:query', NULL, 140, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(161, 1, '导出', 3, 140, 2, NULL, NULL, 1, 'connector:routeLog:export', 'connector:routeLog:export', NULL, 140, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 密钥管理新增按钮 (关联ID 142)
INSERT INTO `t_menu` (`menu_id`, `tenant_id`, `menu_name`, `menu_type`, `parent_id`, `sort`, `path`, `component`, `perms_type`, `api_perms`, `web_perms`, `icon`, `context_menu_id`, `frame_flag`, `frame_url`, `cache_flag`, `visible_flag`, `disabled_flag`, `deleted_flag`, `create_user_id`, `create_time`, `update_user_id`, `update_time`)
VALUES
(162, 1, '批量删除', 3, 142, 5, NULL, NULL, 1, 'connector:secret-key:batchDelete', 'connector:secret-key:batchDelete', NULL, 142, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(163, 1, '导入', 3, 142, 6, NULL, NULL, 1, 'connector:secret-key:import', 'connector:secret-key:import', NULL, 142, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW()),
(164, 1, '导出', 3, 142, 7, NULL, NULL, 1, 'connector:secret-key:export', 'connector:secret-key:export', NULL, 142, 0, NULL, 0, 1, 0, 0, 1, NOW(), 1, NOW());

-- 授权给管理员角色 (ID 1)
INSERT INTO `t_role_menu` (`tenant_id`, `role_id`, `menu_id`, `create_time`, `update_time`) VALUES 
(1, 1, 153, NOW(), NOW()),
(1, 1, 154, NOW(), NOW()),
(1, 1, 155, NOW(), NOW()),
(1, 1, 156, NOW(), NOW()),
(1, 1, 157, NOW(), NOW()),
(1, 1, 158, NOW(), NOW()),
(1, 1, 159, NOW(), NOW()),
(1, 1, 160, NOW(), NOW()),
(1, 1, 161, NOW(), NOW()),
(1, 1, 162, NOW(), NOW()),
(1, 1, 163, NOW(), NOW()),
(1, 1, 164, NOW(), NOW());

INSERT INTO `t_config` VALUES (1,1, '万能密码', 'super_password', '1024ok', '一路春光啊一路荆棘呀惊鸿一般短暂如夏花一样绚烂这是一个不能停留太久的世界，一路春光啊一路荆棘呀惊鸿一般短暂如夏花一样绚烂这是一个不能停留太久的世界啊', NOW(), NOW());
INSERT INTO `t_config` VALUES (2,1, '三级等保', 'level3_protect_config', '{\n	\"fileDetectFlag\":true,\n	\"loginActiveTimeoutMinutes\":30,\n	\"loginFailLockMinutes\":30,\n	\"loginFailMaxTimes\":3,\n	\"maxUploadFileSizeMb\":30,\n	\"passwordComplexityEnabled\":true,\n	\"regularChangePasswordMonths\":3,\n	\"regularChangePasswordNotAllowRepeatTimes\":3,\n	\"twoFactorLoginEnabled\":false\n}', 'Sample2 update', NOW(), NOW());
