<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>范围内归寝记录</span>
          <el-button type="success" @click="exportData">导出</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="filters">
        <el-form-item label="日期">
          <el-date-picker v-model="filters.dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable style="width: 130px">
            <el-option label="已归" :value="0" />
            <el-option label="晚归" :value="1" />
            <el-option label="未归" :value="2" />
            <el-option label="请假" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="studentName" label="学生" width="100" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="buildingName" label="楼栋" width="100" />
        <el-table-column prop="roomNumber" label="房间" width="80" />
        <el-table-column prop="checkDate" label="日期" width="120" />
        <el-table-column prop="checkTime" label="打卡时间" min-width="170">
          <template #default="{ row }">{{ formatTime(row.checkTime) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }"><el-tag :type="statusMeta(row.status).type">{{ statusMeta(row.status).text }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="locationAccuracy" label="精度" width="100">
          <template #default="{ row }">{{ row.locationAccuracy ? `${Math.round(row.locationAccuracy)}米` : '-' }}</template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        class="pager"
      />
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { getRecords, exportRecords } from '@/api/checkIn'

const loading = ref(false)
const records = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const filters = reactive({ dateRange: [], status: '' })

const params = () => ({
  page: page.value,
  size: size.value,
  startDate: filters.dateRange?.[0],
  endDate: filters.dateRange?.[1],
  status: filters.status === '' ? undefined : filters.status
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRecords(params())
    records.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const exportData = async () => {
  const blob = await exportRecords(params())
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '归寝记录.csv'
  a.click()
  URL.revokeObjectURL(url)
}

const statusMeta = (status) => {
  const map = {
    0: { text: '已归', type: 'success' },
    1: { text: '晚归', type: 'warning' },
    2: { text: '未归', type: 'danger' },
    3: { text: '请假', type: 'info' }
  }
  return map[status] || { text: '未知', type: 'info' }
}

const formatTime = (value) => value ? new Date(value).toLocaleString('zh-CN') : '-'

onMounted(loadData)
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pager { margin-top: 20px; justify-content: flex-end; }
</style>
