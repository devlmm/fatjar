/*  ====================================================================================
    fatjar uniapp - 路由登录拦截 src/utils/permission.js
    ------------------------------------------------------------------------------------
    功能：
      1. 拦截 navigateTo / redirectTo / reLaunch / switchTab 四种路由跳转
      2. 访问需登录页面时，未登录则跳转登录页
      3. 白名单（login/register）免登录可访问
      4. 与 request.js 的 401 跳转、App.vue 的 onLaunch 兜底形成完整登录态守卫
    ------------------------------------------------------------------------------------
    token 存储 key 与 request.js 一致：fatjar_token
    权限体系与 admin-vue3 一致：同一套后端 /auth/login + JWT + /auth/userInfo
    ==================================================================================== */

// 登录页路径
const LOGIN_PAGE = '/pages/login/login'
// 白名单：免登录可访问的页面（登录页、注册页）
const WHITE_LIST = ['/pages/login/login', '/pages/register/register']

/**
 * 规范化 url：去除 query 参数，仅保留 path
 * @param {string} url 原始跳转 url（可能带 ?xxx=yyy）
 * @returns {string} path 部分
 */
function normalizeUrl(url) {
  if (!url) return ''
  return url.split('?')[0]
}

/**
 * 是否已登录（token 存在即视为登录，token 有效性由后端 JwtAuthenticationFilter 校验）
 * @returns {boolean} true 表示本地存在 token
 */
function isLoggedIn() {
  try {
    return !!uni.getStorageSync('fatjar_token')
  } catch (e) {
    return false
  }
}

/**
 * 跳转登录页（reLaunch 清空页面栈，避免返回键回到需登录页）
 */
function redirectToLogin() {
  uni.reLaunch({ url: LOGIN_PAGE })
}

// 注册路由拦截器：四种跳转方式统一拦截
;['navigateTo', 'redirectTo', 'reLaunch', 'switchTab'].forEach((method) => {
  uni.addInterceptor(method, {
    invoke(args) {
      const url = normalizeUrl(args.url)
      // 白名单页面直接放行
      if (WHITE_LIST.includes(url)) {
        return args
      }
      // 需登录且未登录：拦截并跳转登录页
      if (!isLoggedIn()) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        redirectToLogin()
        return false
      }
      return args
    },
  })
})

export default { isLoggedIn, redirectToLogin, WHITE_LIST, LOGIN_PAGE }
