<!--
  ====================================================================================
  fatjar 管理后台 - 后台布局 src/views/layout/index.vue
  ------------------------------------------------------------------------------------
  布局结构（Element Plus Container 组件）：
    ┌─────────────────────────────────────────────────┐
    │ Header（顶部栏：折叠按钮 + 面包屑 + 用户菜单）  │
    ├────────────┬────────────────────────────────────┤
    │            │                                    │
    │  Aside     │       Main                         │
    │  侧边菜单  │  <router-view> 内容出口             │
    │            │                                    │
    └────────────┴────────────────────────────────────┘
  功能：
    1. 侧边菜单：根据路由 meta.group 分组渲染
    2. 菜单折叠/展开（Aside 宽度变化）
    3. 顶部用户下拉：个人中心 / 退出登录
    4. 路由出口
  ====================================================================================
-->
<template>
  <el-container class="layout-container">
    <!-- ============ 顶部栏 ============ -->
    <el-header class="layout-header">
      <div class="header-left">
        <!-- 折叠按钮 -->
        <el-icon class="collapse-btn" @click="collapsed = !collapsed">
          <Fold v-if="!collapsed" />
          <Expand v-else />
        </el-icon>
        <!-- 面包屑：当前路由路径 -->
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item v-if="route.meta.group">{{ route.meta.group }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="header-right">
        <!-- 全屏按钮 -->
        <el-icon class="action-icon" @click="toggleFullscreen"><FullScreen /></el-icon>
        <!-- 用户下拉菜单 -->
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="32" :src="userStore.avatar" />
            <span class="username">{{ userStore.nickname }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon> 个人中心
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <el-icon><SwitchButton /></el-icon> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container class="layout-body">
      <!-- ============ 侧边菜单 ============ -->
      <el-aside :width="collapsed ? '64px' : '220px'" class="layout-aside">
        <!-- Logo 区 -->
        <div class="logo-bar">
          <span class="logo-mark">F</span>
          <span v-show="!collapsed" class="logo-text">fatjar</span>
        </div>
        <!-- 菜单：根据路由表自动渲染，按 meta.group 分组 -->
        <el-menu
          :default-active="route.path"
          :collapse="collapsed"
          :collapse-transition="false"
          router
          background-color="#001529"
          text-color="#a6adb4"
          active-text-color="#fff"
        >
          <!-- 仪表盘：单独一项 -->
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>

          <!-- 按 group 分组渲染 -->
          <el-sub-menu
            v-for="group in groupedMenus"
            :key="group.name"
            :index="group.name"
          >
            <template #title>
              <el-icon><Grid /></el-icon>
              <span>{{ group.name }}</span>
            </template>
            <el-menu-item
              v-for="item in group.items"
              :key="item.path"
              :index="item.path"
            >
              <el-icon><component :is="item.meta.icon" /></el-icon>
              <template #title>{{ item.meta.title }}</template>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <!-- ============ 内容区 ============ -->
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const route = useRoute()
const routerInstance = useRouter()
const userStore = useUserStore()

// ---------- 菜单折叠状态 ----------
const collapsed = ref(false)

// ---------- 按 meta.group 分组菜单 ----------
const groupedMenus = computed(() => {
  // 取出所有有 group 标识的路由（layout 子路由中非 dashboard 的）
  const children = router.options.routes
    .find((r) => r.path === '/')
    ?.children?.filter((c) => c.meta?.group) || []
  // 按 group 名称聚合
  const map = new Map()
  children.forEach((c) => {
    const g = c.meta.group
    if (!map.has(g)) map.set(g, [])
    map.get(g).push({ path: '/' + c.path, meta: c.meta })
  })
  return Array.from(map, ([name, items]) => ({ name, items }))
})

// ---------- 顶部用户菜单事件 ----------
const handleCommand = async (cmd) => {
  if (cmd === 'profile') {
    ElMessage.info('个人中心：占位，待开发')
  } else if (cmd === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
      await userStore.logout()
      ElMessage.success('已退出登录')
      routerInstance.push('/login')
    } catch (e) {
      // 用户取消
    }
  }
}

// ---------- 全屏切换 ----------
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen?.()
  } else {
    document.exitFullscreen?.()
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

/* ---------- 顶部栏 ---------- */
.layout-header {
  height: var(--fatjar-header-height, 56px);
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #1f2937;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.action-icon {
  font-size: 18px;
  cursor: pointer;
  color: #4b5563;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.username {
  font-size: 14px;
  color: #1f2937;
}

/* ---------- 侧边栏 ---------- */
.layout-body {
  height: calc(100vh - var(--fatjar-header-height, 56px));
}
.layout-aside {
  background: #001529;
  transition: width 0.3s;
  overflow-x: hidden;
}
.logo-bar {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.logo-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #1677ff, #5b21b6);
  border-radius: 4px;
}
/* 覆盖 Element Plus Menu 在深色背景下样式 */
.layout-aside .el-menu {
  border-right: none;
}

/* ---------- 内容区 ---------- */
.layout-main {
  background: #f0f2f5;
  padding: 16px;
  overflow-y: auto;
}

/* ---------- 路由切换动画 ---------- */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
