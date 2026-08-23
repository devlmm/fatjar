<!--
  ====================================================================================
  fatjar 管理后台 - 注册页 src/views/register/index.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 用户名 + 密码 + 确认密码表单（Element Plus Form 校验）
    2. 调用 /auth/register 接口
    3. 注册成功后跳转到登录页
  布局：左侧品牌展示 + 右侧注册表单（响应式）
  ====================================================================================
-->
<template>
  <div class="register-container">
    <!-- 左侧：品牌视觉区（移动端隐藏） -->
    <div class="register-banner">
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

    <!-- 右侧：注册表单 -->
    <div class="register-form-wrapper">
      <div class="register-form-card">
        <h2 class="form-title">注册账号</h2>
        <p class="form-subtitle">fatjar 管理后台</p>

        <!-- 注册表单：ref 用于校验，model 绑定数据 -->
        <el-form
          ref="registerFormRef"
          :model="form"
          :rules="rules"
          size="large"
          @keyup.enter="handleRegister"
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
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              :prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              class="register-btn"
              :loading="loading"
              @click="handleRegister"
            >
              注 册
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-tip">
          已有账号？
          <el-link type="primary" :underline="false" @click="goLogin">返回登录</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

// ---------- 表单数据 ----------
const registerFormRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
})

// ---------- 自定义校验：确认密码 ----------
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// ---------- 表单校验规则 ----------
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 32, message: '用户名长度 3-32 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

// ---------- 注册处理 ----------
const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
  } catch (e) {
    return
  }
  loading.value = true
  try {
    await request.post('/auth/register', {
      username: form.username,
      password: form.password,
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    // 错误已由 request.js 弹出，这里静默
    console.error('注册失败', e)
  } finally {
    loading.value = false
  }
}

// ---------- 返回登录 ----------
const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
/* 注册容器：左右分栏布局 */
.register-container {
  display: flex;
  height: 100vh;
  background: #f0f2f5;
}

/* 左侧品牌区：渐变背景 */
.register-banner {
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
.register-form-wrapper {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}
.register-form-card {
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
.register-btn {
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
  .register-banner {
    display: none;
  }
  .register-form-wrapper {
    width: 100%;
    padding: 24px;
  }
}
</style>
