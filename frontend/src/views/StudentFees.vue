<template>
  <div class="student-fees">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>水电费查询</span>
        </div>
      </template>
      
      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <el-statistic title="电费总计" :value="stats.electricity" suffix="元" />
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <el-statistic title="水费总计" :value="stats.water" suffix="元" />
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <el-statistic title="记录数" :value="stats.count" suffix="条" />
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 费用列表 -->
      <el-table :data="fees" v-loading="loading" stripe style="margin-top: 20px">
        <el-table-column label="月份" prop="month" width="120" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'ELECTRICITY' ? 'warning' : 'primary'" size="small">
              {{ row.type === 'ELECTRICITY' ? '电费' : '水费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="用量" width="120">
          <template #default="{ row }">
            {{ row.usage }} {{ row.type === 'ELECTRICITY' ? '度' : '吨' }}
          </template>
        </el-table-column>
        <el-table-column label="金额" prop="amount" width="100">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '已缴费' : '待缴费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="缴费时间" prop="payTime" width="160" />
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadFees"
          @current-change="loadFees"
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
const fees = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const stats = reactive({
  electricity: 0,
  water: 0,
  count: 0
})

const loadFees = async () => {
  loading.value = true
  try {
    const res = await api.get('/utility-fees', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value
      }
    })
    fees.value = res.data || []
    total.value = res.data?.length || 0
    
    // 计算统计数据
    let electricity = 0
    let water = 0
    fees.value.forEach(fee => {
      if (fee.type === 'ELECTRICITY') {
        electricity += fee.amount || 0
      } else {
        water += fee.amount || 0
      }
    })
    stats.electricity = electricity
    stats.water = water
    stats.count = fees.value.length
  } catch (error) {
    console.error('加载费用数据失败:', error)
    ElMessage.error('加载费用数据失败')
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