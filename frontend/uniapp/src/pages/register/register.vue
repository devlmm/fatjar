<!--
  ====================================================================================
  fatjar uniapp - 注册页 src/pages/register/register.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 用户名 + 密码 + 确认密码注册表单
    2. 调用 /auth/register 接口
    3. 注册成功后跳转登录页
    4. 提供返回登录入口
  ====================================================================================
-->
<template>
  <view class="register-page">
    <!-- 顶部 Logo 区 -->
    <view class="logo-area">
      <view class="logo-mark">F</view>
      <text class="logo-title">fatjar</text>
      <text class="logo-subtitle">企业云原生大单体业务管理系统 · 9 大业务模块</text>
    </view>

    <!-- 注册表单 -->
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
      <view class="form-item">
        <input
          v-model="form.confirmPassword"
          class="form-input"
          password
          placeholder="请再次输入密码"
          placeholder-class="placeholder"
        />
      </view>
      <button class="register-btn" :loading="loading" @click="handleRegister">注 册</button>
      <view class="login-link" @click="goLogin">已有账号？返回登录</view>
    </view>
  </view>
</template>

<script>
import request from '@/utils/request.js'

export default {
  data() {
    return {
      form: {
        username: '',
        password: '',
        confirmPassword: '',
      },
      loading: false,
    }
  },
  methods: {
    // 返回登录页
    goLogin() {
      uni.navigateBack({
        fail: () => {
          uni.redirectTo({ url: '/pages/login/login' })
        },
      })
    },
    // 注册处理
    async handleRegister() {
      // 简单非空校验
      if (!this.form.username) {
        uni.showToast({ title: '请输入用户名', icon: 'none' })
        return
      }
      if (!this.form.password) {
        uni.showToast({ title: '请输入密码', icon: 'none' })
        return
      }
      if (!this.form.confirmPassword) {
        uni.showToast({ title: '请再次输入密码', icon: 'none' })
        return
      }
      // 两次密码一致性校验
      if (this.form.password !== this.form.confirmPassword) {
        uni.showToast({ title: '两次输入的密码不一致', icon: 'none' })
        return
      }
      this.loading = true
      try {
        await request.post('/auth/register', {
          username: this.form.username,
          password: this.form.password,
        })
        uni.showToast({ title: '注册成功', icon: 'success' })
        // 注册成功后跳转登录页
        setTimeout(() => {
          uni.redirectTo({ url: '/pages/login/login' })
        }, 800)
      } catch (e) {
        // 错误已由 request.js 弹窗
        console.error('注册失败', e)
      } finally {
        this.loading = false
      }
    },
  },
}
</script>

<style>
.register-page {
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
.register-btn {
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
.register-btn::after {
  border: none;
}
.login-link {
  text-align: center;
  color: #1677ff;
  font-size: 26rpx;
  margin-top: 32rpx;
}
</style>
