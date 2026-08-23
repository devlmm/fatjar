<!--
  ====================================================================================
  fatjar uniapp - 我的 src/pages/mine/mine.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 用户头像 + 昵称 + 角色信息
    2. 功能列表（个人资料 / 修改密码 / 关于 / 设置）
    3. 退出登录按钮
  ====================================================================================
-->
<template>
  <view class="mine-page">
    <!-- 用户信息头部 -->
    <view class="user-header">
      <view class="user-info">
        <view class="user-avatar">{{ avatarText }}</view>
        <view class="user-detail">
          <text class="user-name">{{ userInfo.nickname || userInfo.username || '游客' }}</text>
          <text class="user-role">{{ userInfo.roles ? userInfo.roles.join(' / ') : '未登录' }}</text>
        </view>
      </view>
    </view>

    <!-- 数据统计 -->
    <view class="stats-grid">
      <view class="stat-item">
        <text class="stat-num">12</text>
        <text class="stat-label">待办</text>
      </view>
      <view class="stat-item">
        <text class="stat-num">3</text>
        <text class="stat-label">通知</text>
      </view>
      <view class="stat-item">
        <text class="stat-num">96</text>
        <text class="stat-label">收藏</text>
      </view>
    </view>

    <!-- 功能列表 -->
    <view class="menu-list">
      <view
        v-for="(m, i) in menus"
        :key="i"
        class="menu-item"
        @click="handleMenu(m)"
      >
        <text class="menu-emoji">{{ m.emoji }}</text>
        <text class="menu-text">{{ m.text }}</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <!-- 退出登录按钮 -->
    <button class="logout-btn" @click="handleLogout">退出登录</button>

    <!-- 版本信息 -->
    <view class="version">fatjar v1.0.0</view>
  </view>
</template>

<script>
import request from '@/utils/request.js'

export default {
  data() {
    return {
      userInfo: {},
      menus: [
        { emoji: '👤', text: '个人资料', action: 'profile' },
        { emoji: '🔑', text: '修改密码', action: 'password' },
        { emoji: '🔔', text: '消息通知', action: 'notification' },
        { emoji: '⚙️', text: '系统设置', action: 'settings' },
        { emoji: '📖', text: '关于我们', action: 'about' },
      ],
    }
  },
  computed: {
    avatarText() {
      const name = this.userInfo.nickname || this.userInfo.username || 'U'
      return name.charAt(0).toUpperCase()
    },
  },
  onShow() {
    // 每次显示时刷新用户信息
    this.loadUserInfo()
  },
  methods: {
    // 加载用户信息（从本地存储读取）
    loadUserInfo() {
      try {
        const raw = uni.getStorageSync('fatjar_user_info')
        if (raw) {
          this.userInfo = JSON.parse(raw)
        }
      } catch (e) {
        this.userInfo = {}
      }
    },
    // 菜单点击
    handleMenu(m) {
      uni.showToast({ title: `${m.text}（占位）`, icon: 'none' })
    },
    // 退出登录
    async handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await request.post('/auth/logout', {}, { hideError: true })
            } catch (e) {
              // 即使后端调用失败，也前端清空
            }
            request.setToken('')
            uni.removeStorageSync('fatjar_user_info')
            this.userInfo = {}
            uni.showToast({ title: '已退出登录', icon: 'success' })
            setTimeout(() => {
              uni.reLaunch({ url: '/pages/login/login' })
            }, 800)
          }
        },
      })
    },
  },
}
</script>

<style>
.mine-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 60rpx;
}

/* 用户信息头部 */
.user-header {
  background: linear-gradient(135deg, #1677ff 0%, #0958d9 100%);
  padding: 60rpx 40rpx 80rpx;
}
.user-info {
  display: flex;
  align-items: center;
}
.user-avatar {
  width: 100rpx;
  height: 100rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  color: #fff;
  font-size: 48rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}
.user-detail {
  display: flex;
  flex-direction: column;
}
.user-name {
  color: #fff;
  font-size: 36rpx;
  font-weight: 600;
  margin-bottom: 8rpx;
}
.user-role {
  color: rgba(255, 255, 255, 0.85);
  font-size: 24rpx;
}

/* 数据统计 */
.stats-grid {
  display: flex;
  background: #fff;
  margin: -40rpx 24rpx 0;
  border-radius: 12rpx;
  padding: 32rpx 0;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-right: 2rpx solid #f3f4f6;
}
.stat-item:last-child {
  border-right: none;
}
.stat-num {
  font-size: 40rpx;
  font-weight: 700;
  color: #1677ff;
  margin-bottom: 8rpx;
}
.stat-label {
  font-size: 24rpx;
  color: #6b7280;
}

/* 功能列表 */
.menu-list {
  background: #fff;
  margin: 24rpx;
  border-radius: 12rpx;
  overflow: hidden;
}
.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx 24rpx;
  border-bottom: 2rpx solid #f3f4f6;
}
.menu-item:last-child {
  border-bottom: none;
}
.menu-emoji {
  font-size: 36rpx;
  margin-right: 24rpx;
}
.menu-text {
  flex: 1;
  font-size: 30rpx;
  color: #1f2937;
}
.menu-arrow {
  font-size: 36rpx;
  color: #d1d5db;
}

/* 退出登录按钮 */
.logout-btn {
  margin: 40rpx 24rpx 0;
  background: #fff;
  color: #ff4d4f;
  font-size: 30rpx;
  border-radius: 12rpx;
  height: 88rpx;
  line-height: 88rpx;
}
.logout-btn::after {
  border: none;
}

/* 版本信息 */
.version {
  text-align: center;
  color: #9ca3af;
  font-size: 24rpx;
  margin-top: 40rpx;
}
</style>
