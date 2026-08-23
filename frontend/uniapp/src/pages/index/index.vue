<!--
  ====================================================================================
  fatjar uniapp - 首页 src/pages/index/index.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 顶部欢迎卡片（已登录时显示用户昵称）
    2. 八大业务模块宫格入口（FICO/SCM/MES/HRM/CRM/PM/BI/OA）
  ====================================================================================
-->
<template>
  <view class="home-page">
    <!-- 顶部欢迎卡片 -->
    <view class="header">
      <view class="header-text">
        <text class="title">fatjar 业务管理</text>
        <text class="subtitle">企业云原生大单体业务管理系统</text>
        <text v-if="nickname" class="welcome">您好，{{ nickname }} 👋</text>
      </view>
      <view class="header-logo">F</view>
    </view>

    <!-- 业务模块宫格 -->
    <view class="menu-grid">
      <view
        v-for="item in menus"
        :key="item.path"
        class="menu-item"
        @click="navigate(item.path)"
      >
        <view class="menu-icon" :style="{ backgroundColor: item.color }">
          <text class="menu-emoji">{{ item.icon }}</text>
        </view>
        <text class="menu-name">{{ item.name }}</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      // 当前用户昵称（未登录时为空）
      nickname: '',
      // 八大业务模块入口配置
      menus: [
        { name: '财务会计', path: '/pages/fico/voucher', icon: '💰', color: '#1677ff' },
        { name: '供应链', path: '/pages/scm/purchaseOrder', icon: '📦', color: '#52c41a' },
        { name: '制造执行', path: '/pages/mes/workOrder', icon: '🏭', color: '#fa8c16' },
        { name: '人力资源', path: '/pages/hrm/employee', icon: '👤', color: '#eb2f96' },
        { name: '客户关系', path: '/pages/crm/customer', icon: '🤝', color: '#722ed1' },
        { name: '项目管理', path: '/pages/pm/project', icon: '📋', color: '#13c2c2' },
        { name: '商业智能', path: '/pages/bi/report', icon: '📊', color: '#faad14' },
        { name: '办公审批', path: '/pages/oa/approval', icon: '📝', color: '#ff4d4f' },
      ],
    }
  },
  onShow() {
    // 每次进入首页时，刷新用户昵称
    this.loadNickname()
  },
  methods: {
    // 从本地存储读取用户昵称
    loadNickname() {
      try {
        const raw = uni.getStorageSync('fatjar_user_info')
        if (raw) {
          const info = JSON.parse(raw)
          this.nickname = info.nickname || info.username || ''
        } else {
          this.nickname = ''
        }
      } catch (e) {
        this.nickname = ''
      }
    },
    // 跳转到对应业务模块页面
    navigate(path) {
      uni.navigateTo({ url: path })
    },
  },
}
</script>

<style>
.home-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

/* 顶部欢迎卡片 */
.header {
  margin: 24rpx;
  padding: 40rpx;
  background: linear-gradient(135deg, #1677ff 0%, #0958d9 100%);
  border-radius: 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-text {
  display: flex;
  flex-direction: column;
}
.title {
  color: #fff;
  font-size: 36rpx;
  font-weight: 700;
  margin-bottom: 8rpx;
}
.subtitle {
  color: rgba(255, 255, 255, 0.85);
  font-size: 24rpx;
  margin-bottom: 16rpx;
}
.welcome {
  color: #fff;
  font-size: 26rpx;
}
.header-logo {
  width: 80rpx;
  height: 80rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16rpx;
  color: #fff;
  font-size: 44rpx;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 业务模块宫格 */
.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx;
  padding: 24rpx;
}
.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 0;
  background: #fff;
  border-radius: 12rpx;
}
.menu-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
}
.menu-emoji {
  font-size: 40rpx;
}
.menu-name {
  font-size: 24rpx;
  color: #4b5563;
}
</style>
