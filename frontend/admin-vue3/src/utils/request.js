/*  ====================================================================================
    fatjar 管理后台 - Axios 封装 src/utils/request.js
    ------------------------------------------------------------------------------------
    功能：
      1. baseURL：/api（由 vite proxy 转发到 http://localhost:8080）
      2. 请求拦截器：自动携带 Authorization 头
      3. 响应拦截器：
         - HTTP 状态码非 2xx：抛错 + ElMessage 提示
         - 业务 code 401：token 失效，清空登录态跳转 /login
         - 业务 code 非 200：ElMessage 提示 msg
      4. 超时：10s
    使用方式：
      import request from '@/utils/request'
      request.get('/sys/user/page', { params: { pageNum: 1 } })
      request.post('/auth/login', { username, password })
  ==================================================================================== */

import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// ---------- 创建 axios 实例 ----------
const request = axios.create({
  // 基址：开发环境通过 vite proxy 转发到 http://localhost:8080
  baseURL: '/api',
  // 请求超时：10 秒
  timeout: 10000,
  // 跨域携带 cookie（如后端用 session）
  withCredentials: false,
  // 默认请求头
  headers: {
    'Content-Type': 'application/json;charset=utf-8',
  },
})

// ---------- 请求拦截器 ----------
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 取 token，避免循环依赖 Pinia
    const token = localStorage.getItem('fatjar_token')
    if (token) {
      // 标准 JWT Bearer 协议
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    // 请求构造阶段出错（如配置错误）
    console.error('[请求拦截] 请求构造失败', error)
    return Promise.reject(error)
  }
)

// ---------- 响应拦截器 ----------
request.interceptors.response.use(
  (response) => {
    // HTTP 2xx：取出业务数据
    const res = response.data

    // 约定业务返回结构：{ code: 200, msg: 'ok', data: {...} }
    // 401：未登录或 token 失效
    if (res.code === 401) {
      handleUnauthorized()
      return Promise.reject(new Error('登录已过期，请重新登录'))
    }
    // 非 200：业务异常，统一弹错
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    // 正常返回业务数据（直接返回 res，调用方取 res.data）
    return res
  },
  (error) => {
    // HTTP 非 2xx：网络/服务器异常
    let message = '网络异常，请稍后重试'
    if (error.response) {
      const status = error.response.status
      switch (status) {
        case 401:
          handleUnauthorized()
          return Promise.reject(error)
        case 403:
          message = '没有权限访问该资源'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        case 502:
        case 503:
        case 504:
          message = '服务暂时不可用，请稍后重试'
          break
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时，请检查网络'
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// ---------- 处理 401 未授权 ----------
// 用全局 flag 避免并发请求时多次弹窗
let isReloginShown = false
function handleUnauthorized() {
  if (isReloginShown) return
  isReloginShown = true
  ElMessageBox.confirm('登录状态已失效，请重新登录', '提示', {
    confirmButtonText: '重新登录',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      // 清空本地登录态
      localStorage.removeItem('fatjar_token')
      localStorage.removeItem('fatjar_user_info')
      // 跳转登录页（带 redirect 参数）
      window.location.href = `/login?redirect=${encodeURIComponent(window.location.pathname)}`
    })
    .catch(() => {})
    .finally(() => {
      isReloginShown = false
    })
}

export default request
