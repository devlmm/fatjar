<!--
  ====================================================================================
  fatjar 管理后台 - 角色管理 src/views/sys/role.vue
  ------------------------------------------------------------------------------------
  说明：占位页面，演示角色管理基础结构。后续对接 /sys/role 接口
  ====================================================================================
-->
<template>
  <div class="page-card">
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
    </div>
    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="code" label="角色编码" min-width="120" />
      <el-table-column prop="name" label="角色名称" min-width="120" />
      <el-table-column prop="dataScope" label="数据范围" width="120" align="center">
        <template #default="{ row }">
          <el-tag>{{ row.dataScope || '全部' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="160" />
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handlePermission(row)">分配权限</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])

// 加载角色列表：调用 /sys/role/page
const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/sys/role/page', { params: { pageNum: 1, pageSize: 100 } })
    tableData.value = res.data?.records || []
  } catch (e) {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const handleAdd = () => ElMessage.info('新增角色：占位，待开发')
const handlePermission = (row) => ElMessage.info(`分配权限：${row.name}（占位）`)
const handleDelete = (row) => ElMessage.info(`删除角色：${row.name}（占位）`)

onMounted(loadData)
</script>

<style scoped>
/* 复用 main.css 中 .page-card / .toolbar */
</style>
