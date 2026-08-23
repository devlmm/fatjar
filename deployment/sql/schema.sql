-- =====================================================================
-- fatjar 企业云原生大单体业务管理系统 - 数据库建表脚本
-- 数据库架构：单 MySQL 实例 + 多数据库（数据库即 Schema，业务隔离）
--   fatjar - 连接锚定库（空库，仅用于 JDBC URL 连接）
--   auth   - 认证模块（sys_ 前缀系统表）
--   fico   - 财务会计（FICO）
--   scm    - 供应链管理（SCM）
--   mes    - 制造执行系统（MES）
--   hrm    - 人力资源（HRM）
--   crm    - 客户关系（CRM）
--   pm     - 项目管理（PM）
--   bi     - 商业智能（BI）
--   oa     - 办公自动化（OA）
-- 字符集：utf8mb4 | 存储引擎：InnoDB | 主键：雪花算法（应用层填充）
-- 跨库事务：同一 MySQL 实例 + 同一连接池 -> 本地事务 @Transactional
-- =====================================================================

-- ---------------------------------------------------------------------
-- 创建 10 个数据库
-- ---------------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS fatjar DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS auth   DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS fico   DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS scm    DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS mes    DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS hrm    DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS crm    DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS pm    DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS bi    DEFAULT CHARSET utf8mb4;
CREATE DATABASE IF NOT EXISTS oa    DEFAULT CHARSET utf8mb4;

-- =====================================================================
-- 一、认证模块 auth 数据库（系统管理表）
-- =====================================================================

