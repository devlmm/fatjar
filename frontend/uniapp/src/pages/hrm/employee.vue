<!--
  ====================================================================================
  fatjar uniapp - 员工管理 src/pages/hrm/employee.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 员工列表展示（工号/姓名/职位/电话/状态）
    2. 按姓名 + 状态搜索
    3. 分页加载
    4. 调用接口 GET /hrm/employee/page
  ====================================================================================
-->
<template>
  <view class="page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input v-model="query.name" placeholder="姓名" class="search-input" placeholder-class="placeholder" />
      <input v-model="query.status" placeholder="状态(0/1)" class="search-input" placeholder-class="placeholder" />
      <button @click="handleSearch" size="mini" class="search-btn">搜索</button>
    </view>

    <!-- 列表 -->
    <view class="list">
      <view class="list-item" v-for="item in list" :key="item.id">
        <view class="item-main">
          <text class="item-title">{{ item.empNo }} · {{ item.name }}</text>
          <text class="item-sub">职位：{{ item.position }} | 电话：{{ item.phone }}</text>
        </view>
        <text class="item-status" :class="'status-' + item.status">{{ statusText(item.status) }}</text>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty" v-if="list.length === 0">暂无数据</view>

    <!-- 分页 -->
    <view class="pagination">
      <button @click="prevPage" :disabled="query.pageNum <= 1" size="mini">上一页</button>
      <text class="page-info">第 {{ query.pageNum }} 页</text>
      <button @click="nextPage" :disabled="list.length < query.pageSize" size="mini">下一页</button>
    </view>
  </view>
</template>

<script>
import request from '@/utils/request.js'

export default {
  data() {
    return {
      query: { name: '', status: '', pageNum: 1, pageSize: 10 },
      list: [],
    }
  },
  onLoad() {
    this.loadData()
  },
  methods: {
    // 加载员工分页数据
    async loadData() {
      try {
        const res = await request.get('/hrm/employee/page', { data: this.query })
        this.list = res.data?.records || []
      } catch (e) {
        console.error('加载员工列表失败', e)
      }
    },
    // 搜索（重置到第一页）
    handleSearch() {
      this.query.pageNum = 1
      this.loadData()
    },
    prevPage() {
      if (this.query.pageNum > 1) {
        this.query.pageNum--
        this.loadData()
      }
    },
    nextPage() {
      this.query.pageNum++
      this.loadData()
    },
    // 状态：0=在职，1=离职
    statusText(s) {
      return ['在职', '离职'][s] || '未知'
    },
  },
}
</script>

<style>
.page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 16rpx 24rpx;
  gap: 16rpx;
}
.search-input {
  flex: 1;
  height: 64rpx;
  border: 2rpx solid #e5e7eb;
  border-radius: 8rpx;
  padding: 0 16rpx;
  font-size: 26rpx;
}
.placeholder {
  color: #9ca3af;
}
.search-btn {
  background: #1677ff;
  color: #fff;
  font-size: 24rpx;
}
.search-btn::after {
  border: none;
}

/* 列表 */
.list {
  padding: 16rpx 24rpx;
}
.list-item {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 24rpx;
  margin-bottom: 16rpx;
  border-radius: 12rpx;
}
.item-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.item-title {
  font-size: 30rpx;
  color: #1f2937;
  font-weight: 600;
  margin-bottom: 8rpx;
}
.item-sub {
  font-size: 24rpx;
  color: #6b7280;
}
.item-status {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  margin-left: 16rpx;
}
.status-0 {
  background: #f6ffed;
  color: #52c41a;
}
.status-1 {
  background: #fff1f0;
  color: #ff4d4f;
}

/* 空状态 */
.empty {
  text-align: center;
  color: #9ca3af;
  font-size: 26rpx;
  padding: 80rpx 0;
}

/* 分页 */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24rpx;
  gap: 24rpx;
}
.page-info {
  font-size: 26rpx;
  color: #4b5563;
}
</style>
