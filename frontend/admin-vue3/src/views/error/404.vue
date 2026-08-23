<!--
  ====================================================================================
  fatjar 管理后台 - 404 页面 src/views/error/404.vue
  ------------------------------------------------------------------------------------
  说明：兜底 404 页面，未匹配路由时显示
  ====================================================================================
-->
<template>
  <div class="not-found">
    <div class="content">
      <div class="code">404</div>
      <p class="title">页面不存在</p>
      <p class="desc">抱歉，您访问的页面不存在或已被删除</p>
      <el-button type="primary" @click="$router.push('/dashboard')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
// 静态页面，无业务逻辑
</script>

<style scoped>
.not-found {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
}
.content {
  text-align: center;
}
.code {
  font-size: 120px;
  font-weight: 700;
  color: #1677ff;
  line-height: 1;
  margin-bottom: 16px;
}
.title {
  font-size: 24px;
  color: #1f2937;
  margin-bottom: 8px;
}
.desc {
  color: #6b7280;
  margin-bottom: 24px;
}
</style>
