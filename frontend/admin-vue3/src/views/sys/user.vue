<!--
  ====================================================================================
  fatjar 管理后台 - 用户管理 src/views/sys/user.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 搜索栏：用户名 / 状态 / 手机号
    2. 工具栏：新增 / 批量删除 / 刷新
    3. 表格：分页列表（调用 /sys/user/page）
    4. 占位实现，演示表格与分页基础结构
  ====================================================================================
-->
<template>
  <div class="page-card">
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="用户名">
        <el-input v-model="query.username" placeholder="请输入用户名" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
      <el-button type="danger" :icon="Delete" :disabled="!selectedRows.length" @click="handleBatchDelete">
        批量删除
      </el-button>
      <el-button :icon="RefreshRight" circle @click="loadData" />
    </div>

    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="username" label="用户名" min-width="120" />
      <el-table-column prop="nickname" label="昵称" min-width="120" />
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, RefreshRight, Edit } from '@element-plus/icons-vue'
import request from '@/utils/request'

// ---------- 查询条件 ----------
const query = reactive({
  username: '',
  status: '',
  pageNum: 1,
  pageSize: 10,
})

// ---------- 表格数据 ----------
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])

// ---------- 加载数据：调用 /sys/user/page ----------
const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/sys/user/page', { params: query })
    // 假设后端返回：{ data: { records: [], total: 0 } }
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    // 错误已由 request.js 弹出，这里静默
    // 占位：失败时显示空表
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// ---------- 搜索 / 重置 ----------
const handleSearch = () => {
  query.pageNum = 1
  loadData()
}
const handleReset = () => {
  query.username = ''
  query.status = ''
  query.pageNum = 1
  loadData()
}

// ---------- 表格选择 ----------
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

// ---------- 增删改占位 ----------
const handleAdd = () => ElMessage.info('新增功能：占位，待开发')
const handleEdit = (row) => ElMessage.info(`编辑用户：${row.username}（占位）`)
const handleDelete = (row) => ElMessage.info(`删除用户：${row.username}（占位）`)
const handleBatchDelete = () =>
  ElMessage.info(`批量删除 ${selectedRows.value.length} 条（占位）`)

// ---------- 初始化 ----------
onMounted(loadData)
</script>

<style scoped>
/* 全局样式已在 main.css 定义 .page-card / .search-bar / .toolbar / .pagination */
</style>
