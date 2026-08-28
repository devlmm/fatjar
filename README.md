<div align="center">

# fatjar 企业云原生大单体业务管理系统脚手架

**Enterprise Cloud Ready Monolith Framework**

> 为小微企业从零搭建系统而生 —— 开箱即用、生产级、教学级的大单体三层架构脚手架

![JDK](https://img.shields.io/badge/JDK-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green)
![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.5-blue)
![Maven](https://img.shields.io/badge/Maven-3.9+-red)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

</div>

---

## 目录

- [1. 项目简介](#1-项目简介)
- [2. 核心特性](#2-核心特性)
- [3. 技术栈](#3-技术栈)
- [4. 系统架构](#4-系统架构)
- [5. 模块结构](#5-模块结构)
- [6. DEV 环境几步搭建（JDK/Maven → Docker → 初始化 → 编译 → 启动验证）](#6-dev-环境几步搭建jdkmaven--docker--初始化--编译--启动验证)
  - [6.0 端口规划（部署前先查占用）](#60-端口规划部署前先查占用)
  - [6.1 安装 JDK 17 与 Maven 3.9+](#61-安装-jdk-17-与-maven-39)
  - [6.2 安装 Docker Desktop / Docker Engine](#62-安装-docker-desktop--docker-engine)
  - [6.3 一条命令起 4 个中间件（MySQL / Redis / Nacos / RocketMQ）](#63-一条命令起-4-个中间件mysql--redis--nacos--rocketmq)
  - [6.4 初始化 10 个数据库 + 示例数据](#64-初始化-10-个数据库--示例数据)
  - [6.5 初始化 Nacos 命名空间与配置](#65-初始化-nacos-命名空间与配置)
  - [6.6 Maven 编译打包 42 模块](#66-maven-编译打包-42-模块)
  - [6.7 启动后端并验证登录与健康检查](#67-启动后端并验证登录与健康检查)
  - [6.8 启动前端并登录查看 9 大业务菜单](#68-启动前端并登录查看-9-大业务菜单)
- [7. 日常开发指南](#7-日常开发指南)
- [8. 配置体系](#8-配置体系)
- [9. 数据库设计](#9-数据库设计)
- [10. API 文档](#10-api-文档)
- [11. 单元测试](#11-单元测试)
- [12. 部署发布](#12-部署发布)
- [13. 前端项目](#13-前端项目)
- [14. 常见问题 FAQ](#14-常见问题-faq)
- [15. 贡献指南](#15-贡献指南)

---

## 1. 项目简介

**fatjar** 是一套面向小微企业的「大单体三层架构」业务管理系统脚手架，遵循**简单、可控、出活快**的原则，开箱即用。

### 1.1 解决什么问题

小微企业从公域获得流量，再用产品将流量变现。过程中涉及人财物进销存管理：

| 演进阶段 | 场景 | 特点 |
|---------|------|------|
| 单机 OFFICE | 早期 | Excel/Word 管理即可 |
| 内部管理系统集成（FICO/SCM/MES/HRM/CRM/PM/BI/OA） | 规模扩大 | 访问量小/数据小/用户量小，对协同业务流转统一管理要求高 |
| 微服务流量互联网系统 | 自营品牌 | 公域转私域，访问量大/数据量大/用户量大，对高并发/分布式要求高 |

**本脚手架定位**：大单体三层架构脚手架（当前）→ 未来演进到流量电商微服务（注册/发现/熔断/雪崩/治理）。

### 1.2 设计哲学

- **不过度设计微服务组件**：小微企业首要诉求是出活，分布式事务/服务治理等留待流量阶段
- **本地事务为主**：单实例多库 + `@Transactional`，跨库本地事务，避免 Seata/XA 的资金与运维成本
- **数据库即 Schema**：业务模块通过独立数据库隔离（`auth.sys_user` / `fico.voucher` / `scm.purchase_order`），共享连接池
- **技术能力与业务解耦**：7 个自定义 Starter 封装技术能力，业务模块按需依赖

---

## 2. 核心特性

- ✅ **大单体三层架构**：Controller / Service(+Impl) / Mapper，单实例多库共享连接池
- ✅ **门面 Facade 模式**：业务模块拆 `api`（契约）+ 业务本名实现，跨模块只依赖对方 `api`
- ✅ **权限体系**：JWT + Redis 鉴权、RBAC、菜单/按钮/接口细粒度权限、动态菜单、多租户（默认关闭）
- ✅ **代码生成器**：MyBatis-Plus Generator + Velocity，一键生成 Entity/Mapper/Service/Controller
- ✅ **统一返回与异常**：`R<T>` / `PageResult<T>` / `BizException` + 全局异常处理
- ✅ **雪花算法主键**：单机/多 Pod 唯一，`FATJAR_WORKER_ID` 环境变量防冲突
- ✅ **链路追踪**：MDC + Filter，`X-Trace-Id` 跨服务传播
- ✅ **配置中心**：Spring Cloud Alibaba Nacos Config 统一配置，优先级 `Nacos > application-{env}.yml > application.yml`
- ✅ **三环境支持**：DEV（本地）/ SIT（阿里云测试）/ PRD（阿里云生产）
- ✅ **全链路 DevOps**：Docker + K8S + Jenkins/GitLab CI + Nacos 自动发布
- ✅ **DEV 文档化几步搭建**：README Section 6 覆盖 JDK/Maven → Docker → 初始化 → 编译 → 启动验证，无脚本依赖
- ✅ **全代码注释**：所有类与方法均有中文注释，教学友好

---

## 3. 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 语言环境 | JDK | 17 |
| 容器框架 | Spring | 6.x |
| 集成框架 | Spring Boot | 3.2.5 |
| MVC 框架 | Spring MVC | 6.x |
| 参数校验 | Hibernate-Validator | 8.x |
| ORM 框架 | MyBatis-Plus | 3.5.5 |
| 日志框架 | Logback + SLF4J | 1.4+ |
| 测试框架 | JUnit5 + Mockito | 5.10.2 / 5.11 |
| 权限框架 | Spring Security + JWT (jjwt) | 6.x / 0.12.5 |
| 代码增强 | Lombok + MapStruct | 1.18.32 / 1.5.5 |
| 数据连接 | MySQL + Druid | 8.0.33 / 1.2.22 |
| 构建 | Mvnd / Maven | 3.9+ |
| API 文档 | Knife4j + SpringDoc | 4.5.0 / 2.5.0 |
| JDK 增强 | Guava + Hutool | 33.1.0 / 5.8.27 |
| 三方调用 | Spring RestClient | 6.x |
| 事件通信 | Spring Event | 6.x |
| 分布缓存 | Redis + Redisson | 3.27.2 |
| 削峰消息 | RocketMQ Starter | 2.3.6 |
| 文件上传 | AliOSS | 3.17.4 |
| 定时任务 | XXL-JOB | 2.4.1 |
| 表格生成 | EasyExcel | 3.3.4 |
| 虚拟服务 | Docker + K8S | - |
| 链路追踪 | MDC + Filter | - |
| 自动发布 | Jenkins / GitLab CI | - |
| 代码版本 | Git | - |
| 操作系统 | RockyLinux | - |
| 配置中心 | Nacos (Spring Cloud Alibaba) | SCA 2023.0.1.0 |
| 负载均衡 | Nginx (DEV) / 阿里云 SLB (SIT/PRD) | - |
| 健康检查 | Spring Actuator | 3.x |
| 代码生成 | MyBatis-Plus Generator + Velocity | 3.5.5 / 2.3 |
| 库表版本 | Flyway | 9.22.3 |
| 主键生成 | MyBatis-Plus 雪花算法 | - |
| 本地事务 | `@Transactional` | - |

---

## 4. 系统架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         前端层                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────────┐     │
│  │ 官网静态站点  │  │ Vue3 管理后台 │  │ Uniapp 移动端      │     │
│  │ (HTML5)      │  │ (admin-vue3) │  │ (小程序/APP)        │     │
│  └──────────────┘  └──────────────┘  └────────────────────┘     │
└─────────────────────────────────────────────────────────────────┘
                              │ HTTP RESTful API
┌─────────────────────────────────────────────────────────────────┐
│                      网关/负载均衡层                            │
│  DEV: Nginx  /  SIT/PRD: 阿里云 SLB                            │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                    业务大单体（Spring Boot）                    │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                    Startup 启动模块                     │   │
│  └──────────────────────────────────────────────────────────┘   │
│  ┌──────┬──────┬──────┬──────┬──────┬──────┬──────┬──────┬───┐ │
│  │ Auth │ FICO │ SCM  │ MES  │ HRM  │ CRM  │ PM   │ BI   │ OA │ │
│  │(权限)│(财务)│(供应链)│(制造)│(人力)│(客户)│(项目)│(报表)│(审批)│ │
│  └──┬───┴──┬───┴──┬───┴──┬───┴──┬───┴──┬───┴──┬───┴──┬───┴──┬──┘ │
│     │  每个业务模块分 api（契约）+ 业务本名实现                │
│     │  模块间通过 api 接口调用，实现依赖倒置                   │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                   中间件与基础设施层                            │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌──────────┐ ┌─────────┐   │
│  │  MySQL  │ │  Redis  │ │RocketMQ │ │ XXL-JOB  │ │  Nacos  │   │
│  │(多库共享)│ │(共享缓存)│ │ (削峰)   │ │(定时调度)│ │(配置中心)│   │
│  └─────────┘ └─────────┘ └─────────┘ └──────────┘ └─────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### 4.1 依赖关系（恒为 impl → api，永不循环）

```
fatjar-parent (顶层依赖管理，import fatjar-dependencies BOM)
  ├── fatjar-dependencies (版本 BOM，独立无 parent)
  ├── fatjar-framework (聚合 7 个自定义 Starter)
  ├── fatjar-common (通用工具)
  ├── fatjar-generator (代码生成器)
  ├── fatjar-backend (业务父，聚合 9 个业务根)
  │     ├── auth/  → api + auth        (权限：JWT+Redis+RBAC)
  │     ├── fico/  → api + fico        (财务会计：凭证)
  │     ├── scm/   → api + scm         (供应链：采购订单，跨模块调用 FICO)
  │     ├── mes/   → api + mes         (制造执行：工单)
  │     ├── hrm/   → api + hrm         (人力资源：员工)
  │     ├── crm/   → api + crm         (客户关系：客户)
  │     ├── pm/    → api + pm          (项目管理：项目，跨模块调用 HRM)
  │     ├── bi/    → api + bi          (商业智能：报表)
  │     └── oa/    → api + oa          (办公自动化：审批)
  └── fatjar-startup (启动入口，依赖 9 个业务实现 + 7 个 Starter)
```

**核心规则**：业务实现模块（`fatjar-{biz}`）仅依赖其他模块的 `fatjar-{biz}-api` 契约，**永不依赖对方实现 jar**，从而在 Maven 构建图上恒为 `impl → api`，从根源杜绝循环依赖。

**跨模块调用示例**：
- `scm`（供应链）调用 `fico-api` 的 `FicoVoucherApi#checkBudget` 进行采购预算校验
- `pm`（项目管理）调用 `hrm-api` 的 `HrmEmployeeApi#getEmployeeName` 校验项目经理合法性

跨模块调用统一在 ServiceImpl 中注入对方 Facade Api，通过 `@Autowired` 注入接口而非实现类；写操作叠加 `@Transactional(rollbackFor=Exception.class)` 即可让跨库本地事务覆盖全部数据库变更。

---

## 5. 模块结构

### 5.1 顶层目录

```
fatjar/
├── pom.xml                          # 顶层 parent（聚合 + import BOM）
├── dependencies/                    # 版本 BOM（独立无 parent，集中锁版本）
├── framework/                       # 技术工具箱（7 个自定义 Starter）
├── common/                          # 通用工具（异常/枚举/返回/雪花ID/状态机）
├── generator/                       # 代码生成器（MyBatis-Plus Generator）
├── backend/                         # 业务父（9 个业务根）
│   ├── auth/                        # 权限模块（JWT+Redis+RBAC+多租户）
│   ├── fico/                        # FICO 财务会计（凭证）
│   ├── scm/                         # SCM 供应链管理（采购订单，跨调用 FICO）
│   ├── mes/                         # MES 制造执行（工单）
│   ├── hrm/                         # HRM 人力资源（员工）
│   ├── crm/                         # CRM 客户关系（客户）
│   ├── pm/                          # PM 项目管理（项目，跨调用 HRM）
│   ├── bi/                          # BI 商业智能（报表）
│   └── oa/                          # OA 办公自动化（审批）
├── startup/                         # 启动入口（整合所有模块 + 三环境配置）
├── frontend/                        # 前端聚合（website / admin-vue3 / uniapp）
└── deployment/                      # 部署（SQL / Nacos / Docker / K8s / CI-CD）
```

### 5.2 业务模块嵌套结构（门面 Facade + 业务本名实现）

每个业务模块采用「视觉业务根 + 两层嵌套」布局：

```
backend/{biz}/                       # 视觉业务根（aggregator pom, artifactId=fatjar-{biz}-parent）
├── pom.xml                          # 聚合 api + 业务实现，继承 fatjar-backend
├── api/                             # fatjar-{biz}-api：门面契约
│   └── src/main/java/com/workspace/fatjar/{biz}/
│       ├── api/{Biz}Api.java        # Facade 接口（只暴露跨模块方法）
│       ├── dto/                     # 数据传输对象（永不暴露 Entity）
│       └── ro/                      # 请求对象
└── {biz}/                           # fatjar-{biz}（业务本名，无 -impl 后缀）
    └── src/main/java/com/workspace/fatjar/{biz}/
        ├── entity/                  # 数据库实体（@TableName）
        ├── mapper/                  # Mapper 接口（仅声明 + @Param）
        ├── service/                 # 内部 Service 接口（extends IService）
        │   └── impl/                # ServiceImpl（implements Service + Api 双契约）
        └── controller/              # RestController
    └── src/main/resources/mapper/   # Mapper XML（手写 SQL）
```

**Facade 核心规则**：`api` 模块的 Facade 接口只暴露「其他业务系统需要调用的方法」（如 `checkBudget`、`getEmployeeName`），用 DTO/RO/原始类型，**永不暴露 Entity**。`ServiceImpl` 同时实现内部 Service + Facade Api 两个接口——一个实现满足双契约。

### 5.3 共 42 个 Maven 模块

| 类型 | 模块数 | 说明 |
|------|--------|------|
| 顶层 parent + BOM | 2 | fatjar-parent + fatjar-dependencies |
| framework 聚合 + 7 Starter | 8 | web/mybatis/security/biz-tenant/redis/mq/task |
| common + generator | 2 | 通用工具 + 代码生成 |
| backend 父 + 9 业务聚合器 | 10 | auth/fico/scm/mes/hrm/crm/pm/bi/oa parent |
| 9 api 契约 + 9 业务实现 | 18 | 双模块（Facade + 业务本名） |
| startup | 1 | 启动入口 |
| frontend 聚合 | 1 | website/admin-vue3/uniapp 占位 |

---

## 6. DEV 环境几步搭建（JDK/Maven → Docker → 初始化 → 编译 → 启动验证）

> 目标：一台全新开发机，不依赖任何脚本，按下面 **8 步** 手动操作，即可跑起完整后端 + 前端，并以 `admin / admin123` 登录看到 9 大业务模块菜单。
>
> 核心原则：中间件一律 Docker 起（MySQL/Redis/Nacos/RocketMQ），版本/端口/密码全部固定；JDK 必须是 17，Maven 必须是 3.9+。

### 6.0 端口规划（部署前先查占用）

| 服务 | 端口 | 默认账号/说明 |
|------|------|---------------|
| fatjar-app | 8080 | 业务 HTTP 接口 |
| Actuator | 8081 | 健康检查 `/actuator/health` |
| MySQL 8 | 3306 | root / root |
| Redis 7 | 6379 | 无密码 |
| Nacos 2.2.3 | 8848 | nacos / nacos（控制台） |
| RocketMQ | 9876 | NameServer，无密码 |
| admin-vue3 (Vite) | 5173 | 管理后台 |
| uniapp H5 | 9000 | 移动端 |
| website (静态) | 8090 | 官网 |

---

### 6.1 安装 JDK 17 与 Maven 3.9+

**必选版本**：JDK 17（Spring Boot 3.x 最低要求），Maven 3.9.x。安装后重开终端验证：
```
java -version   # 需含 "version "17""
mvn  -version   # 需含 "Apache Maven 3.9" 且 Java 为 17
```

**Windows 手动部署（最稳）**：
1. JDK 17：下载 <https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.11_9.zip>，解压到 `C:\tools\jdk17`，最终 `C:\tools\jdk17\bin\java.exe` 存在
2. Maven：下载 <https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip>，解压到 `C:\tools\maven`
3. 系统环境变量（系统属性 → 高级 → 环境变量 → 系统变量）：

```
JAVA_HOME          = C:\tools\jdk17
MAVEN_HOME         = C:\tools\maven
Path              → 追加：%JAVA_HOME%\bin  与  %MAVEN_HOME%\bin
JAVA_TOOL_OPTIONS  = -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8
```

**Linux (Ubuntu 22.04)**：
```bash
sudo apt-get update
sudo apt-get install -y wget tar
# JDK 17 Temurin
wget -O jdk17.tgz "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_linux_hotspot_17.0.11_9.tar.gz"
sudo tar -xzf jdk17.tgz -C /opt && sudo ln -s /opt/jdk-17.* /opt/jdk17
# Maven 3.9.9
wget -O mvn.tgz "https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz"
sudo tar -xzf mvn.tgz -C /opt && sudo ln -s /opt/apache-maven-3.9.9 /opt/maven
# 持久化
sudo tee /etc/profile.d/fatjar.sh <<'EOF'
export JAVA_HOME=/opt/jdk17
export MAVEN_HOME=/opt/maven
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH
export JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8'
EOF
source /etc/profile.d/fatjar.sh && java -version && mvn -version
```

**macOS (Homebrew)**：
```bash
brew install --cask temurin17
brew install maven
cat >> ~/.zshrc <<'EOF'
export JAVA_HOME="$(/usr/libexec/java_home -v17)"
export JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8'
EOF
source ~/.zshrc && java -version && mvn -version
```

---

### 6.2 安装 Docker Desktop / Docker Engine

DEV 环境所有中间件用 Docker 容器化，**不建议本机直装 MySQL/Redis/Nacos**。

- **Windows / macOS**：下载 <https://www.docker.com/products/docker-desktop> 安装（Windows 推荐勾选 WSL 2 Backend）。启动后右下角鲸鱼图标变绿即为 daemon 就绪，新开终端运行 `docker run --rm hello-world` 应输出 "Hello from Docker!"。
- **Linux Ubuntu 22.04**：

```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg lsb-release
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update && sudo apt-get install -y docker-ce docker-ce-cli containerd.io
sudo usermod -aG docker $USER && newgrp docker
sudo systemctl enable --now docker
docker run --rm hello-world
```

---

### 6.3 一条命令起 4 个中间件（MySQL / Redis / Nacos / RocketMQ）

固定镜像、固定端口、固定密码、固定容器名、固定 volume 持久化。首次执行会拉镜像，约 5~15 分钟。

**Windows PowerShell**（复制即可）：
```powershell
docker run -d --name fatjar-mysql --restart unless-stopped -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=fatjar -v fatjar-mysql-data:/var/lib/mysql mysql:8.0 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
docker run -d --name fatjar-redis --restart unless-stopped -p 6379:6379 -v fatjar-redis-data:/data redis:7-alpine
docker run -d --name fatjar-nacos --restart unless-stopped -p 8848:8848 -p 9848:9848 -e MODE=standalone -e NACOS_AUTH_ENABLE=false -v fatjar-nacos-data:/home/nacos/data nacos/nacos-server:v2.2.3
docker run -d --name fatjar-rocketmq --restart unless-stopped -p 9876:9876 -v fatjar-rocketmq-data:/home/rocketmq/store apache/rocketmq:5.2.0 sh mqnamesrv
```

**Linux / macOS Bash**（同上，只需每行删掉末尾反引号，或整段复制也可）：
```bash
docker run -d --name fatjar-mysql    --restart unless-stopped -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=fatjar -v fatjar-mysql-data:/var/lib/mysql    mysql:8.0 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
docker run -d --name fatjar-redis    --restart unless-stopped -p 6379:6379 -v fatjar-redis-data:/data           redis:7-alpine
docker run -d --name fatjar-nacos    --restart unless-stopped -p 8848:8848 -p 9848:9848 -e MODE=standalone -e NACOS_AUTH_ENABLE=false -v fatjar-nacos-data:/home/nacos/data nacos/nacos-server:v2.2.3
docker run -d --name fatjar-rocketmq --restart unless-stopped -p 9876:9876 -v fatjar-rocketmq-data:/home/rocketmq/store apache/rocketmq:5.2.0 sh mqnamesrv
```

**验证 4 个容器状态**：
```
docker ps          # 应看到 4 个容器，STATUS 均为 Up ...（不是 Restarting / Exited）
```

> 后续电脑重启后只要执行：`docker start fatjar-mysql fatjar-redis fatjar-nacos fatjar-rocketmq` 即可。

---

### 6.4 初始化 10 个数据库 + 示例数据

**先确认 MySQL 3306 已就绪**（首次启动初始化 data 目录需 30~60 秒）：
```
docker exec -it fatjar-mysql mysqladmin ping -uroot -proot
# 成功输出：mysqld is alive
```

就绪后用 docker exec 在容器里执行 SQL（本机不用装 mysql 客户端）。进入项目根目录执行：

**Windows PowerShell**：
```powershell
cd D:\workspace\fatjar
docker cp deployment/sql/schema.sql fatjar-mysql:/tmp/schema.sql
docker cp deployment/sql/data.sql   fatjar-mysql:/tmp/data.sql
docker exec -it fatjar-mysql sh -c "mysql -uroot -proot < /tmp/schema.sql"
docker exec -it fatjar-mysql sh -c "mysql -uroot -proot < /tmp/data.sql"
# 快速验证 admin 用户存在
docker exec -it fatjar-mysql mysql -uroot -proot -e "SELECT id,username,status FROM auth.sys_user;"
# 期望：id=1 username=admin status=0
```

**Linux / macOS Bash**：
```bash
cd /path/to/fatjar
docker cp deployment/sql/schema.sql fatjar-mysql:/tmp/schema.sql
docker cp deployment/sql/data.sql   fatjar-mysql:/tmp/data.sql
docker exec -it fatjar-mysql sh -c "mysql -uroot -proot < /tmp/schema.sql"
docker exec -it fatjar-mysql sh -c "mysql -uroot -proot < /tmp/data.sql"
docker exec -it fatjar-mysql mysql -uroot -proot -e "SELECT id,username,status FROM auth.sys_user;"
```

> **要点**：`schema.sql` 创建 10 个数据库（fatjar/auth/fico/scm/mes/hrm/crm/pm/bi/oa）并跨库建表；`data.sql` 用 `库名.表名` 前缀写入 admin/admin123、角色、20 个菜单、角色-菜单关联，以及 8 大业务模块各 2 条示例数据。

---

### 6.5 初始化 Nacos 命名空间与配置

**先确认 Nacos 已启动**：浏览器打开 <http://localhost:8848/nacos>，能看到登录页即成功（`nacos / nacos`）。首次启动需要 30~60 秒。

#### 方式 A：控制台手动（推荐新手，不易错）

1. **命名空间 → 新建 3 个**：

| 命名空间 ID | 命名空间名 | 描述 |
|------------|-----------|------|
| `fatjar-dev` | fatjar开发环境 | DEV |
| `fatjar-sit` | fatjar集成测试 | SIT |
| `fatjar-prd` | fatjar生产环境 | PRD |

> ⚠️ **关键**：DEV 命名空间 ID **必须是 `fatjar-dev`**（和 bootstrap.yml 中 `spring.cloud.nacos.config.namespace=fatjar-dev` 保持一致，不能简写为 dev）。

2. **配置管理 → 配置列表 → 切换到 `fatjar-dev` 命名空间 → 导入配置**：
   - `application-common.yml`，Group=`FATJAR_GROUP`，文件：`deployment/nacos-config/application-common.yml`
   - `application-dev.yml`，Group=`FATJAR_GROUP`，文件：`deployment/nacos-config/application-dev.yml`

3. 同理切换到 `fatjar-sit` / `fatjar-prd` 命名空间，各上传一份：
   - common：`application-common.yml`（3 个命名空间通用同一份）
   - 环境专属：`application-sit.yml` / `application-prd.yml`

#### 方式 B：命令行自动（CI/重复部署用）

```bash
# 建 3 个命名空间
curl -X POST 'http://localhost:8848/nacos/v1/console/namespaces' -d 'customNamespaceId=fatjar-dev&namespaceName=fatjar开发环境&namespaceDesc=DEV'
curl -X POST 'http://localhost:8848/nacos/v1/console/namespaces' -d 'customNamespaceId=fatjar-sit&namespaceName=fatjar集成测试&namespaceDesc=SIT'
curl -X POST 'http://localhost:8848/nacos/v1/console/namespaces' -d 'customNamespaceId=fatjar-prd&namespaceName=fatjar生产环境&namespaceDesc=PRD'

# 往 fatjar-dev 上传 application-common.yml
curl -X POST 'http://localhost:8848/nacos/v1/cs/configs' \
  --data-urlencode 'dataId=application-common.yml' \
  --data-urlencode 'group=FATJAR_GROUP' \
  --data-urlencode 'tenant=fatjar-dev' \
  --data-urlencode 'type=yaml' \
  --data-urlencode "content=$(cat deployment/nacos-config/application-common.yml)"
# application-dev.yml / sit / prd 同理，改 tenant 与 filename 即可
```

---

### 6.6 Maven 编译打包 42 模块

回到项目根目录执行。首次下载依赖约 10~30 分钟，后续增量 1~3 分钟。

**Windows PowerShell**：
```powershell
cd D:\workspace\fatjar
mvn clean install -DskipTests -Pdev
```

**Linux / macOS Bash**：
```bash
cd /path/to/fatjar
export JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8'
mvn clean install -DskipTests -Pdev
```

**成功标志**：
- 控制台最后出现 `BUILD SUCCESS`
- 存在产物：`startup/target/fatjar-startup-1.0.0.jar`
- 42 模块构成：顶层 parent + dependencies（2）+ framework 聚合 + 7 Starter（8）+ common + generator（2）+ backend 父 + 9 业务聚合（10）+ 9 api + 9 impl（18）+ startup（1）+ frontend 聚合（1）= 42

**常见失败三例**：
1. 中文 GBK 乱码报错 → `JAVA_TOOL_OPTIONS` 未生效（Windows 确认系统环境变量后重开终端；Linux/macOS 手动 export 一次再跑）
2. 依赖下载失败 404/502 → `~/.m2/settings.xml` 加阿里云镜像 `https://maven.aliyun.com/repository/public`，然后加 `-U` 强制更新
3. 跨模块符号找不到 → 必须在项目**根目录**执行 `mvn clean install`，不要 cd 进子模块单独打包

---

### 6.7 启动后端并验证登录与健康检查

**Windows（前台，日志直接看，Ctrl+C 停止）**：
```powershell
cd D:\workspace\fatjar
java -jar startup/target/fatjar-startup-1.0.0.jar --spring.profiles.active=dev
```

**Linux / macOS（后台 + 日志文件 + pidfile）**：
```bash
cd /path/to/fatjar
nohup java -jar startup/target/fatjar-startup-1.0.0.jar --spring.profiles.active=dev > fatjar-dev.log 2>&1 &
echo $! > fatjar.pid
tail -f fatjar-dev.log
```

**启动成功判定**：
1. 日志末尾看到 `Started FatjarApplication in xx.xxx seconds`，且无 `ERROR` 堆栈
2. 健康检查 `GET http://localhost:8081/actuator/health` 返回 `{"status":"UP"}`
3. 登录验证（Knife4j 文档或命令行二选一）：
   - Knife4j：浏览器打开 <http://localhost:8080/doc.html> → auth 分组 → POST /auth/login → body `{"username":"admin","password":"admin123"}` → 返回 `data.token` 非空
   - 命令行：

```powershell
# Windows PowerShell
$b = @{username='admin';password='admin123'} | ConvertTo-Json
$r = Invoke-RestMethod http://localhost:8080/auth/login -Method Post -Body $b -ContentType application/json
$r.code        # 应为 200
$r.data.token  # 非空 JWT
```

> 端口被占用时追加 `--server.port=18080 --management.server.port=18081` 改端口。

---

### 6.8 启动前端并登录查看 9 大业务菜单

**① admin-vue3 管理后台（Vue3 + Vite + Element Plus，端口 5173）**：
```powershell
cd D:\workspace\fatjar\frontend\admin-vue3
npm install           # 首次 3~10 分钟；国内建议：npm config set registry https://registry.npmmirror.com
npm run dev           # 启动后按提示浏览器打开 http://localhost:5173
```
登录 `admin / admin123` 后侧边栏出现 9 大模块：系统管理 / 财务会计 / 供应链 / 制造执行 / 人力资源 / 客户关系 / 项目管理 / 商业智能 / 办公自动化，每个子菜单均可 CRUD。

**② uniapp 移动端（H5 端口 9000）**：
```powershell
cd D:\workspace\fatjar\frontend\uniapp
npm install
npm run dev:h5        # 浏览器打开 http://localhost:9000
```
或用 HBuilderX 打开 `frontend/uniapp/`，选「运行 → 运行到浏览器/小程序模拟器」。

**③ website 官网静态页（端口 8090）**：
```powershell
cd D:\workspace\fatjar\frontend\website
python -m http.server 8090     # 浏览器打开 http://localhost:8090
```

**默认账号**：`admin / admin123`，角色 admin（id=1），可见全部 20 个菜单（9 个顶级目录 + 11 个子菜单）。

---

## 7. 日常开发指南

### 7.1 重启后端（代码改动后）

已成功初始化一次（6.3~6.5 完成）后，日常只需要：
```bash
mvn clean package -DskipTests -Pdev
java -jar startup/target/fatjar-startup-1.0.0.jar --spring.profiles.active=dev
```
IDEA 里推荐 Spring Boot Run Configuration + `mvn compile`，比命令行更快。

### 7.2 切换环境 DEV → SIT → PRD

只改 `--spring.profiles.active=` 参数。SIT/PRD 对应的 DB 连接、Nacos namespace 在 `application-sit.yml` / `application-prd.yml`（Nacos 对应命名空间下）中管理：
```
java -jar startup/target/fatjar-startup-1.0.0.jar --spring.profiles.active=sit
```

### 7.3 新增一个业务模块骨架（以 WMS 为例）

1. `backend/pom.xml` modules 列表追加 `<module>wms</module>`
2. 在 `backend/` 下复制 `fico/` 目录为 `wms/`，批量替换 `fico→wms`、`Fico→Wms`、`FICO→WMS`
3. `dependencies/pom.xml` 增加 `fatjar-wms-api` 与 `fatjar-wms` 版本管理
4. `startup/pom.xml` 增加对 `fatjar-wms` 的依赖（实现 jar 才会打进最终 fatjar）
5. MySQL 新建 `wms` 数据库 + 建表；`deployment/sql/schema.sql` 与 `data.sql` 同步更新
6. `auth.sys_menu` 插顶级目录 + 子菜单各一行，并给 admin 角色（id=1）在 `auth.sys_role_menu` 关联

### 7.4 调试教学亮点：跨模块调用 + 本地事务

脚手架特意做了一个端到端的跨模块调用案例 **SCM 采购下单 → FICO 预算校验**，便于理解本项目「门面 Facade + 单实例多库本地事务」的设计：

- 入口：`ScmPurchaseOrderServiceImpl.save(...)` 加了 `@Transactional(rollbackFor=Exception.class)`，方法体内先 `ficoVoucherApi.checkBudget(deptId, amount)`（跨业务系统），再写 scm.purchase_order 表
- 实现：`FicoVoucherServiceImpl` 同时实现 `FicoVoucherApi` 门面接口，SCM 的 pom 只依赖 `fatjar-fico-api`（契约），不依赖实现 jar → 无循环依赖
- 事务边界：两个写操作（SCM 写订单表 + FICO 写预算扣减日志）在同一个 MySQL 实例、同一个 DataSource、同一个本地事务里，任一抛异常都会整体回滚 ✅

**实操验证**：admin-vue3 登录 → 供应链 → 采购订单 → 新增一笔总金额 > 100000 的订单，期望返回 "预算不足" 且 `SELECT * FROM scm.purchase_order` 查无该笔（回滚生效）。

---

## 8. 配置体系

### 8.1 优先级

```
Nacos (application-{env}.yml) > Nacos (application-common.yml) > application-{env}.yml > application.yml
```

- `application.yml`：Nacos 连接信息（server-addr/username/password）+ 本地兗底最小配置
- `application-{dev,sit,prd}.yml`：环境覆盖 + Nacos namespace/import 声明
- Nacos `application-common.yml`：所有环境共享（数据源/Redis/MQ/JWT/MyBatis-Plus）
- Nacos `application-{env}.yml`：环境专属覆盖

集成方案：`spring-cloud-starter-alibaba-nacos-config`（SCA 2023.0.1.0），
通过 `spring.cloud.nacos.config.import` 在启动时从 Nacos 拉取配置，
`optional:` 前缀保证 Nacos 不可用时自动回退到本地配置。

### 8.2 三环境

| 环境 | Nacos 命名空间 | 部署位置 | 日志级别 |
|------|---------------|----------|----------|
| DEV | fatjar-dev | 本地内网 | DEBUG |
| SIT | fatjar-sit | 阿里云测试 | INFO |
| PRD | fatjar-prd | 阿里云生产 | WARN |

### 8.3 Nacos DataId 清单

| DataId | group | 命名空间 | 说明 |
|--------|-------|---------|------|
| application-common.yml | FATJAR_GROUP | fatjar-dev/sit/prd | 通用配置（数据源/Redis/MQ/JWT/MyBatis-Plus） |
| application-dev.yml | FATJAR_GROUP | fatjar-dev | DEV 覆盖 |
| application-sit.yml | FATJAR_GROUP | fatjar-sit | SIT 覆盖 |
| application-prd.yml | FATJAR_GROUP | fatjar-prd | PRD 覆盖 |

### 8.4 关键配置项

```yaml
# mybatis-plus 主键策略：INPUT（应用层雪花算法填充，非数据库自增）
mybatis-plus:
  global-config:
    db-config:
      id-type: INPUT
      logic-delete-field: deleted

# 多租户默认关闭（业务表无 tenant_id 列，开启需所有表加此列）
fatjar:
  tenant:
    enabled: false
  jwt:
    secret: fatjar-secret-key-2024-must-be-at-least-32-bytes-long
    expire: 86400
```

---

## 9. 数据库设计

### 9.1 单实例多库 + 数据库即 Schema

单 MySQL 实例，业务模块通过**独立数据库**（MySQL 数据库即 Schema）隔离，共享连接池与事务管理器：

- **锚定库 `fatjar`**：空库，仅用于 JDBC URL 建立连接（`@TableName` 中不使用）
- **业务库**：`auth` / `fico` / `scm` / `mes` / `hrm` / `crm` / `pm` / `bi` / `oa`，各业务表的 `@TableName` 使用 `db.table` 全限定名

| 模块 | 数据库 | 业务表（去掉模块前缀） | @TableName 示例 |
|------|--------|------------------------|------------------|
| auth（权限） | `auth` | sys_user, sys_role, sys_menu, sys_user_role, sys_role_menu | `auth.sys_user` |
| fico（财务会计） | `fico` | voucher | `fico.voucher` |
| scm（供应链） | `scm` | purchase_order | `scm.purchase_order` |
| mes（制造执行） | `mes` | work_order | `mes.work_order` |
| hrm（人力资源） | `hrm` | employee | `hrm.employee` |
| crm（客户关系） | `crm` | customer | `crm.customer` |
| pm（项目管理） | `pm` | project | `pm.project` |
| bi（商业智能） | `bi` | report | `bi.report` |
| oa（办公自动化） | `oa` | approval | `oa.approval` |

> auth 模块保留 `sys_` 前缀：`user` 是 MySQL 保留字，且 `sys_` 是系统表通用约定，非业务前缀。其他业务模块统一去掉模块前缀（如 `fico.voucher` 而非 `fico.fico_voucher`），表名简洁直观。

### 9.2 跨库事务（本地事务）

单 MySQL 实例 + 单 DataSource + 单 SqlSessionFactory + 单 TransactionManager：

- 跨库操作（如 `scm.purchase_order` 写入 + `fico.voucher` 预算校验回写）在同一 MySQL 实例内
- Spring `@Transactional` 本地事务自动覆盖所有库，**无需分布式事务框架**（XA/Seata）
- 跨库 JOIN 同样支持：`SELECT * FROM auth.sys_user u JOIN oa.approval a ON u.id = a.applicant_id`
- 跨模块调用（如 SCM 采购下单时调用 FICO 预算校验）：在 SCM 的 ServiceImpl 方法上声明 `@Transactional`，FICO 的写库操作与 SCM 的写库操作在同一事务中提交或回滚

### 9.3 初始数据

| 数据 | 主键 ID | 说明 |
|------|--------|------|
| 超管用户 | 1 | admin / admin123（BCrypt 加密） |
| admin 角色 | 1 | role_code=admin，关联全部菜单 |
| 系统菜单 | 100-901 | 9 大顶级目录 + 系统管理 3 个子菜单 + 8 大业务模块各 1 个子菜单 |
| 角色-菜单关联 | 1-20 | admin 角色关联全部 20 个菜单 |
| 各模块示例数据 | 1001-1002 | fico/scm/mes/hrm/crm/pm/bi/oa 各 2 条 demo 数据 |

---

## 10. API 文档

集成 Knife4j + SpringDoc，启动后访问：

- **文档首页**：http://localhost:8080/doc.html
- **OpenAPI JSON**：http://localhost:8080/v3/api-docs

分组：`auth` / `fico` / `scm` / `mes` / `hrm` / `crm` / `pm` / `bi` / `oa`（由 `starter-web` 的 `Knife4jConfig` 注册）。

PRD 环境通过 `fatjar.web.enable-knife4j=false` 关闭文档（安全考虑）。

### 10.1 统一返回格式

```json
{
  "code": 0,
  "message": "操作成功",
  "data": { },
  "traceId": "a1b2c3d4e5f6"
}
```

`code == 0` 表示成功，非 0 表示失败。

---

## 11. 单元测试

关键场景测试位于 `startup/src/test` 与 `common/src/test`，使用 Mockito 模拟依赖，**不依赖 MySQL/Redis 真实环境**，构建期可独立运行：

| 测试类 | 场景数 | 覆盖点 |
|--------|--------|--------|
| `SnowflakeIdGeneratorTest` | 1 | 10 线程并发 10 万 ID 唯一性 |
| `StateMachineTest` | 2 | 状态流转 + 非法流转 |
| `AuthServiceTest` | 4 | 登录成功 / 验证码过期 / 用户不存在 / 账号禁用 |

运行测试：

```bash
mvn test
```

---

## 12. 部署发布

### 12.1 Docker

```bash
# 构建镜像
docker build -f deployment/dockerfiles/Dockerfile -t fatjar-startup:1.0.0 .

# 一键编排（MySQL/Redis/Nacos/RocketMQ/XXL-JOB/fatjar-app）
docker compose -f deployment/dockerfiles/docker-compose.yml up -d
```

Dockerfile 基于 `eclipse-temurin:17-jre`，运行用户 UID=1000，产出可执行 fatjar。

### 12.2 Kubernetes

```bash
kubectl apply -f deployment/k8s/namespace.yaml
kubectl apply -f deployment/k8s/configmap.yaml
kubectl apply -f deployment/k8s/secret.yaml
kubectl apply -f deployment/k8s/deployment.yaml
kubectl apply -f deployment/k8s/service.yaml
kubectl apply -f deployment/k8s/ingress.yaml
```

- Deployment `fatjar-app`：replicas=2，探针走 8081/actuator/health
- 多 Pod 通过 `FATJAR_WORKER_ID` 环境变量区分雪花 ID worker

### 12.3 CI/CD

- **Jenkins**：`deployment/ci-cd/Jenkinsfile`（声明式 pipeline：Checkout/Compile/Test/Build Image/Push/Deploy K8s）
- **GitLab CI**：`deployment/ci-cd/.gitlab-ci.yml`（build/test/docker/deploy）

---

## 13. 前端项目

3 个前端子项目各自独立启动，访问后端 RESTful API：

| 项目 | 路径 | 技术栈 | 启动 |
|------|------|--------|------|
| 官网 | `frontend/website` | HTML5 + CSS3 + JS | `python -m http.server 8090` |
| 管理后台 | `frontend/admin-vue3` | Vue3 + Vite + Element Plus + Pinia | `npm install && npm run dev`（5173） |
| 移动端 | `frontend/uniapp` | uniapp（H5/微信小程序/APP） | HBuilderX 或 `npm run dev:h5`（9000） |

前端 `pom.xml` 仅作 Maven 占位聚合，实际通过 npm/HBuilderX 构建，不参与 Maven 编译。

### 13.1 admin-vue3 后台页面

- 登录页 `/login`、注册页 `/register`、仪表盘 `/dashboard`、404 兜底
- 系统管理 3 个页面：用户管理 / 角色管理 / 菜单管理（`/sys/user|role|menu`）
- 8 大业务模块各一个 CRUD demo 页面，与后端 RESTful API 一一对应：

| 业务模块 | 路由 | 页面文件 | 权限标识 |
|---------|------|----------|----------|
| FICO 财务会计 | `/fico/voucher` | `views/fico/voucher.vue` | `fico:voucher:list` |
| SCM 供应链 | `/scm/purchase-order` | `views/scm/purchaseOrder.vue` | `scm:order:list` |
| MES 制造执行 | `/mes/work-order` | `views/mes/workOrder.vue` | `mes:workorder:list` |
| HRM 人力资源 | `/hrm/employee` | `views/hrm/employee.vue` | `hrm:employee:list` |
| CRM 客户关系 | `/crm/customer` | `views/crm/customer.vue` | `crm:customer:list` |
| PM 项目管理 | `/pm/project` | `views/pm/project.vue` | `pm:project:list` |
| BI 商业智能 | `/bi/report` | `views/bi/report.vue` | `bi:report:list` |
| OA 办公自动化 | `/oa/approval` | `views/oa/approval.vue` | `oa:approval:list` |

菜单来自 `auth.sys_menu` 初始化数据（admin 角色关联全部菜单），路由 meta.group 控制侧边栏分组显示。

### 13.2 uniapp 移动端页面

- 登录页 `pages/login/login`、注册页 `pages/register/register`
- TabBar 首页 `pages/index/index`、我的 `pages/mine/mine`
- 8 大业务模块 demo 页面：`pages/{module}/{biz}.vue`，与 admin-vue3 一一对应

---

## 14. 常见问题 FAQ

### 14.1 Windows 编译乱码？

设置环境变量：

```
JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8
```

### 14.2 多租户开启后业务表报错？

`TenantLineInnerInterceptor` 会给所有 SQL 追加 `tenant_id` 条件，若业务表无此列会报错。默认关闭（`fatjar.tenant.enabled=false`）。启用前需确保所有业务表均含 `tenant_id` 列。

### 14.3 启动报「PasswordEncoder Bean 重复」？

`PasswordEncoder` 已由 `fatjar-spring-boot-starter-security` 的 `SecurityBaseConfig` 统一注册，业务模块（如 auth）的 `SecurityConfig` **不要重复注册**，直接 `@Autowired` 使用即可。

### 14.4 MyBatis 报「already contains statement」？

避免在 Java Mapper 注解（`@Select` 等）与 XML 中重复定义同名方法。本脚手架约定：**手写 SQL 全部放 XML，Java 接口只保留方法声明 + `@Param`**；仅使用 `BaseMapper` 通用方法的 Mapper 不创建 XML。

### 14.5 端口 8080 冲突？

`fatjar-app` 占用 8080，RocketMQ Broker Proxy 与 XXL-JOB Admin 默认也用 8080。本脚手架已将 RocketMQ Broker Proxy 改为 8084、XXL-JOB Admin 改为 8085。

### 14.6 Nacos namespace 不一致？

`application.yml` 中 dev profile 块配置 `spring.cloud.nacos.config.namespace: fatjar-dev`，必须与 Nacos 控制台创建的命名空间 ID 一致（不是 `dev`）。

### 14.7 主键为什么是 INPUT 而非 AUTO？

`id-type=INPUT` 表示由应用层雪花算法填充主键（`MetaObjectHandler` 在 insert 时调用 `IdGeneratorHolder.nextId()`），便于多 Pod 部署全局唯一、分库分表迁移。

---

## 15. 贡献指南

1. Fork 本仓库
2. 新建分支：`git checkout -b feature/your-feature`
3. 提交：`git commit -m 'feat: add your feature'`（遵循 Conventional Commits）
4. 推送：`git push origin feature/your-feature`
5. 提交 Pull Request

### 15.1 代码规范

- 所有类与方法必须有中文注释（类注释含 `@author fatjar`、`@since 1.0.0`、职责说明）
- Mapper 手写 SQL 一律放 XML，Java 接口只留声明 + `@Param`
- 业务写方法加 `@Transactional(rollbackFor=Exception.class)`
- 跨模块调用只依赖对方 `fatjar-{biz}-api`，禁止依赖实现 jar

### 15.2 后续演进路线

- **当前**：大单体三层架构（单实例多库 + 本地事务 + Facade 门面）
- **未来**：流量电商微服务（注册发现/熔断雪崩/治理通信），Facade 契约可直接转为 Feign/Dubbo 远程调用接口

---

<div align="center">

**fatjar** — 让小微企业从零到一，开箱即用。

</div>