DROP TABLE IF EXISTS `auth`.`sys_user`;
CREATE TABLE `auth`.`sys_user` (
  `id`          BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
  `username`    VARCHAR(64)  NOT NULL COMMENT '登录用户名',
  `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
  `password`    VARCHAR(128) NOT NULL COMMENT '密码（BCrypt）',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `email`       VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `status`      INT          NOT NULL DEFAULT 0 COMMENT '状态：0=正常，1=禁用',
  `tenant_id`   BIGINT       DEFAULT NULL COMMENT '租户ID',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by`   VARCHAR(64)  DEFAULT NULL,
  `update_by`   VARCHAR(64)  DEFAULT NULL,
  `deleted`     INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

DROP TABLE IF EXISTS `auth`.`sys_role`;
CREATE TABLE `auth`.`sys_role` (
  `id`          BIGINT       NOT NULL,
  `role_code`   VARCHAR(64)  NOT NULL COMMENT '角色编码',
  `role_name`   VARCHAR(64)  NOT NULL COMMENT '角色名称',
  `status`      INT          NOT NULL DEFAULT 0,
  `remark`      VARCHAR(255) DEFAULT NULL,
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by`   VARCHAR(64)  DEFAULT NULL,
  `update_by`   VARCHAR(64)  DEFAULT NULL,
  `deleted`     INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

DROP TABLE IF EXISTS `auth`.`sys_menu`;
CREATE TABLE `auth`.`sys_menu` (
  `id`          BIGINT       NOT NULL,
  `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  `name`        VARCHAR(64)  NOT NULL COMMENT '菜单名称',
  `path`        VARCHAR(255) DEFAULT NULL COMMENT '前端路由路径',
  `component`   VARCHAR(255) DEFAULT NULL COMMENT '前端组件路径',
  `icon`        VARCHAR(64)  DEFAULT NULL COMMENT '菜单图标',
  `type`        INT          NOT NULL DEFAULT 0 COMMENT '类型：0=目录，1=菜单，2=按钮',
  `permission`  VARCHAR(128) DEFAULT NULL COMMENT '权限标识',
  `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
  `status`      INT          NOT NULL DEFAULT 0,
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by`   VARCHAR(64)  DEFAULT NULL,
  `update_by`   VARCHAR(64)  DEFAULT NULL,
  `deleted`     INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

DROP TABLE IF EXISTS `auth`.`sys_user_role`;
CREATE TABLE `auth`.`sys_user_role` (
  `id`          BIGINT   NOT NULL,
  `user_id`     BIGINT   NOT NULL,
  `role_id`     BIGINT   NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

DROP TABLE IF EXISTS `auth`.`sys_role_menu`;
CREATE TABLE `auth`.`sys_role_menu` (
  `id`          BIGINT   NOT NULL,
  `role_id`     BIGINT   NOT NULL,
  `menu_id`     BIGINT   NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-菜单关联表';

-- =====================================================================
-- 二、FICO 财务会计模块
-- =====================================================================
DROP TABLE IF EXISTS `fico`.`voucher`;
CREATE TABLE `fico`.`voucher` (
  `id`          BIGINT        NOT NULL COMMENT '主键ID',
  `voucher_no`  VARCHAR(64)   NOT NULL COMMENT '凭证编号',
  `title`       VARCHAR(255)  NOT NULL COMMENT '凭证摘要',
  `amount`      DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `direction`   INT           NOT NULL DEFAULT 0 COMMENT '方向：0=借方，1=贷方',
  `period`      VARCHAR(20)   DEFAULT NULL COMMENT '会计期间（如 2026-08）',
  `status`      INT           NOT NULL DEFAULT 0 COMMENT '状态：0=草稿，1=已审核',
  `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by`   VARCHAR(64)   DEFAULT NULL,
  `update_by`   VARCHAR(64)   DEFAULT NULL,
  `deleted`     INT           NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_voucher_no` (`voucher_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会计凭证表';

-- =====================================================================
-- 三、SCM 供应链管理模块
-- =====================================================================
DROP TABLE IF EXISTS `scm`.`purchase_order`;
CREATE TABLE `scm`.`purchase_order` (
  `id`           BIGINT        NOT NULL COMMENT '主键ID',
  `order_no`     VARCHAR(64)   NOT NULL COMMENT '采购订单号',
  `supplier_name` VARCHAR(128) NOT NULL COMMENT '供应商名称',
  `total_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '采购总金额',
  `dept_id`      BIGINT        DEFAULT NULL COMMENT '申请部门ID（跨库关联 auth.sys_user）',
  `status`       INT           NOT NULL DEFAULT 0 COMMENT '状态：0=待审批，1=已审批，2=已驳回',
  `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by`    VARCHAR(64)   DEFAULT NULL,
  `update_by`    VARCHAR(64)   DEFAULT NULL,
  `deleted`      INT           NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';

-- =====================================================================
-- 四、MES 制造执行系统模块
-- =====================================================================
DROP TABLE IF EXISTS `mes`.`work_order`;
CREATE TABLE `mes`.`work_order` (
  `id`            BIGINT       NOT NULL COMMENT '主键ID',
  `work_order_no` VARCHAR(64)  NOT NULL COMMENT '工单编号',
  `product_name`  VARCHAR(128) NOT NULL COMMENT '产品名称',
  `quantity`      INT          NOT NULL DEFAULT 0 COMMENT '计划数量',
  `status`        INT          NOT NULL DEFAULT 0 COMMENT '状态：0=新建，1=生产中，2=已完成',
  `planned_start` DATETIME     DEFAULT NULL COMMENT '计划开始时间',
  `planned_end`   DATETIME     DEFAULT NULL COMMENT '计划结束时间',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by`     VARCHAR(64)  DEFAULT NULL,
  `update_by`     VARCHAR(64)  DEFAULT NULL,
  `deleted`      INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_work_order_no` (`work_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';

-- =====================================================================
-- 五、HRM 人力资源模块
-- =====================================================================
DROP TABLE IF EXISTS `hrm`.`employee`;
CREATE TABLE `hrm`.`employee` (
  `id`          BIGINT       NOT NULL COMMENT '主键ID',
  `emp_no`      VARCHAR(64)  NOT NULL COMMENT '工号',
  `name`        VARCHAR(64)  NOT NULL COMMENT '员工姓名',
  `dept_id`     BIGINT       DEFAULT NULL COMMENT '部门ID',
  `position`    VARCHAR(64)  DEFAULT NULL COMMENT '职位',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `email`       VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `status`      INT          NOT NULL DEFAULT 0 COMMENT '状态：0=在职，1=离职',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by`   VARCHAR(64)  DEFAULT NULL,
  `update_by`   VARCHAR(64)  DEFAULT NULL,
  `deleted`     INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_emp_no` (`emp_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- =====================================================================
-- 六、CRM 客户关系管理模块
-- =====================================================================
DROP TABLE IF EXISTS `crm`.`customer`;
CREATE TABLE `crm`.`customer` (
  `id`            BIGINT       NOT NULL COMMENT '主键ID',
  `customer_name` VARCHAR(128) NOT NULL COMMENT '客户名称',
  `contact`       VARCHAR(64)  DEFAULT NULL COMMENT '联系人',
  `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `email`         VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `level`         INT          NOT NULL DEFAULT 0 COMMENT '等级：0=普通，1=VIP，2=战略',
  `status`        INT          NOT NULL DEFAULT 0 COMMENT '状态：0=潜在，1=正式，2=流失',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by`     VARCHAR(64)  DEFAULT NULL,
  `update_by`     VARCHAR(64)  DEFAULT NULL,
  `deleted`       INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- =====================================================================
-- 七、PM 项目管理模块
-- =====================================================================
DROP TABLE IF EXISTS `pm`.`project`;
CREATE TABLE `pm`.`project` (
  `id`           BIGINT        NOT NULL COMMENT '主键ID',
  `project_no`   VARCHAR(64)   NOT NULL COMMENT '项目编号',
  `project_name` VARCHAR(128)  NOT NULL COMMENT '项目名称',
  `manager_id`   BIGINT        DEFAULT NULL COMMENT '项目经理ID（跨库关联 hrm.employee.id）',
  `start_date`   DATE          DEFAULT NULL COMMENT '开始日期',
  `end_date`     DATE          DEFAULT NULL COMMENT '结束日期',
  `budget`       DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '预算',
  `status`       INT           NOT NULL DEFAULT 0 COMMENT '状态：0=规划中，1=进行中，2=已完成，3=已取消',
  `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by`    VARCHAR(64)   DEFAULT NULL,
  `update_by`    VARCHAR(64)   DEFAULT NULL,
  `deleted`     INT           NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_no` (`project_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- =====================================================================
-- 八、BI 商业智能模块
-- =====================================================================
DROP TABLE IF EXISTS `bi`.`report`;
CREATE TABLE `bi`.`report` (
  `id`           BIGINT       NOT NULL COMMENT '主键ID',
  `report_name`  VARCHAR(128) NOT NULL COMMENT '报表名称',
  `report_type`  VARCHAR(64)  DEFAULT NULL COMMENT '报表类型',
  `data_source`  VARCHAR(255) DEFAULT NULL COMMENT '数据源',
  `status`       INT          NOT NULL DEFAULT 0 COMMENT '状态：0=草稿，1=已发布',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by`    VARCHAR(64)  DEFAULT NULL,
  `update_by`    VARCHAR(64)  DEFAULT NULL,
  `deleted`     INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表表';

-- =====================================================================
-- 九、OA 办公自动化模块
-- =====================================================================
DROP TABLE IF EXISTS `oa`.`approval`;
CREATE TABLE `oa`.`approval` (
  `id`           BIGINT       NOT NULL COMMENT '主键ID',
  `title`        VARCHAR(255) NOT NULL COMMENT '审批标题',
  `applicant_id` BIGINT      NOT NULL COMMENT '申请人ID（跨库关联 auth.sys_user.id）',
  `type`         VARCHAR(64)  DEFAULT NULL COMMENT '审批类型',
  `status`       INT          NOT NULL DEFAULT 0 COMMENT '状态：0=待审批，1=已通过，2=已驳回',
  `content`      TEXT         DEFAULT NULL COMMENT '审批内容',
  `comment`      VARCHAR(512) DEFAULT NULL COMMENT '审批意见',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by`    VARCHAR(64)  DEFAULT NULL,
  `update_by`    VARCHAR(64)  DEFAULT NULL,
  `deleted`     INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批表';
