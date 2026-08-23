# fatjar-frontend-uniapp

> uniapp 移动端 - fatjar 企业云原生大单体业务管理系统移动端

## 多端覆盖

一份代码，多端运行：

- **H5 端**：浏览器访问
- **微信小程序**：上传微信开发者工具
- **App（iOS/Android）**：HBuilderX 云打包或本地打包

## 目录结构

```
uniapp/
├── pom.xml              # Maven 占位 POM（不参与编译）
├── package.json         # npm 依赖与脚本
├── vite.config.js       # Vite + uniapp 配置
└── src/
    ├── main.js          # 应用入口
    ├── App.vue          # 根组件（全局生命周期 + 全局样式）
    ├── pages.json       # 页面路由配置（含 tabBar）
    ├── manifest.json     # 应用配置（appid/appname/各端设置）
    ├── utils/
    │   └── request.js   # uni.request 网络封装
    └── pages/
        ├── index/index.vue   # 首页（功能宫格 + 公告）
        ├── list/list.vue     # 通用列表（上拉加载 + 下拉刷新）
        ├── mine/mine.vue     # 我的（用户信息 + 设置）
        └── login/login.vue   # 登录页
```

## 快速启动

### 方式一：CLI（推荐）

```bash
# 1. 进入项目目录
cd frontend/uniapp

# 2. 安装依赖
npm install

# 3. 启动 H5 开发服务器（端口 9000）
npm run dev:h5

# 4. 打包 H5
npm run build:h5

# 5. 编译微信小程序（产物在 dist/build/mp-weixin）
npm run build:mp-weixin

# 6. 启动微信小程序开发模式
npm run dev:mp-weixin
```

H5 访问地址：<http://localhost:9000>

### 方式二：HBuilderX（图形化）

1. 打开 HBuilderX
2. 文件 → 导入 → 从本地目录导入 → 选择 `frontend/uniapp`
3. 顶部菜单"运行" → 选择运行平台（浏览器/小程序/手机模拟器）
4. 顶部菜单"发行" → 选择打包平台

## 与后端联调

| 端           | 后端地址配置                                     |
|--------------|--------------------------------------------------|
| H5           | `vite.config.js` 中 proxy `/api` → `http://localhost:8080` |
| 微信小程序   | `manifest.json` 的 `mp-weixin.setting.urlCheck` 关闭，或在小程序后台配置 request 合法域名 |
| App          | 直连后端域名，需在 `request.js` 中修改 `BASE_URL` |

## 主要接口

| 模块     | 接口路径      | 方法 |
|----------|---------------|------|
| 登录     | /auth/login   | POST |
| 退出     | /auth/logout  | POST |
| 用户列表 | /sys/user/page | GET  |

业务返回结构约定：

```json
{ "code": 200, "msg": "ok", "data": { ... } }
```

## 演示账号

```
用户名：admin
密码：123456
```

## tabBar 配置

页面路由与底部 tabBar 在 `src/pages.json` 中配置，包含 3 个 tab：

- 首页 `/pages/index/index`
- 列表 `/pages/list/list`
- 我的 `/pages/mine/mine`

登录页 `/pages/login/login` 不在 tabBar，通过 `uni.reLaunch` 跳转。

## 端口规划

| 服务         | 端口  |
|--------------|-------|
| uniapp H5    | 9000  |
| 后端 API     | 8080  |
| API 文档     | 8080/doc.html |
