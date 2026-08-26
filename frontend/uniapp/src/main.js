/*  ====================================================================================
    fatjar uniapp - 应用入口 src/main.js
    ------------------------------------------------------------------------------------
    说明：
      1. 引入 uniapp 公共样式（含内置组件样式）
      2. 创建 Vue3 应用实例，挂载到 #app
      3. uniapp 自动注入 uni / wx 等全局对象，无需手动 import
    ==================================================================================== */

import { createSSRApp } from 'vue'
import App from './App.vue'
// 注册路由登录拦截器（副作用 import：执行 uni.addInterceptor 注册全局路由守卫）
import './utils/permission.js'

export function createApp() {
  const app = createSSRApp(App)
  return { app }
}
