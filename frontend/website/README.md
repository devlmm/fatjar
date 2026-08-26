# fatjar-frontend-website

> HTML5 静态官网 - fatjar 企业云原生大单体业务管理系统脚手架门户

## 目录结构

```
website/
├── pom.xml         # Maven 占位 POM（不参与编译）
├── package.json    # npm 脚本入口（仅 serve/dev，无依赖）
├── serve.js        # Node.js 内置静态服务器（零依赖）
├── index.html      # 单页官网首页
├── css/
│   └── style.css   # 全局样式表
├── js/
│   └── main.js     # 交互脚本（平滑滚动/导航高亮）
└── README.md       # 本文件
```

## 快速启动

### 方式一：Node.js 内置静态服务器（推荐，零依赖）

> 项目自带 `serve.js`，基于 Node.js 内置 `http` 模块，无需任何 npm 包
> 前端项目已依赖 Node.js（admin-vue3 / uniapp 同样需要），无需额外安装

```bash
# 进入 website 目录
cd frontend/website

# 启动静态服务器，监听 8090 端口
node serve.js
# 或等价命令
npm run serve
```

浏览器访问：<http://localhost:8090>

IDEA 用户：右上角运行配置下拉框选择 `website: serve`，点绿色三角 ▶ 一键启动。

### 方式二：Python 内置 HTTP 服务（备选，需 Python 3）

> 适合已装 Python 3 的环境，使用自带 `http.server` 模块

```bash
# 进入 website 目录
cd frontend/website

# 启动静态服务器，监听 8090 端口
python -m http.server 8090
```

浏览器访问：<http://localhost:8090>

### 方式三：Nginx 静态托管（生产环境）

将 `website/` 整个目录作为 Nginx 静态根目录：

```nginx
server {
    listen       8090;
    server_name  localhost;

    root   /usr/share/nginx/html/website;
    index  index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

启动 nginx 后访问：<http://localhost:8090>

## 端口规划

| 服务       | 端口  |
|------------|-------|
| website    | 8090  |
| admin-vue3 | 5173  |
| 后端 API   | 8080  |
| API 文档   | 8080/doc.html |

## 浏览器兼容

- Chrome / Edge / Firefox / Safari 最新两个大版本
- 移动端：iOS 13+ / Android 8+
- 依赖：CSS Grid / Flexbox / `backdrop-filter`（现代浏览器原生支持）

## 与后端联动

页面内的「API 文档」「进入管理后台」按钮分别跳转：

- API 文档：<http://localhost:8080/doc.html>
- 管理后台：<http://localhost:5173>

请先启动后端服务（参见 `scripts/run.ps1` / `scripts/run.sh`）。
