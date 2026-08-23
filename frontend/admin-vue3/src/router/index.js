/*  ====================================================================================
    fatjar 管理后台 - 路由配置 src/router/index.js
    ------------------------------------------------------------------------------------
    路由规划（与菜单一一对应）：
      公共路由（无需登录）：
        /login                登录页
        /register             注册页
      受保护路由（需登录，使用 Layout 布局）：
        /dashboard            仪表盘
        /sys/user             系统管理-用户
        /sys/role             系统管理-角色
        /sys/menu             系统管理-菜单
        /fico/voucher         FICO-凭证管理
        /scm/purchase-order   SCM-采购订单
        /mes/work-order       MES-工单管理
        /hrm/employee         HRM-员工管理
        /crm/customer        CRM-客户管理
        /pm/project           PM-项目管理
        /bi/report            BI-报表管理
        /oa/approval          OA-审批管理
    导航守卫：
      beforeEach：检查 token，未登录跳转 /login
  ==================================================================================== */

import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// ---------- 路由表 ----------
const routes = [
  // ---- 公共路由：无需登录 ----
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册', requiresAuth: false },
  },

  // ---- 受保护路由：使用 Layout 布局 ----
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      // 仪表盘
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' },
      },
      // ---- 系统管理 ----
      {
        path: 'sys/user',
        name: 'SysUser',
        component: () => import('@/views/sys/user.vue'),
        meta: { title: '用户管理', icon: 'User', group: '系统管理' },
      },
      {
        path: 'sys/role',
        name: 'SysRole',
        component: () => import('@/views/sys/role.vue'),
        meta: { title: '角色管理', icon: 'UserFilled', group: '系统管理' },
      },
      {
        path: 'sys/menu',
        name: 'SysMenu',
        component: () => import('@/views/sys/menu.vue'),
        meta: { title: '菜单管理', icon: 'Menu', group: '系统管理' },
      },
      // ---- FICO 财务会计 ----
      {
        path: 'fico/voucher',
        name: 'FicoVoucher',
        component: () => import('@/views/fico/voucher.vue'),
        meta: { title: '凭证管理', icon: 'Tickets', group: '财务会计' },
      },
      // ---- SCM 供应链管理 ----
      {
        path: 'scm/purchase-order',
        name: 'ScmPurchaseOrder',
        component: () => import('@/views/scm/purchaseOrder.vue'),
        meta: { title: '采购订单', icon: 'ShoppingCart', group: '供应链管理' },
      },
      // ---- MES 制造执行 ----
      {
        path: 'mes/work-order',
        name: 'MesWorkOrder',
        component: () => import('@/views/mes/workOrder.vue'),
        meta: { title: '工单管理', icon: 'Tools', group: '制造执行' },
      },
      // ---- HRM 人力资源 ----
      {
        path: 'hrm/employee',
        name: 'HrmEmployee',
        component: () => import('@/views/hrm/employee.vue'),
        meta: { title: '员工管理', icon: 'UserFilled', group: '人力资源' },
      },
      // ---- CRM 客户关系 ----
      {
        path: 'crm/customer',
        name: 'CrmCustomer',
        component: () => import('@/views/crm/customer.vue'),
        meta: { title: '客户管理', icon: 'Avatar', group: '客户关系' },
      },
      // ---- PM 项目管理 ----
      {
        path: 'pm/project',
        name: 'PmProject',
        component: () => import('@/views/pm/project.vue'),
        meta: { title: '项目管理', icon: 'Briefcase', group: '项目管理' },
      },
      // ---- BI 商业智能 ----
      {
        path: 'bi/report',
        name: 'BiReport',
        component: () => import('@/views/bi/report.vue'),
        meta: { title: '报表管理', icon: 'DataAnalysis', group: '商业智能' },
      },
      // ---- OA 办公自动化 ----
      {
        path: 'oa/approval',
        name: 'OaApproval',
        component: () => import('@/views/oa/approval.vue'),
        meta: { title: '审批管理', icon: 'Document', group: '办公自动化' },
      },
    ],
  },

  // ---- 404 兜底 ----
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在', requiresAuth: false },
  },
]

// ---------- 创建路由实例 ----------
const router = createRouter({
  // HTML5 History 模式（无 # 号）
  history: createWebHistory(),
  routes,
  // 切换路由后滚动到顶部
  scrollBehavior: () => ({ top: 0 }),
})

// ---------- 全局前置守卫：登录态校验 ----------
router.beforeEach((to, from, next) => {
  // 设置浏览器标签页标题
  document.title = to.meta.title ? `${to.meta.title} - fatjar 管理后台` : 'fatjar 管理后台'

  // 获取用户 store（Pinia 在 main.js 已挂载，这里可直接 use）
  const userStore = useUserStore()

  // 需要鉴权的页面，且未登录 → 跳转登录页
  if (to.meta.requiresAuth !== false && !userStore.token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  }
  // 已登录又访问登录页 → 跳转首页
  else if (to.path === '/login' && userStore.token) {
    next('/dashboard')
  }
  // 其他情况放行
  else {
    next()
  }
})

export default router
