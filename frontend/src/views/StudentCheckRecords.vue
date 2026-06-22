<template>
  <div class="student-check-records">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的打卡记录</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="loadRecords"
          />
        </div>
      </template>
      
      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="checkDate" label="日期" width="120" />
        <el-table-column prop="checkTime" label="打卡时间" width="100">
          <template #default="{ row }">
            {{ formatTime(row.checkTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="checkType" label="打卡类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.checkType === 1 ? 'success' : 'primary'">
              {{ row.checkType === 1 ? '主动打卡' : '系统补签' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" min-width="150">
          <template #default="{ row }">
            <span v-if="row.latitude && row.longitude">
              {{ row.latitude.toFixed(6) }}, {{ row.longitude.toFixed(6) }}
            </span>
            <span v-else class="text-muted">未记录</span>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadRecords"
          @current-change="loadRecords"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { studentAPI } from '@/api/student'

const loading = ref(false)
const records = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dateRange = ref([])

const getStatusType = (status) => {
  switch (status) {
    case 0: return 'success'
    case 1: return 'warning'
    case 2: return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 0: return '正常'
    case 1: return '晚归'
    case 2: return '未归'
    default: return '未知'
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  if (typeof time === 'string') {
    return time.includes('T') ? time.substring(11, 19) : time
  }
  return new Date(time).toLocaleTimeString('zh-CN', { hour12: false })
}

const loadRecords = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0].toISOString().split('T')[0]
      params.endDate = dateRange.value[1].toISOString().split('T')[0]
    }
    
    const res = await studentAPI.getCheckRecords(params)
    records.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.student-check-records {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.text-muted {
  color: #909399;
}
</style>