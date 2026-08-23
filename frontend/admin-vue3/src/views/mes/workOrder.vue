<!--
  ====================================================================================
  fatjar 管理后台 - 工单管理 src/views/mes/workOrder.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 生产工单列表（调用 /mes/work-order/page）
    2. 搜索 + 表格 + 分页 + 增删改占位
  说明：业务字段为 MES 制造执行常见字段，后端接口对接后即可展示
  ====================================================================================
-->
<template>
  <div class="page-card">
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="工单号">
        <el-input v-model="query.workOrderNo" placeholder="请输入工单号" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 160px">
          <el-option label="新建" :value="0" />
          <el-option label="生产中" :value="1" />
          <el-option label="已完成" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增工单</el-button>
    </div>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="workOrderNo" label="工单号" min-width="140" />
      <el-table-column prop="productName" label="产品" min-width="160" />
      <el-table-column prop="quantity" label="数量" width="100" align="right" />
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="plannedStart" label="计划开始" min-width="160" />
      <el-table-column prop="plannedEnd" label="计划结束" min-width="160" />
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
  workOrderNo: '',
  status: '',
  pageNum: 1,
  pageSize: 10,
})

// ---------- 表格数据 ----------
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// ---------- 状态文案 / 样式映射 ----------
const statusText = (s) => ({ 0: '新建', 1: '生产中', 2: '已完成' }[s] || '未知')
const statusType = (s) => ({ 0: 'info', 1: 'warning', 2: 'success' }[s] || 'info')

// ---------- 加载数据：调用 /mes/work-order/page ----------
const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/mes/work-order/page', { params: query })
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
  query.workOrderNo = ''
  query.status = ''
  query.pageNum = 1
  loadData()
}

// ---------- 占位操作 ----------
const handleAdd = () => ElMessage.info('新增工单：占位，待开发')
const handleEdit = (row) => ElMessage.info(`编辑工单：${row.workOrderNo}（占位）`)
const handleDelete = (row) => ElMessage.info(`删除工单：${row.workOrderNo}（占位）`)

onMounted(loadData)
</script>

<style scoped>
/* 复用 main.css 中的 .page-card / .search-bar / .toolbar / .pagination */
</style>
