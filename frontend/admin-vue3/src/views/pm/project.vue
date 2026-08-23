<!--
  ====================================================================================
  fatjar 管理后台 - 项目管理 src/views/pm/project.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 项目列表（调用 /pm/project/page）
    2. 搜索 + 表格 + 分页 + 增删改占位
  说明：业务字段为 PM 项目管理常见字段，后端接口对接后即可展示
  ====================================================================================
-->
<template>
  <div class="page-card">
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="项目名称">
        <el-input v-model="query.projectName" placeholder="请输入项目名称" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 160px">
          <el-option label="规划中" :value="0" />
          <el-option label="进行中" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增项目</el-button>
    </div>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="projectNo" label="项目编号" width="130" />
      <el-table-column prop="projectName" label="项目名称" min-width="160" />
      <el-table-column prop="managerId" label="经理ID" width="100" align="center" />
      <el-table-column prop="startDate" label="开始日期" min-width="120" />
      <el-table-column prop="endDate" label="结束日期" min-width="120" />
      <el-table-column prop="budget" label="预算" width="120" align="right">
        <template #default="{ row }">
          ¥ {{ Number(row.budget || 0).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

// ---------- 查询条件 ----------
const query = reactive({
  projectName: '',
  status: '',
  pageNum: 1,
  pageSize: 10,
})

// ---------- 表格数据 ----------
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// ---------- 状态文案 / 样式映射 ----------
const statusText = (s) => ({ 0: '规划中', 1: '进行中', 2: '已完成', 3: '已取消' }[s] || '未知')
const statusType = (s) => ({ 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }[s] || 'info')

// ---------- 加载数据：调用 /pm/project/page ----------
const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/pm/project/page', { params: query })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
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
  query.projectName = ''
  query.status = ''
  query.pageNum = 1
  loadData()
}

// ---------- 占位操作 ----------
const handleAdd = () => ElMessage.info('新增项目：占位，待开发')
const handleEdit = (row) => ElMessage.info(`编辑项目：${row.projectName}（占位）`)
const handleDelete = (row) => ElMessage.info(`删除项目：${row.projectName}（占位）`)

onMounted(loadData)
</script>

<style scoped>
/* 复用 main.css 中的 .page-card / .search-bar / .toolbar / .pagination */
</style>
