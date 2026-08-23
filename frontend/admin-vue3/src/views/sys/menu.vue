<!--
  ====================================================================================
  fatjar 管理后台 - 菜单管理 src/views/sys/menu.vue
  ------------------------------------------------------------------------------------
  说明：占位页面，演示菜单树形结构。后续对接 /sys/menu/tree 接口
  ====================================================================================
-->
<template>
  <div class="page-card">
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增菜单</el-button>
      <el-button :icon="Refresh" @click="loadData">刷新</el-button>
    </div>
    <!-- 树形表格：菜单本身是树形数据 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      row-key="id"
      border
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="name" label="菜单名称" min-width="200" />
      <el-table-column prop="icon" label="图标" width="100" align="center" />
      <el-table-column prop="type" label="类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.type === 1 ? '' : row.type === 2 ? 'success' : 'warning'">
            {{ row.type === 1 ? '目录' : row.type === 2 ? '菜单' : '按钮' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="path" label="路由地址" min-width="160" />
      <el-table-column prop="perms" label="权限标识" min-width="160" />
      <el-table-column prop="sort" label="排序" width="80" align="center" />
      <el-table-column label="操作" width="200" align="center">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleAdd(row)">新增子项</el-button>
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])

// 加载菜单树：调用 /sys/menu/tree
const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/sys/menu/tree')
    tableData.value = res.data || []
  } catch (e) {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const handleAdd = (row) =>
  ElMessage.info(row ? `新增子菜单：${row.name}（占位）` : '新增菜单（占位）')
const handleEdit = (row) => ElMessage.info(`编辑菜单：${row.name}（占位）`)
const handleDelete = (row) => ElMessage.info(`删除菜单：${row.name}（占位）`)

onMounted(loadData)
</script>

<style scoped>
/* 复用 main.css 中 .page-card / .toolbar */
</style>
