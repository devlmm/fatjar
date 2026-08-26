/*  ====================================================================================
    fatjar website 静态服务器 - 基于 Node.js 内置 http 模块（零依赖）
    ------------------------------------------------------------------------------------
    功能：
      1. 托管 website/ 目录下的静态文件（HTML/CSS/JS/图片/字体）
      2. 默认入口 index.html，支持子目录访问
      3. 自动识别 MIME 类型，UTF-8 编码
      4. 防止路径穿越攻击（.. 越权）
    启动方式：
      - 命令行：node serve.js
      - npm：npm run serve / npm run dev
      - IDEA：右上角运行配置 "website: serve" 一键启动
    访问地址：http://localhost:8090
    ==================================================================================== */

const http = require('http');
const fs = require('fs');
const path = require('path');

// 端口与根目录配置（与项目端口规划一致：website=8090）
const PORT = 8090;
const ROOT = __dirname;

// 扩展名 -> MIME 类型映射表（覆盖 website 用到的所有静态资源类型）
const MIME = {
  '.html': 'text/html; charset=utf-8',
  '.htm': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.mjs': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.xml': 'application/xml; charset=utf-8',
  '.txt': 'text/plain; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.webp': 'image/webp',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.ttf': 'font/ttf',
  '.otf': 'font/otf',
  '.eot': 'application/vnd.ms-fontobject',
  '.map': 'application/json; charset=utf-8',
};

/**
 * 创建 HTTP 服务器：解析 URL -> 读取文件 -> 返回响应
 */
const server = http.createServer((req, res) => {
  // 解析 URL，去掉 query 参数部分
  let urlPath = decodeURIComponent(req.url.split('?')[0]);

  // 根路径返回 index.html
  if (urlPath === '/') urlPath = '/index.html';

  // 拼接绝对路径并防止路径穿越（必须仍在 ROOT 之内）
  const filePath = path.join(ROOT, urlPath);
  if (!filePath.startsWith(ROOT)) {
    res.writeHead(403, { 'Content-Type': 'text/plain; charset=utf-8' });
    res.end('403 Forbidden');
    return;
  }

  // 读取文件并返回（不存在则 404）
  fs.readFile(filePath, (err, data) => {
    if (err) {
      res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
      res.end('404 Not Found: ' + urlPath);
      return;
    }
    const ext = path.extname(filePath).toLowerCase();
    const mime = MIME[ext] || 'application/octet-stream';
    res.writeHead(200, { 'Content-Type': mime });
    res.end(data);
  });
});

// 启动监听并打印访问地址（与后端 StartupInfoPrinter 风格一致）
server.listen(PORT, () => {
  console.log('═══════════════════════════════════════════════════');
  console.log('  \u2705 fatjar website 静态服务已启动');
  console.log('═══════════════════════════════════════════════════');
  console.log('  访问地址: http://localhost:' + PORT);
  console.log('  根目录  : ' + ROOT);
  console.log('  停止服务: Ctrl + C');
  console.log('═══════════════════════════════════════════════════');
});
