<template>
  <div class="report-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分配报表</span>
        </div>
      </template>

      <div class="filter-bar">
        <el-select v-model="selectedBatchId" placeholder="请选择批次" @change="loadReport" style="width: 280px;">
          <el-option v-for="b in batches" :key="b.id" :label="b.name + ' (' + statusLabel(b.matchStatus) + ')'" :value="b.id" />
        </el-select>
        <el-button type="primary" :disabled="!selectedBatchId" @click="handleDownload" style="margin-left: 12px;">
          <el-icon><Download /></el-icon>
          导出 Excel
        </el-button>
      </div>

      <el-table :data="results" v-loading="loading" stripe style="margin-top: 15px;" max-height="600">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床位" width="70" />
        <el-table-column prop="batchName" label="批次" min-width="160" show-overflow-tooltip />
        <el-table-column label="匹配度" width="90" sortable prop="matchScore">
          <template #default="{ row }">
            <span>{{ row.matchScore != null ? row.matchScore : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import { dormBatchAPI } from '@/api/dormBatch'
import { allocationReportAPI } from '@/api/allocationReport'

const loading = ref(false)
const batches = ref([])
const selectedBatchId = ref(null)
const results = ref([])

const statusType = (s) => ({ recommended: 'info', confirmed: 'success', auto_confirmed: 'success', manual_assigned: 'warning', adjusted: 'warning' }[s] || 'info')
const statusLabel = (s) => ({ pending: '待启动', running: '运行中', matching: '匹配中', confirming: '确认中', finished: '已结束', archived: '已归档', cancelled: '已作废', recommended: '推荐', confirmed: '已确认', auto_confirmed: '自动确认', manual_assigned: '手动分配', adjusted: '已调换' }[s] || s)

const loadBatches = async () => {
  try {
    const res = await dormBatchAPI.list({})
    batches.value = res.data || []
  } catch (e) { /* ignore */ }
}

const loadReport = async () => {
  if (!selectedBatchId.value) return
  loading.value = true
  try {
    const res = await allocationReportAPI.getReport(selectedBatchId.value)
    results.value = res.data || []
  } catch (e) {
    ElMessage.error('加载报表失败')
  } finally {
    loading.value = false
  }
}

const handleDownload = async () => {
  if (!selectedBatchId.value) return
  try {
    const res = await allocationReportAPI.downloadExcel(selectedBatchId.value)
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url
    link.download = '分配报表.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch (e) {
    ElMessage.error('下载失败')
  }
}

onMounted(() => {
  loadBatches()
})
</script>

<style scoped>
.report-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.filter-bar { display: flex; align-items: center; }
</style>
