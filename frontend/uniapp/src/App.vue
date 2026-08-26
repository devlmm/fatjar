<!--
  ====================================================================================
  fatjar uniapp - 根组件 src/App.vue
  ------------------------------------------------------------------------------------
  说明：
    1. globalStyle 在 pages.json 中配置；这里仅设置全局生命周期
    2. onLaunch：应用启动时执行，可用于检查更新、获取系统信息
    3. onShow：应用从后台进入前台
    4. onHide：应用从前台进入后台
  ====================================================================================
-->
<script>
export default {
  // 应用生命周期
  onLaunch: function () {
    // 应用启动时打印日志（生产环境会被去除）
    console.log('fatjar uniapp 启动')
    // 启动时检查登录态：未登录直接跳登录页
    // uni.addInterceptor（permission.js）只拦截主动路由跳转，应用首次进入首页不走拦截器，需在此兜底
    try {
      if (!uni.getStorageSync('fatjar_token')) {
        uni.reLaunch({ url: '/pages/login/login' })
      }
    } catch (e) {
      uni.reLaunch({ url: '/pages/login/login' })
    }
  },
  onShow: function () {
    console.log('应用进入前台')
  },
  onHide: function () {
    console.log('应用进入后台')
  },
}
</script>

<style>
/* ---------- 全局样式 ---------- */
/* uniapp 内置基础样式，所有页面通用 */

/* 字体族：iOS/Android 自带中文字体 */
page {
  background-color: #f5f5f5;
  color: #333;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  font-size: 28rpx; /* uniapp 推荐用 rpx 单位，自动适配各端 */
}

/* 去除按钮默认边框 */
button {
  border: none;
  outline: none;
}

/* 通用容器：白底 + 圆角 + 阴影 */
.container-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

/* 主题色变量（小程序不支持 CSS 变量，这里硬编码） */
.text-primary {
  color: #1677ff;
}
.bg-primary {
  background-color: #1677ff;
}
</style>
