<!--
  ====================================================================================
  fatjar uniapp - 登录页 src/pages/login/login.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 用户名 + 密码登录表单
    2. 调用 /auth/login 接口
    3. 登录成功后保存 token + 跳转首页
    4. 提供注册账号入口，跳转注册页
  ====================================================================================
-->
<template>
  <view class="login-page">
    <!-- 顶部 Logo 区 -->
    <view class="logo-area">
      <view class="logo-mark">F</view>
      <text class="logo-title">fatjar</text>
      <text class="logo-subtitle">企业云原生大单体业务管理系统 · 9 大业务模块</text>
    </view>

    <!-- 登录表单 -->
    <view class="form-area">
      <view class="form-item">
        <input
          v-model="form.username"
          class="form-input"
          placeholder="请输入用户名"
          placeholder-class="placeholder"
        />
      </view>
      <view class="form-item">
        <input
          v-model="form.password"
          class="form-input"
          password
          placeholder="请输入密码"
          placeholder-class="placeholder"
        />
      </view>
      <button class="login-btn" :loading="loading" @click="handleLogin">登 录</button>
      <view class="form-tip">演示账号：admin / admin123</view>
      <view class="register-link" @click="goRegister">还没有账号？立即注册</view>
    </view>
  </view>
</template>

<script>
import request from '@/utils/request.js'

export default {
  data() {
    return {
      form: {
        username: 'admin',
        password: 'admin123',
      },
      loading: false,
    }
  },
  methods: {
    // 跳转注册页
    goRegister() {
      uni.navigateTo({ url: '/pages/register/register' })
    },
    // 登录处理
    async handleLogin() {
      // 简单非空校验
      if (!this.form.username) {
        uni.showToast({ title: '请输入用户名', icon: 'none' })
        return
      }
      if (!this.form.password) {
        uni.showToast({ title: '请输入密码', icon: 'none' })
        return
      }
      this.loading = true
      try {
        const res = await request.post('/auth/login', {
          username: this.form.username,
          password: this.form.password,
        })
        // 保存 token
        request.setToken(res.data.token)
        // 保存用户信息
        if (res.data.userInfo) {
          uni.setStorageSync('fatjar_user_info', JSON.stringify(res.data.userInfo))
        }
        uni.showToast({ title: '登录成功', icon: 'success' })
        // 跳转首页
        setTimeout(() => {
          uni.switchTab({ url: '/pages/index/index' })
        }, 800)
      } catch (e) {
        // 错误已由 request.js 弹窗
        console.error('登录失败', e)
      } finally {
        this.loading = false
      }
    },
  },
}
</script>

<style>
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #1677ff 0%, #0958d9 100%);
  padding: 80rpx 40rpx;
}

/* Logo 区 */
.logo-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 80rpx;
}
.logo-mark {
  width: 100rpx;
  height: 100rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20rpx;
  color: #fff;
  font-size: 56rpx;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
}
.logo-title {
  color: #fff;
  font-size: 44rpx;
  font-weight: 700;
  margin-bottom: 8rpx;
}
.logo-subtitle {
  color: rgba(255, 255, 255, 0.85);
  font-size: 24rpx;
}

/* 表单区 */
.form-area {
  background: #fff;
  border-radius: 20rpx;
  padding: 60rpx 40rpx;
}
.form-item {
  margin-bottom: 32rpx;
}
.form-input {
  width: 100%;
  height: 88rpx;
  border: 2rpx solid #e5e7eb;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}
.placeholder {
  color: #9ca3af;
}
.login-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  background: #1677ff;
  color: #fff;
  border-radius: 12rpx;
  font-size: 32rpx;
  font-weight: 500;
  margin-top: 16rpx;
}
.login-btn::after {
  border: none;
}
.form-tip {
  text-align: center;
  color: #9ca3af;
  font-size: 24rpx;
  margin-top: 32rpx;
}
.register-link {
  text-align: center;
  color: #1677ff;
  font-size: 26rpx;
  margin-top: 24rpx;
}
</style>
