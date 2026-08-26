/*  ====================================================================================
    fatjar uniapp - 网络请求封装 src/utils/request.js
    ------------------------------------------------------------------------------------
    功能：
      1. 基于 uni.request 封装 Promise
      2. baseURL：H5 端 /api（由 vite proxy 转发），小程序直连后端域名
      3. 请求拦截：自动携带 token
      4. 响应拦截：401 跳登录、非 200 业务码弹窗提示
    使用：
      import request from '@/utils/request'
      request.get('/sys/user/page', { data: { pageNum: 1 } })
      request.post('/auth/login', { username, password })
  ==================================================================================== */

// 后端 API 基址
// H5 端：/api 走 vite proxy
// 小程序端：需配置合法域名（在 manifest.json 的 mp-weixin 中），或开发时关闭域名校验
const BASE_URL = '/api'

// 从本地存储获取 token
function getToken() {
  try {
    return uni.getStorageSync('fatjar_token') || ''
  } catch (e) {
    return ''
  }
}

// 保存 token 到本地存储
function setToken(token) {
  try {
    if (token) {
      uni.setStorageSync('fatjar_token', token)
    } else {
      uni.removeStorageSync('fatjar_token')
    }
  } catch (e) {
    console.error('保存 token 失败', e)
  }
}

/**
 * 核心请求方法
 * @param {Object} options { url, method, data, header, hideError }
 * @returns {Promise}
 */
function request(options) {
  const { url, method = 'GET', data = {}, header = {}, hideError = false } = options
  return new Promise((resolve, reject) => {
    // 拼接 token 到请求头
    const token = getToken()
    if (token) {
      header.Authorization = `Bearer ${token}`
    }
    uni.request({
      url: BASE_URL + url,
      method: method.toUpperCase(),
      data,
      header: {
        'Content-Type': 'application/json',
        ...header,
      },
      success: (res) => {
        // HTTP 状态码非 2xx
        if (res.statusCode < 200 || res.statusCode >= 300) {
          // 401 未认证 / 403 无权限：清空登录态跳登录页
          // （后端 RestAuthenticationEntryPoint/RestAccessDeniedHandler 返回 401/403 + R body）
          if (res.statusCode === 401 || res.statusCode === 403) {
            setToken('')
            uni.removeStorageSync('fatjar_user_info')
            uni.showToast({
              title: res.statusCode === 401 ? '登录已过期' : '没有权限，请重新登录',
              icon: 'none',
            })
            setTimeout(() => {
              uni.reLaunch({ url: '/pages/login/login' })
            }, 1000)
            reject(new Error(res.statusCode === 401 ? '未授权' : '无权限'))
            return
          }
          if (!hideError) {
            uni.showToast({ title: `网络错误 ${res.statusCode}`, icon: 'none' })
          }
          reject(new Error(`HTTP ${res.statusCode}`))
          return
        }
        const body = res.data || {}
        // 业务码 10002：未授权（后端 CommonResultCode.UNAUTHORIZED），跳登录
        if (body.code === 10002) {
          setToken('')
          uni.showToast({ title: '登录已过期', icon: 'none' })
          setTimeout(() => {
            uni.reLaunch({ url: '/pages/login/login' })
          }, 1000)
          reject(new Error('未授权'))
          return
        }
        // 业务码非 0：弹窗提示（后端 R 约定 code==0 成功，字段名 message）
        if (body.code !== 0) {
          if (!hideError) {
            uni.showToast({ title: body.message || '请求失败', icon: 'none' })
          }
          reject(new Error(body.message || '请求失败'))
          return
        }
        // 成功：返回业务数据
        resolve(body)
      },
      fail: (err) => {
        if (!hideError) {
          uni.showToast({ title: '网络请求失败', icon: 'none' })
        }
        reject(err)
      },
    })
  })
}

// ---------- 便捷方法 ----------
export default {
  get(url, options = {}) {
    return request({ url, method: 'GET', ...options })
  },
  post(url, data = {}, options = {}) {
    return request({ url, method: 'POST', data, ...options })
  },
  put(url, data = {}, options = {}) {
    return request({ url, method: 'PUT', data, ...options })
  },
  delete(url, options = {}) {
    return request({ url, method: 'DELETE', ...options })
  },
  // 暴露 token 操作工具
  getToken,
  setToken,
}
