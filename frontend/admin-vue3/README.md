# fatjar-frontend-admin-vue3

> Vue3 管理后台 - fatjar 企业云原生大单体业务管理系统统一大后台

## 技术栈

- **Vue 3.4** + Composition API (`<script setup>`)
- **Vite 5** 极速构建
- **Element Plus 2.6** UI 组件库（按需自动导入）
- **Pinia 2** 状态管理
- **Vue Router 4** 路由
- **Axios** HTTP 客户端

## 目录结构

```
admin-vue3/
├── pom.xml                  # Maven 占位 POM（不参与编译）
├── package.json             # npm 依赖与脚本
├── vite.config.js           # Vite 配置（端口 5173、代理 /api）
├── index.html               # Vite 入口 HTML
├── public/                  # 静态资源（favicon 等）
└── src/
    ├── main.js              # 应用入口（挂载 ElementPlus/Pinia/Router）
    ├── App.vue              # 根组件
    ├── router/
    │   └── index.js         # 路由配置（登录/仪表盘/系统管理/ERP/OA/CRM/EMS）
    ├── stores/
    │   └── user.js          # 用户 Pinia store（token/userInfo/login/logout）
    ├── utils/
    │   └── request.js       # axios 封装（拦截器、401 跳登录）
    ├── styles/
    │   └── main.css         # 全局样式与主题变量
    └── views/
        ├── login/index.vue      # 登录页
        ├── layout/index.vue     # 后台布局（侧边菜单 + 顶部）
        ├── dashboard/index.vue  # 仪表盘
        ├── sys/
        │   ├── user.vue         # 用户管理
        │   ├── role.vue         # 角色管理
        │   └── menu.vue         # 菜单管理
        ├── erp/product.vue      # 商品管理
        ├── oa/approval.vue      # 审批管理
        ├── crm/customer.vue     # 客户管理
        ├── ems/account.vue      # 设备账户
        └── error/404.vue        # 404 页
```

## 快速启动

```bash
# 1. 进入项目目录
cd frontend/admin-vue3

# 2. 安装依赖（推荐使用 pnpm 加速）
npm install
# 或：pnpm install

# 3. 启动开发服务器（端口 5173，自动打开浏览器）
npm run dev

# 4. 打包生产环境
npm run build

# 5. 预览生产构建（端口 5174）
npm run preview
```

启动后访问：<http://localhost:5173>

## 后端 API 联调

`vite.config.js` 中已配置代理：

```
/api  →  http://localhost:8080
```

前端所有请求经 `src/utils/request.js` 统一加上 `/api` 前缀，由 Vite 转发到后端。
**注意**：开发时请先启动后端服务（参见 `scripts/run.ps1` / `scripts/run.sh`）。

## 主要接口约定

| 模块         | 接口路径                | 方法   |
|--------------|------------------------|--------|
| 登录         | /auth/login            | POST   |
| 退出         | /auth/logout           | POST   |
| 当前用户     | /auth/info             | GET    |
| 用户分页     | /sys/user/page         | GET    |
| 角色分页     | /sys/role/page         | GET    |
| 菜单树       | /sys/menu/tree         | GET    |
| 商品分页     | /erp/product/page      | GET    |
| 审批分页     | /oa/approval/page      | GET    |
| 客户分页     | /crm/customer/page     | GET    |
| 设备账户分页 | /ems/account/page      | GET    |

业务返回结构约定：

```json
{ "code": 200, "msg": "ok", "data": { ... } }
```

- `code === 200`：成功
- `code === 401`：token 失效，自动跳转登录页

## 演示账号

```
用户名：admin
密码：123456
```

## 端口规划

| 服务         | 端口  |
|--------------|-------|
| admin-vue3   | 5173  |
| 后端 API     | 8080  |
| API 文档     | 8080/doc.html |
| website      | 8090  |
