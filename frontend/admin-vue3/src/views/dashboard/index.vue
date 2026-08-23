<!--
  ====================================================================================
  fatjar 管理后台 - 仪表盘 src/views/dashboard/index.vue
  ------------------------------------------------------------------------------------
  说明：占位页面，展示关键指标卡片 + 欢迎信息
  后续可对接后端 /dashboard/stats 接口
  ====================================================================================
-->
<template>
  <div class="dashboard">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-content">
        <el-avatar :size="56" :src="userStore.avatar" />
        <div class="welcome-text">
          <h2>{{ greeting }}，{{ userStore.nickname }}</h2>
          <p>欢迎使用 fatjar 企业云原生大单体业务管理系统</p>
        </div>
      </div>
    </el-card>

    <!-- 指标卡片：4 列 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="12" :sm="12" :md="6" v-for="stat in stats" :key="stat.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" :style="{ color: stat.color }">
              <component :is="stat.icon" />
            </el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 占位提示卡片 -->
    <el-card shadow="never" class="placeholder-card">
      <template #header>
        <span>系统概览</span>
      </template>
      <el-empty description="仪表盘数据接入中，请期待后续版本">
        <el-button type="primary" @click="$router.push('/sys/user')">前往用户管理</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 根据时间生成问候语
const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '凌晨好'
  if (h < 9) return '早上好'
  if (h < 12) return '上午好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

// 指标数据（占位，后续对接后端）
const stats = [
  { title: '今日访问', value: '1,286', icon: 'View', color: '#1677ff' },
  { title: '订单数', value: '328', icon: 'ShoppingCart', color: '#52c41a' },
  { title: '新增用户', value: '64', icon: 'UserFilled', color: '#faad14' },
  { title: '待审批', value: '12', icon: 'Bell', color: '#ff4d4f' },
]
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 欢迎卡片 */
.welcome-card {
  background: linear-gradient(135deg, #e6f4ff 0%, #f0f7ff 100%);
  border: none;
}
.welcome-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.welcome-text h2 {
  margin: 0 0 4px 0;
  font-size: 20px;
  color: #1f2937;
}
.welcome-text p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

/* 指标卡片 */
.stats-row {
  margin-bottom: 0 !important;
}
.stat-card {
  margin-bottom: 16px;
}
.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  font-size: 40px;
}
.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}
.stat-title {
  color: #6b7280;
  font-size: 13px;
}

.placeholder-card {
  border: none;
}
</style>
