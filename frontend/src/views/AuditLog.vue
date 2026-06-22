<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>审计日志</span>
          <el-button @click="loadData">刷新</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="filters">
        <el-form-item label="操作者类型">
          <el-select v-model="filters.operatorType" clearable style="width: 140px">
            <el-option label="学生" value="student" />
            <el-option label="Manager" value="manager" />
            <el-option label="管理员" value="admin" />
            <el-option label="系统" value="system" />
          </el-select>
        </el-form-item>
        <el-form-item label="动作">
          <el-input v-model="filters.action" placeholder="如 checkin.failed" clearable />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="logs" v-loading="loading" stripe>
        <el-table-column prop="createTime" label="时间" width="180" />
        <el-table-column prop="operatorType" label="类型" width="100" />
        <el-table-column prop="operatorId" label="操作者" width="140" />
        <el-table-column prop="studentName" label="学生" width="120" />
        <el-table-column prop="action" label="动作" width="180" />
        <el-table-column prop="detail" label="详情" min-width="260" show-overflow-tooltip />
      </el-table>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        class="pager"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getOperationLogs } from '@/api/operationLog'

const loading = ref(false)
const logs = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const filters = reactive({ operatorType: '', action: '', keyword: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOperationLogs({
      ...filters,
      page: page.value,
      size: size.value
    })
    logs.value = res.data?.data || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pager { margin-top: 20px; justify-content: flex-end; }
</style>
