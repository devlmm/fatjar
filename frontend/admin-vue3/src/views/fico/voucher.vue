<!--
  ====================================================================================
  fatjar 管理后台 - 凭证管理 src/views/fico/voucher.vue
  ------------------------------------------------------------------------------------
  功能：
    1. 会计凭证列表（调用 /fico/voucher/page）
    2. 搜索 + 表格 + 分页 + 增删改占位
  说明：业务字段为 FICO 财务会计常见字段，后端接口对接后即可展示
  ====================================================================================
-->
<template>
  <div class="page-card">
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="凭证编号">
        <el-input v-model="query.voucherNo" placeholder="请输入凭证编号" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 160px">
          <el-option label="草稿" :value="0" />
          <el-option label="已审核" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增凭证</el-button>
    </div>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="voucherNo" label="凭证编号" min-width="140" />
      <el-table-column prop="title" label="摘要" min-width="160" />
      <el-table-column prop="amount" label="金额" width="120" align="right">
        <template #default="{ row }">
          ¥ {{ Number(row.amount || 0).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="direction" label="方向" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="row.direction === 1 ? 'danger' : 'success'">
            {{ row.direction === 1 ? '贷方' : '借方' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="period" label="期间" width="110" align="center" />
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '已审核' : '草稿' }}
          </el-tag>
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
  voucherNo: '',
  status: '',
  pageNum: 1,
  pageSize: 10,
})

// ---------- 表格数据 ----------
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// ---------- 加载数据：调用 /fico/voucher/page ----------
const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/fico/voucher/page', { params: query })
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
  query.voucherNo = ''
  query.status = ''
  query.pageNum = 1
  loadData()
}

// ---------- 占位操作 ----------
const handleAdd = () => ElMessage.info('新增凭证：占位，待开发')
const handleEdit = (row) => ElMessage.info(`编辑凭证：${row.voucherNo}（占位）`)
const handleDelete = (row) => ElMessage.info(`删除凭证：${row.voucherNo}（占位）`)

onMounted(loadData)
</script>

<style scoped>
/* 复用 main.css 中的 .page-card / .search-bar / .toolbar / .pagination */
</style>
