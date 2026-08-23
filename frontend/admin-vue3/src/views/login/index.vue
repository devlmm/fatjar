<!--
  ====================================================================================
  fatjar 管理后台 - 登录页 src/views/login/index.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 用户名 + 密码表单（Element Plus Form 校验）
    2. 调用 /auth/login 接口（通过 Pinia user store）
    3. 登录成功后跳转到 redirect 参数或默认 /dashboard
  布局：左侧品牌展示 + 右侧登录表单（响应式）
  ====================================================================================
-->
<template>
  <div class="login-container">
    <!-- 左侧：品牌视觉区（移动端隐藏） -->
    <div class="login-banner">
      <div class="banner-content">
        <div class="brand-logo">
          <span class="logo-mark">F</span>
          <span>fatjar</span>
        </div>
        <h1>企业云原生<br>大单体业务管理系统</h1>
        <p>一个 fatjar 搞定 auth / fico / scm / mes / hrm / crm / pm / bi / oa 九大业务域</p>
        <div class="banner-features">
          <div class="feature-item"><el-icon><Check/></el-icon> Spring Boot 3.2</div>
          <div class="feature-item"><el-icon><Check/></el-icon> Spring Cloud Alibaba</div>
          <div class="feature-item"><el-icon><Check/></el-icon> Vue 3 + Element Plus</div>
          <div class="feature-item"><el-icon><Check/></el-icon> 7 个自研 Starter</div>
        </div>
      </div>
    </div>

    <!-- 右侧：登录表单 -->
    <div class="login-form-wrapper">
      <div class="login-form-card">
        <h2 class="form-title">欢迎登录</h2>
        <p class="form-subtitle">fatjar 管理后台</p>

        <!-- 登录表单：ref 用于校验，model 绑定数据 -->
        <el-form
          ref="loginFormRef"
          :model="form"
          :rules="rules"
          size="large"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>
          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="form.remember">记住我</el-checkbox>
              <el-link type="primary" :underline="false">忘记密码？</el-link>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-tip">
          演示账号：admin / admin123
        </div>
        <div class="form-tip">
          还没有账号？
          <el-link type="primary" :underline="false" @click="goRegister">注册账号</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// ---------- 表单数据 ----------
const loginFormRef = ref()
const loading = ref(false)
const form = reactive({
  username: 'admin',
  password: 'admin123',
  remember: false,
})

// ---------- 表单校验规则 ----------
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
}

// ---------- 登录处理 ----------
const handleLogin = async () => {
  // 先做表单校验
  try {
    await loginFormRef.value.validate()
  } catch (e) {
    return
  }
  loading.value = true
  try {
    // 调用 store 的 login action
    await userStore.login({ username: form.username, password: form.password })
    ElMessage.success('登录成功')
    // 获取用户信息（用于菜单权限）
    await userStore.fetchInfo()
    // 跳转 redirect 或默认 dashboard
    const redirect = route.query.redirect || '/dashboard'
    router.push(redirect)
  } catch (e) {
    // 错误已由 request.js 弹出，这里静默
    console.error('登录失败', e)
  } finally {
    loading.value = false
  }
}

// ---------- 跳转注册页 ----------
const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
/* 登录容器：左右分栏布局 */
.login-container {
  display: flex;
  height: 100vh;
  background: #f0f2f5;
}

/* 左侧品牌区：渐变背景 */
.login-banner {
  flex: 1;
  background: linear-gradient(135deg, #1677ff 0%, #0958d9 50%, #5b21b6 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}
.banner-content {
  max-width: 480px;
}
.brand-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 48px;
}
.logo-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 6px;
}
.banner-content h1 {
  font-size: 40px;
  line-height: 1.3;
  margin-bottom: 16px;
}
.banner-content > p {
  opacity: 0.92;
  font-size: 16px;
  margin-bottom: 40px;
}
.banner-features {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.1);
  padding: 10px 16px;
  border-radius: 6px;
  font-size: 14px;
}

/* 右侧表单区 */
.login-form-wrapper {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}
.login-form-card {
  width: 100%;
  max-width: 360px;
}
.form-title {
  font-size: 28px;
  color: #1f2937;
  margin-bottom: 8px;
}
.form-subtitle {
  color: #6b7280;
  margin-bottom: 32px;
}
.form-options {
  display: flex;
  justify-content: space-between;
  width: 100%;
}
.login-btn {
  width: 100%;
}
.form-tip {
  text-align: center;
  color: #9ca3af;
  font-size: 13px;
  margin-top: 16px;
}

/* ---------- 响应式：< 768px 隐藏左侧品牌区 ---------- */
@media (max-width: 768px) {
  .login-banner {
    display: none;
  }
  .login-form-wrapper {
    width: 100%;
    padding: 24px;
  }
}
</style>
