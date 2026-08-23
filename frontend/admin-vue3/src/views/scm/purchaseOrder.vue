<!--
  ====================================================================================
  fatjar 管理后台 - 采购订单 src/views/scm/purchaseOrder.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 采购订单列表（调用 /scm/purchase-order/page）
    2. 搜索 + 表格 + 分页 + 增删改占位
  说明：业务字段为 SCM 供应链管理常见字段，后端接口对接后即可展示
  ====================================================================================
-->
<template>
  <div class="page-card">
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="订单号">
        <el-input v-model="query.orderNo" placeholder="请输入订单号" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 160px">
          <el-option label="待审批" :value="0" />
          <el-option label="已审批" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增采购订单</el-button>
    </div>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="orderNo" label="订单号" min-width="140" />
      <el-table-column prop="supplierName" label="供应商" min-width="160" />
      <el-table-column prop="totalAmount" label="金额" width="120" align="right">
        <template #default="{ row }">
          ¥ {{ Number(row.totalAmount || 0).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="deptId" label="部门ID" width="100" align="center" />
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
  orderNo: '',
  status: '',
  pageNum: 1,
  pageSize: 10,
})

// ---------- 表格数据 ----------
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// ---------- 状态文案 / 样式映射 ----------
const statusText = (s) => ({ 0: '待审批', 1: '已审批', 2: '已驳回' }[s] || '未知')
const statusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')

// ---------- 加载数据：调用 /scm/purchase-order/page ----------
const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/scm/purchase-order/page', { params: query })
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
  query.orderNo = ''
  query.status = ''
  query.pageNum = 1
  loadData()
}

// ---------- 占位操作 ----------
const handleAdd = () => ElMessage.info('新增采购订单：占位，待开发')
const handleEdit = (row) => ElMessage.info(`编辑采购订单：${row.orderNo}（占位）`)
const handleDelete = (row) => ElMessage.info(`删除采购订单：${row.orderNo}（占位）`)

onMounted(loadData)
</script>

<style scoped>
/* 复用 main.css 中的 .page-card / .search-bar / .toolbar / .pagination */
</style>
