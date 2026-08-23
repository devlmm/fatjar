<!--
  ====================================================================================
  fatjar uniapp - 报表管理 src/pages/bi/report.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 报表列表展示（报表名/报表类型/数据源/状态）
    2. 按报表名 + 状态搜索
    3. 分页加载
    4. 调用接口 GET /bi/report/page
  ====================================================================================
-->
<template>
  <view class="page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input v-model="query.reportName" placeholder="报表名" class="search-input" placeholder-class="placeholder" />
      <input v-model="query.status" placeholder="状态(0/1)" class="search-input" placeholder-class="placeholder" />
      <button @click="handleSearch" size="mini" class="search-btn">搜索</button>
    </view>

    <!-- 列表 -->
    <view class="list">
      <view class="list-item" v-for="item in list" :key="item.id">
        <view class="item-main">
          <text class="item-title">{{ item.reportName }}</text>
          <text class="item-sub">类型：{{ item.reportType }} | 数据源：{{ item.dataSource }}</text>
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
      query: { reportName: '', status: '', pageNum: 1, pageSize: 10 },
      list: [],
    }
  },
  onLoad() {
    this.loadData()
  },
  methods: {
    // 加载报表分页数据
    async loadData() {
      try {
        const res = await request.get('/bi/report/page', { data: this.query })
        this.list = res.data?.records || []
      } catch (e) {
        console.error('加载报表列表失败', e)
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
    // 状态：0=草稿，1=已发布
    statusText(s) {
      return ['草稿', '已发布'][s] || '未知'
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
  background: #f3f4f6;
  color: #6b7280;
}
.status-1 {
  background: #f6ffed;
  color: #52c41a;
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
