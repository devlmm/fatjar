/*  ====================================================================================
    fatjar uniapp - Vite 配置 vite.config.js
    ------------------------------------------------------------------------------------
    说明：
      1. 引入 @dcloudio/vite-plugin-uni 处理 uniapp 多端编译
      2. H5 端开发服务器端口 9000，代理 /api -> http://localhost:8080
      3. 小程序端编译产物在 dist/dev/mp-weixin 或 dist/build/mp-weixin
    ==================================================================================== */

import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  // ---------- 插件：uniapp 必备 ----------
  plugins: [uni()],

  // ---------- H5 端开发服务器 ----------
  server: {
    port: 9000,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },

  // ---------- 构建产物目录 ----------
  build: {
    outDir: 'dist',
  },
})
