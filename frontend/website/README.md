# fatjar-frontend-website

> HTML5 静态官网 - fatjar 企业云原生大单体业务管理系统脚手架门户

## 目录结构

```
website/
├── pom.xml         # Maven 占位 POM（不参与编译）
├── index.html      # 单页官网首页
├── css/
│   └── style.css   # 全局样式表
├── js/
│   └── main.js     # 交互脚本（平滑滚动/导航高亮）
└── README.md       # 本文件
```

## 快速启动

### 方式一：Python 内置 HTTP 服务（推荐，无需安装任何依赖）

> 适合本地预览，仅 Python 3 自带 `http.server` 模块

```bash
# 进入 website 目录
cd frontend/website

# 启动静态服务器，监听 8090 端口
python -m http.server 8090
```

浏览器访问：<http://localhost:8090>

### 方式二：Node.js http-server

```bash
# 全局安装 http-server（只需一次）
npm install -g http-server

# 进入 website 目录
cd frontend/website

# 启动服务
http-server -p 8090
```

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
