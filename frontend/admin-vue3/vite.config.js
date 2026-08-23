/*  ====================================================================================
    fatjar 管理后台 - Vite 配置 vite.config.js
    ------------------------------------------------------------------------------------
    功能：
      1. 启用 Vue3 SFC 编译支持（@vitejs/plugin-vue）
      2. Element Plus 按需自动导入（unplugin-auto-import + unplugin-vue-components）
      3. 开发服务器：端口 5173，自动打开浏览器
      4. 代理 /api -> http://localhost:8080，解决跨域
      5. 路径别名 @ -> /src，简化 import
    ==================================================================================== */

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { fileURLToPath, URL } from 'node:url'

// 使用 defineConfig 获得 TS/IDE 类型提示
export default defineConfig({
  // ---------- 插件配置 ----------
  plugins: [
    // Vue3 单文件组件编译
    vue(),
    // Element Plus API 自动导入（如 ElMessage / ElMessageBox 无需手动 import）
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    // Element Plus 组件自动注册（<el-button> 等无需手动注册）
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],

  // ---------- 路径别名 ----------
  resolve: {
    alias: {
      // @ 指向 src 目录，简化 import 路径
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },

  // ---------- 开发服务器配置 ----------
  server: {
    // 监听端口：5173（与端口规划文档一致）
    port: 5173,
    // 启动后自动打开浏览器
    open: true,
    // 代理配置：将 /api 前缀的请求转发到后端
    proxy: {
      '/api': {
        // 后端 DEV API 基址
        target: 'http://localhost:8080',
        // 修改请求头 origin 为目标地址，避免后端 CORS 拒绝
        changeOrigin: true,
        // 重写路径：去掉 /api 前缀（后端接口无 /api 前缀）
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },

  // ---------- 构建配置 ----------
  build: {
    // 输出目录：dist/
    outDir: 'dist',
    // 生产环境去除 console 与 debugger
    minify: 'esbuild',
    // chunk 大小警告阈值（KB）
    chunkSizeWarningLimit: 1500,
    // rollup 分包策略：分离 element-plus / vue 等第三方库，提升缓存命中
    rollupOptions: {
      output: {
        manualChunks: {
          vue: ['vue', 'vue-router', 'pinia'],
          'element-plus': ['element-plus', '@element-plus/icons-vue'],
        },
      },
    },
  },
})
