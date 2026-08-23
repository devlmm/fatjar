/*  ====================================================================================
    fatjar 管理后台 - 用户 Pinia Store src/stores/user.js
    ------------------------------------------------------------------------------------
    状态字段：
      token      : JWT token（持久化到 localStorage）
      userInfo   : 当前登录用户信息（id/username/nickname/avatar/roles）
    Actions：
      login      : 调用 /auth/login 接口，登录成功后保存 token
      fetchInfo  : 调用 /auth/info 获取当前用户信息
      logout     : 清空状态 + 跳转登录页
  ==================================================================================== */

import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useUserStore = defineStore('user', {
  // ---------- state ----------
  state: () => ({
    // token：优先从 localStorage 读取，避免刷新丢失登录态
    token: localStorage.getItem('fatjar_token') || '',
    // 用户信息：null 表示尚未加载
    userInfo: JSON.parse(localStorage.getItem('fatjar_user_info') || 'null'),
  }),

  // ---------- getters ----------
  getters: {
    // 是否已登录
    isLogin: (state) => !!state.token,
    // 用户昵称（兜底 username）
    nickname: (state) => state.userInfo?.nickname || state.userInfo?.username || '游客',
    // 用户头像（兜底默认头像）
    avatar: (state) =>
      state.userInfo?.avatar ||
      'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    // 角色码列表
    roles: (state) => state.userInfo?.roles || [],
  },

  // ---------- actions ----------
  actions: {
    /**
     * 登录
     * @param {Object} payload { username, password }
     * @returns {Promise<void>}
     */
    async login(payload) {
      // 调用后端登录接口（/api 前缀由 axios baseURL 处理）
      const res = await request.post('/auth/login', payload)
      // 假设后端返回结构：{ code: 200, data: { token: 'xxx', ... } }
      if (res.code === 200) {
        this.token = res.data.token
        // 持久化到 localStorage
        localStorage.setItem('fatjar_token', this.token)
        return res
      }
      // 业务异常：抛错让调用方处理（ElMessage）
      throw new Error(res.msg || '登录失败')
    },

    /**
     * 获取当前用户信息（登录后立即调用）
     */
    async fetchInfo() {
      if (!this.token) return
      // 主路径 /auth/userInfo（AuthController 已增加 /auth/info 别名兜底，避免版本不一致时 404）
      const res = await request.get('/auth/userInfo')
      if (res.code === 200) {
        this.userInfo = res.data
        localStorage.setItem('fatjar_user_info', JSON.stringify(this.userInfo))
      }
      return res
    },

    /**
     * 退出登录
     */
    async logout() {
      // 通知后端使 token 失效（即使失败也前端清空）
      try {
        await request.post('/auth/logout')
      } catch (e) {
        console.warn('退出接口调用失败，前端强制清空', e)
      }
      // 清空本地状态
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('fatjar_token')
      localStorage.removeItem('fatjar_user_info')
    },
  },
})
