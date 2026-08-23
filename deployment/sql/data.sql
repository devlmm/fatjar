-- =====================================================================
-- fatjar 企业云原生大单体业务管理系统 - 初始化数据脚本
-- 数据库：单实例多库（auth/fico/scm/mes/hrm/crm/pm/bi/oa）
-- 说明：系统启动必需的基础数据（超管、角色、菜单、关联关系及各模块示例数据）
-- 密码 admin123 的 BCrypt 加密值（成本因子 10）
-- 注意：所有表使用 db.table 全限定名，跨库插入无需切换 USE
-- =====================================================================

-- =====================================================================
-- 一、认证模块数据（auth 数据库）
-- =====================================================================

-- 1. 超级管理员
INSERT INTO `auth`.`sys_user` (`id`, `username`, `nickname`, `password`, `phone`, `email`, `status`, `tenant_id`, `create_by`, `update_by`, `deleted`)
VALUES (1, 'admin', '超级管理员', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq8Kk3pVLdFq4SnYHnA6X9dP6HqJgK', '13800000000', 'admin@fatjar.com', 0, NULL, 'system', 'system', 0);

-- 2. admin 角色
INSERT INTO `auth`.`sys_role` (`id`, `role_code`, `role_name`, `status`, `remark`, `create_by`, `update_by`, `deleted`)
VALUES (1, 'admin', '系统管理员', 0, '拥有系统全部权限', 'system', 'system', 0);

-- 3. 系统菜单（含 8 大业务模块的完整菜单树）
-- 3.1 顶级目录
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (100, 0, '系统管理', '/sys', 'Layout', 'Setting', 0, NULL, 1, 0, 'system', 'system', 0);
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (200, 0, '财务会计', '/fico', 'Layout', 'Money', 0, NULL, 2, 0, 'system', 'system', 0);
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (300, 0, '供应链管理', '/scm', 'Layout', 'Goods', 0, NULL, 3, 0, 'system', 'system', 0);
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (400, 0, '制造执行', '/mes', 'Layout', 'Tools', 0, NULL, 4, 0, 'system', 'system', 0);
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (500, 0, '人力资源', '/hrm', 'Layout', 'UserFilled', 0, NULL, 5, 0, 'system', 'system', 0);
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (600, 0, '客户关系', '/crm', 'Layout', 'Avatar', 0, NULL, 6, 0, 'system', 'system', 0);
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (700, 0, '项目管理', '/pm', 'Layout', 'Briefcase', 0, NULL, 7, 0, 'system', 'system', 0);
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (800, 0, '商业智能', '/bi', 'Layout', 'DataAnalysis', 0, NULL, 8, 0, 'system', 'system', 0);
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (900, 0, '办公自动化', '/oa', 'Layout', 'Document', 0, NULL, 9, 0, 'system', 'system', 0);

-- 3.2 系统管理子菜单
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (101, 100, '用户管理', 'sys/user', 'sys/user', 'User', 1, 'sys:user:list', 1, 0, 'system', 'system', 0);
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (102, 100, '角色管理', 'sys/role', 'sys/role', 'UserFilled', 1, 'sys:role:list', 2, 0, 'system', 'system', 0);
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (103, 100, '菜单管理', 'sys/menu', 'sys/menu', 'Menu', 1, 'sys:menu:list', 3, 0, 'system', 'system', 0);

-- 3.3 FICO 财务会计子菜单
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (201, 200, '凭证管理', 'fico/voucher', 'fico/voucher', 'Tickets', 1, 'fico:voucher:list', 1, 0, 'system', 'system', 0);

-- 3.4 SCM 供应链管理子菜单
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (301, 300, '采购订单', 'scm/purchase-order', 'scm/purchaseOrder', 'ShoppingCart', 1, 'scm:order:list', 1, 0, 'system', 'system', 0);

-- 3.5 MES 制造执行子菜单
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (401, 400, '工单管理', 'mes/work-order', 'mes/workOrder', 'Tools', 1, 'mes:workorder:list', 1, 0, 'system', 'system', 0);

-- 3.6 HRM 人力资源子菜单
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (501, 500, '员工管理', 'hrm/employee', 'hrm/employee', 'User', 1, 'hrm:employee:list', 1, 0, 'system', 'system', 0);

-- 3.7 CRM 客户关系子菜单
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (601, 600, '客户管理', 'crm/customer', 'crm/customer', 'Avatar', 1, 'crm:customer:list', 1, 0, 'system', 'system', 0);

-- 3.8 PM 项目管理子菜单
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (701, 700, '项目管理', 'pm/project', 'pm/project', 'Briefcase', 1, 'pm:project:list', 1, 0, 'system', 'system', 0);

-- 3.9 BI 商业智能子菜单
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (801, 800, '报表管理', 'bi/report', 'bi/report', 'DataAnalysis', 1, 'bi:report:list', 1, 0, 'system', 'system', 0);

-- 3.10 OA 办公自动化子菜单
INSERT INTO `auth`.`sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `type`, `permission`, `sort`, `status`, `create_by`, `update_by`, `deleted`) VALUES (901, 900, '审批管理', 'oa/approval', 'oa/approval', 'Document', 1, 'oa:approval:list', 1, 0, 'system', 'system', 0);

-- 4. 用户-角色关联
INSERT INTO `auth`.`sys_user_role` (`id`, `user_id`, `role_id`) VALUES (1, 1, 1);

-- 5. 角色-菜单关联（admin 角色关联所有菜单）
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (1, 1, 100);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (2, 1, 101);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (3, 1, 102);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (4, 1, 103);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (5, 1, 200);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (6, 1, 201);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (7, 1, 300);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (8, 1, 301);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (9, 1, 400);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (10, 1, 401);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (11, 1, 500);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (12, 1, 501);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (13, 1, 600);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (14, 1, 601);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (15, 1, 700);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (16, 1, 701);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (17, 1, 800);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (18, 1, 801);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (19, 1, 900);
INSERT INTO `auth`.`sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (20, 1, 901);

-- =====================================================================
-- 二、FICO 财务会计模块示例数据
-- =====================================================================
INSERT INTO `fico`.`voucher` (`id`, `voucher_no`, `title`, `amount`, `direction`, `period`, `status`, `create_by`, `update_by`, `deleted`)
VALUES
  (1001, 'FV-2026-0001', '八月办公租金', 50000.00, 0, '2026-08', 1, 'system', 'system', 0),
  (1002, 'FV-2026-0002', '软件销售收款', 99999.00, 1, '2026-08', 1, 'system', 'system', 0);

-- =====================================================================
-- 三、SCM 供应链管理模块示例数据
-- =====================================================================
INSERT INTO `scm`.`purchase_order` (`id`, `order_no`, `supplier_name`, `total_amount`, `dept_id`, `status`, `create_by`, `update_by`, `deleted`)
VALUES
  (1001, 'PO-2026-0001', '阿里云计算有限公司', 88888.00, 1, 1, 'system', 'system', 0),
  (1002, 'PO-2026-0002', '华为技术有限公司', 166666.00, 1, 0, 'system', 'system', 0);

-- =====================================================================
-- 四、MES 制造执行模块示例数据
-- =====================================================================
INSERT INTO `mes`.`work_order` (`id`, `work_order_no`, `product_name`, `quantity`, `status`, `planned_start`, `planned_end`, `create_by`, `update_by`, `deleted`)
VALUES
  (1001, 'WO-2026-0001', '企业级云原生中间件', 500, 1, '2026-08-01 08:00:00', '2026-08-31 18:00:00', 'system', 'system', 0),
  (1002, 'WO-2026-0002', '高可用数据库集群服务', 200, 0, '2026-09-01 08:00:00', '2026-09-15 18:00:00', 'system', 'system', 0);

-- =====================================================================
-- 五、HRM 人力资源模块示例数据
-- =====================================================================
INSERT INTO `hrm`.`employee` (`id`, `emp_no`, `name`, `dept_id`, `position`, `phone`, `email`, `status`, `create_by`, `update_by`, `deleted`)
VALUES
  (1001, 'EMP-0001', '张三', 1, '技术总监', '13900000001', 'zhangsan@fatjar.com', 0, 'system', 'system', 0),
  (1002, 'EMP-0002', '李四', 2, '产品经理', '13900000002', 'lisi@fatjar.com', 0, 'system', 'system', 0);

-- =====================================================================
-- 六、CRM 客户关系管理模块示例数据
-- =====================================================================
INSERT INTO `crm`.`customer` (`id`, `customer_name`, `contact`, `phone`, `email`, `level`, `status`, `create_by`, `update_by`, `deleted`)
VALUES
  (1001, '北京科技有限公司', '王五', '13700000001', 'wangwu@bjtech.com', 1, 1, 'system', 'system', 0),
  (1002, '上海贸易集团', '赵六', '13700000002', 'zhaoliu@shtrade.com', 2, 1, 'system', 'system', 0);

-- =====================================================================
-- 七、PM 项目管理模块示例数据
-- =====================================================================
INSERT INTO `pm`.`project` (`id`, `project_no`, `project_name`, `manager_id`, `start_date`, `end_date`, `budget`, `status`, `create_by`, `update_by`, `deleted`)
VALUES
  (1001, 'PRJ-2026-001', 'fatjar 脚手架研发', 1001, '2026-01-01', '2026-12-31', 500000.00, 1, 'system', 'system', 0),
  (1002, 'PRJ-2026-002', '企业 ERP 系统升级', 1002, '2026-03-01', '2026-09-30', 300000.00, 1, 'system', 'system', 0);

-- =====================================================================
-- 八、BI 商业智能模块示例数据
-- =====================================================================
INSERT INTO `bi`.`report` (`id`, `report_name`, `report_type`, `data_source`, `status`, `create_by`, `update_by`, `deleted`)
VALUES
  (1001, '月度销售汇总报表', '汇总报表', 'MySQL:scm', 1, 'system', 'system', 0),
  (1002, '客户活跃度分析', '分析报表', 'MySQL:crm', 0, 'system', 'system', 0);

-- =====================================================================
-- 九、OA 办公自动化模块示例数据
-- =====================================================================
INSERT INTO `oa`.`approval` (`id`, `title`, `applicant_id`, `type`, `status`, `content`, `comment`, `create_by`, `update_by`, `deleted`)
VALUES
  (1001, '采购阿里云服务审批', 1, '采购审批', 1, '申请采购阿里云 ECS 实例 10 台', '同意采购', 'system', 'system', 0),
  (1002, '出差申请-上海客户拜访', 1, '出差审批', 0, '拜访上海贸易集团客户', NULL, 'system', 'system', 0);
