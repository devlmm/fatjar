<!--
  ====================================================================================
  fatjar 管理后台 - 根组件 App.vue
  ------------------------------------------------------------------------------------
  职责：
    1. 提供 <router-view> 出口，渲染当前路由对应的页面
    2. 全局过渡动画（路由切换淡入淡出）
    3. ElConfigProvider 全局配置（备用：可在 main.js 已配中文，这里保留可扩展）
  ====================================================================================
-->
<template>
  <!-- Vue 内置 transition：路由切换淡入淡出动画 -->
  <router-view v-slot="{ Component }">
    <transition name="fade" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script setup>
// 根组件无业务逻辑，仅作路由出口
// 后续可在 setup 中处理全局键盘事件、窗口尺寸监听等
</script>

<style scoped>
/* 路由切换淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
