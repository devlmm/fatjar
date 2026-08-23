/*  ====================================================================================
    fatjar 管理后台 - 应用入口 main.js
    ------------------------------------------------------------------------------------
    职责：
      1. 创建 Vue3 应用实例
      2. 全局注册 Element Plus（含中文语言包、所有图标）
      3. 挂载 Pinia 状态管理（必须在 router 之前，避免 router 内使用 store 报错）
      4. 挂载 Vue Router
      5. 全局样式入口
    ==================================================================================== */

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

// ---------- 引入样式（顺序很重要：Element Plus 优先，自定义样式最后覆盖） ----------
import 'element-plus/dist/index.css'
import './styles/main.css'

// 1. 创建 Vue 应用实例
const app = createApp(App)

// 2. 全局注册 Element Plus 所有图标组件（<el-icon><Edit/></el-icon>）
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 3. 挂载 Pinia（状态管理）—— 必须在 router 之前
app.use(createPinia())

// 4. 挂载 Vue Router
app.use(router)

// 5. 注册 Element Plus（含中文语言包）
app.use(ElementPlus, { locale: zhCn })

// 6. 全局错误处理：捕获未处理的 Vue 错误，避免白屏
app.config.errorHandler = (err, instance, info) => {
  console.error('[fatjar 全局错误]', err, info)
}

// 7. 挂载到 DOM
app.mount('#app')

// 开发环境彩蛋
if (import.meta.env.DEV) {
  console.log(
    '%c fatjar 管理后台 %c DEV ',
    'background:#1677ff;color:#fff;padding:4px 8px;border-radius:4px 0 0 4px;',
    'background:#52c41a;color:#fff;padding:4px 8px;border-radius:0 4px 4px 0;'
  )
}
