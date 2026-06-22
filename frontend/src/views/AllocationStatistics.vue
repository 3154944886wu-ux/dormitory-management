<template>
  <div class="stats-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>统计分析</span>
        </div>
      </template>

      <div class="filter-bar">
        <el-select v-model="selectedBatchId" placeholder="请选择批次" @change="loadStats" style="width: 280px;">
          <el-option v-for="b in batches" :key="b.id" :label="b.name + ' (' + statusLabel(b.matchStatus) + ')'" :value="b.id" />
        </el-select>
      </div>

      <div v-if="stats" style="margin-top: 20px;">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-card">
                <div class="stat-value">{{ stats.totalCount }}</div>
                <div class="stat-label">总分配人数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-card">
                <div class="stat-value">{{ stats.avgMatchScore }}</div>
                <div class="stat-label">平均匹配度</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-card">
                <div class="stat-value">{{ stats.reallocationRate }}</div>
                <div class="stat-label">重匹配使用率</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-card">
                <div class="stat-value">{{ stats.relocationRate }}</div>
                <div class="stat-label">调换比例</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="8">
            <el-card shadow="hover">
              <template #header><span>分配状态分布</span></template>
              <el-table :data="statusData" size="small">
                <el-table-column prop="status" label="状态">
                  <template #default="{ row }">
                    <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="cnt" label="人数" width="80" align="center" />
                <el-table-column label="占比" width="120">
                  <template #default="{ row }">
                    <el-progress :percentage="pct(row.cnt)" :stroke-width="6" />
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover">
              <template #header><span>各楼栋分布</span></template>
              <el-table :data="stats.buildingDistribution" size="small">
                <el-table-column prop="building_name" label="楼栋" />
                <el-table-column prop="cnt" label="人数" width="80" align="center" />
              </el-table>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover">
              <template #header><span>匹配度分布</span></template>
              <el-table :data="scoreDistributionData" size="small">
                <el-table-column prop="range" label="区间" />
                <el-table-column prop="cnt" label="人数" width="80" align="center" />
              </el-table>
            </el-card>
          </el-col>
        </el-row>

        <el-card shadow="hover" style="margin-top: 20px;">
          <template #header><span>各专业分布</span></template>
          <el-table :data="stats.majorDistribution" size="small">
            <el-table-column prop="major_name" label="专业" />
            <el-table-column prop="cnt" label="人数" width="100" align="center" />
            <el-table-column label="占比" width="200">
              <template #default="{ row }">
                <el-progress :percentage="pct(row.cnt)" :stroke-width="8" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>

      <el-empty v-else-if="selectedBatchId" description="暂无数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { dormBatchAPI } from '@/api/dormBatch'
import { allocationReportAPI } from '@/api/allocationReport'

const batches = ref([])
const selectedBatchId = ref(null)
const stats = ref(null)

const statusType = (s) => ({ recommended: 'info', confirmed: 'success', auto_confirmed: 'success', manual_assigned: 'warning', adjusted: 'warning' }[s] || 'info')
const statusLabel = (s) => ({ pending: '待启动', running: '运行中', matching: '匹配中', confirming: '确认中', finished: '已结束', archived: '已归档', cancelled: '已作废', recommended: '推荐', confirmed: '已确认', auto_confirmed: '自动确认', manual_assigned: '手动分配', adjusted: '已调换' }[s] || s)

const statusData = computed(() => stats.value ? stats.value.statusDistribution : [])
const pct = (cnt) => stats.value && stats.value.totalCount ? Math.round(cnt / stats.value.totalCount * 100) : 0

const scoreDistributionData = computed(() => {
  if (!stats.value || !stats.value.scoreDistribution) return []
  const d = stats.value.scoreDistribution
  return [
    { range: '0-20', cnt: d.range_0_20 || 0 },
    { range: '20-40', cnt: d.range_20_40 || 0 },
    { range: '40-60', cnt: d.range_40_60 || 0 },
    { range: '60-80', cnt: d.range_60_80 || 0 },
    { range: '80-100', cnt: d.range_80_100 || 0 }
  ]
})

const loadBatches = async () => {
  try {
    const res = await dormBatchAPI.list({})
    batches.value = res.data || []
  } catch (e) { /* ignore */ }
}

const loadStats = async () => {
  if (!selectedBatchId.value) return
  try {
    const res = await allocationReportAPI.getStatistics(selectedBatchId.value)
    stats.value = res.data
  } catch (e) {
    stats.value = null
  }
}

onMounted(() => {
  loadBatches()
})
</script>

<style scoped>
.stats-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.filter-bar { display: flex; align-items: center; }
.stat-card { text-align: center; padding: 10px 0; }
.stat-value { font-size: 28px; font-weight: bold; color: #409EFF; }
.stat-label { font-size: 13px; color: #909399; margin-top: 6px; }
</style>
