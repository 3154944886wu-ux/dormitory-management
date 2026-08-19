<template>
  <div class="student-fees">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>水电费查询</span>
        </div>
      </template>

      <el-row :gutter="20" class="stats-row">
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <el-statistic title="电费总计" :value="stats.electricity" :precision="2" suffix="元" />
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <el-statistic title="水费总计" :value="stats.water" :precision="2" suffix="元" />
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <el-statistic title="记录数" :value="stats.count" suffix="条" />
          </el-card>
        </el-col>
      </el-row>

      <el-table :data="fees" v-loading="loading" stripe style="margin-top: 20px">
        <el-table-column label="月份" width="120">
          <template #default="{ row }">
            {{ formatMonth(row) }}
          </template>
        </el-table-column>
        <el-table-column label="电费" width="110">
          <template #default="{ row }">¥{{ formatMoney(row.electricityFee) }}</template>
        </el-table-column>
        <el-table-column label="水费" width="110">
          <template #default="{ row }">¥{{ formatMoney(row.waterFee) }}</template>
        </el-table-column>
        <el-table-column label="合计" width="110">
          <template #default="{ row }">¥{{ formatMoney(row.totalFee) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '已缴费' : '待缴费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="缴费时间" min-width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.payTime) }}
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="applyPage"
          @current-change="applyPage"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

const loading = ref(false)
const allFees = ref([])
const fees = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const stats = reactive({
  electricity: 0,
  water: 0,
  count: 0
})

const formatMoney = (value) => Number(value || 0).toFixed(2)

const formatMonth = (row) => {
  if (row.year == null || row.month == null) return '-'
  return `${row.year}-${String(row.month).padStart(2, '0')}`
}

const formatDateTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ').substring(0, 16)
}

const applyPage = () => {
  const start = (pageNum.value - 1) * pageSize.value
  fees.value = allFees.value.slice(start, start + pageSize.value)
}

const loadFees = async () => {
  loading.value = true
  try {
    const res = await api.get('/utility-fees')
    allFees.value = Array.isArray(res.data) ? res.data : []
    total.value = allFees.value.length
    stats.electricity = allFees.value.reduce((sum, fee) => sum + Number(fee.electricityFee || 0), 0)
    stats.water = allFees.value.reduce((sum, fee) => sum + Number(fee.waterFee || 0), 0)
    stats.count = allFees.value.length
    applyPage()
  } catch (error) {
    ElMessage.error(error.message || '加载费用数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadFees()
})
</script>

<style scoped>
.student-fees {
  padding: 0;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stats-row {
  margin-top: 20px;
}

.stat-card {
  text-align: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
