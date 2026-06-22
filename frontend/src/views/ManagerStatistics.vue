<template>
  <div class="page-container">
    <div class="page-header">
      <h2>归寝统计分析</h2>
      <div class="header-right">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          :shortcuts="dateShortcuts"
          @change="loadData"
        />
        <el-button @click="loadData"><el-icon><Refresh /></el-icon>刷新</el-button>
      </div>
    </div>

    <SkeletonLoader v-if="loading" type="card" :count="7" />
    <div v-else class="stats-row">
      <StatCard :value="checkInSummary.normalCount" label="已归" :color="STATUS_COLORS.normal">
        <template #icon><el-icon :size="24"><CircleCheck /></el-icon></template>
      </StatCard>
      <StatCard :value="checkInSummary.lateCount" label="晚归" :color="STATUS_COLORS.late">
        <template #icon><el-icon :size="24"><Sunset /></el-icon></template>
      </StatCard>
      <StatCard :value="exceptionSummary.absentUnhandledCount" label="未归·未处理" :color="STATUS_COLORS.absent">
        <template #icon><el-icon :size="24"><Warning /></el-icon></template>
      </StatCard>
      <StatCard :value="exceptionSummary.absentHandledCount" label="未归·已处理" color="#FAB6B6">
        <template #icon><el-icon :size="24"><CircleCheck /></el-icon></template>
      </StatCard>
      <StatCard :value="checkInSummary.leaveCount" label="请假" :color="STATUS_COLORS.leave">
        <template #icon><el-icon :size="24"><Document /></el-icon></template>
      </StatCard>
      <StatCard :value="exceptionSummary.totalCount" label="异常总数" color="var(--color-primary)">
        <template #icon><el-icon :size="24"><TrendCharts /></el-icon></template>
      </StatCard>
      <StatCard
        :value="exceptionSummary.unhandledCount"
        label="待处理异常"
        :color="exceptionSummary.unhandledCount > 0 ? 'var(--color-warning)' : 'var(--color-text-secondary)'"
      >
        <template #icon><el-icon :size="24"><Bell /></el-icon></template>
      </StatCard>
    </div>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header><span class="card-header-title">归寝状态分布</span></template>
          <EChartsWrapper v-if="hasCheckInData" :option="statusPieOption" height="320px" />
          <el-empty v-else description="所选时段暂无打卡数据" :image-size="80" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header><span class="card-header-title">异常类型分布</span></template>
          <EChartsWrapper v-if="hasExceptionData" :option="exceptionPieOption" height="320px" />
          <el-empty v-else description="所选时段暂无异常数据" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header><span class="card-header-title">每日归寝趋势</span></template>
          <EChartsWrapper v-if="hasCheckInData" :option="dailyTrendOption" height="340px" />
          <el-empty v-else description="所选时段暂无打卡数据" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header><span class="card-header-title">按楼栋异常统计</span></template>
          <EChartsWrapper v-if="(exceptionTrend.byBuilding || []).length" :option="buildingOption" height="360px" />
          <el-empty v-else description="暂无楼栋异常数据" :image-size="80" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header><span class="card-header-title">按班级异常统计</span></template>
          <EChartsWrapper v-if="(exceptionTrend.byClass || []).length" :option="classOption" height="360px" />
          <el-empty v-else description="暂无班级异常数据" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Bell, CircleCheck, Document, Refresh, Sunset, TrendCharts, Warning } from '@element-plus/icons-vue'
import EChartsWrapper from '@/components/business/EChartsWrapper.vue'
import StatCard from '@/components/business/StatCard.vue'
import SkeletonLoader from '@/components/common/SkeletonLoader.vue'
import { getCheckInTrend, getRecords } from '@/api/checkIn'
import { getExceptionTrend, searchExceptions } from '@/api/checkException'
import {
  STATUS_COLORS,
  buildDailyTrendOption,
  buildExceptionPieOption,
  buildGroupedBarOption,
  buildStatusPieOption
} from '@/utils/checkinChartOptions'
import { aggregateCheckInRecords, aggregateExceptionTrend, mergeExceptionSummary, summarizeExceptionList, summarizeExceptionRows } from '@/utils/checkinStats'

const loading = ref(false)
const dateRange = ref(defaultDateRange())
const checkInSummary = ref(emptyCheckInSummary())
const exceptionSummary = ref(emptyExceptionSummary())
const dailyTrend = ref([])
const exceptionTrend = ref({ byBuilding: [], byClass: [] })

const dateShortcuts = [
  { text: '近7天', value: () => shiftRange(6) },
  { text: '近30天', value: () => shiftRange(29) },
  { text: '本月', value: () => monthRange() }
]

function pad(n) { return String(n).padStart(2, '0') }
function formatDate(d) { return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}` }

function defaultDateRange() {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 6)
  return [formatDate(start), formatDate(end)]
}

function shiftRange(days) {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - days)
  return [start, end]
}

function monthRange() {
  const now = new Date()
  const start = new Date(now.getFullYear(), now.getMonth(), 1)
  return [start, now]
}

function emptyCheckInSummary() {
  return { normalCount: 0, lateCount: 0, absentCount: 0, leaveCount: 0, totalCount: 0 }
}

function emptyExceptionSummary() {
  return {
    lateCount: 0,
    absentCount: 0,
    absentHandledCount: 0,
    absentUnhandledCount: 0,
    missingCount: 0,
    totalCount: 0,
    unhandledCount: 0
  }
}

const params = () => ({
  startDate: dateRange.value?.[0],
  endDate: dateRange.value?.[1]
})

const hasCheckInData = computed(() => (checkInSummary.value.totalCount || 0) > 0)
const hasExceptionData = computed(() => (exceptionSummary.value.totalCount || 0) > 0)

const statusPieOption = computed(() => buildStatusPieOption(checkInSummary.value))
const exceptionPieOption = computed(() => buildExceptionPieOption(exceptionSummary.value))
const dailyTrendOption = computed(() => buildDailyTrendOption(dailyTrend.value, dateRange.value))
const buildingOption = computed(() => buildGroupedBarOption('', exceptionTrend.value.byBuilding || []))
const classOption = computed(() => buildGroupedBarOption('', exceptionTrend.value.byClass || []))

const loadData = async () => {
  loading.value = true
  const query = params()
  try {
    const [checkInResult, exceptionResult, unhandledResult, allExceptionResult] = await Promise.allSettled([
      getCheckInTrend(query),
      getExceptionTrend(query),
      searchExceptions({ ...query, handled: 0 }),
      searchExceptions(query)
    ])

    let checkInData = {}
    if (checkInResult.status === 'fulfilled') {
      checkInData = checkInResult.value?.data || {}
    }
    if (checkInResult.status === 'rejected' || !(checkInData.summary?.totalCount > 0)) {
      const recordsRes = await getRecords({ ...query, page: 1, size: 10000 })
      checkInData = aggregateCheckInRecords(recordsRes.data?.records || [])
    }

    checkInSummary.value = { ...emptyCheckInSummary(), ...(checkInData.summary || {}) }
    dailyTrend.value = checkInData.dailyTrend || []

    let exceptionData = exceptionResult.status === 'fulfilled'
      ? (exceptionResult.value?.data || {})
      : { byBuilding: [], byClass: [] }
    const allExceptionItems = allExceptionResult.status === 'fulfilled'
      ? (allExceptionResult.value?.data || [])
      : []

    let byBuilding = exceptionData.byBuilding || []
    let byClass = exceptionData.byClass || []
    const rowsHaveHandled = [...byBuilding, ...byClass].some(row => row.handled != null)
    if (!rowsHaveHandled && allExceptionItems.length) {
      byBuilding = aggregateExceptionTrend(allExceptionItems, 'buildingName')
      byClass = aggregateExceptionTrend(allExceptionItems, 'className')
    }

    const summaryRows = byBuilding.length ? byBuilding : byClass
    let summary = mergeExceptionSummary(exceptionData.summary, summaryRows)
    if (allExceptionItems.length && !(summary.absentHandledCount + summary.absentUnhandledCount > 0)) {
      const listSummary = summarizeExceptionList(allExceptionItems)
      summary = { ...summary, ...listSummary }
    }
    if (unhandledResult.status === 'fulfilled') {
      const unhandled = unhandledResult.value?.data
      if (Array.isArray(unhandled)) {
        summary.unhandledCount = unhandled.length
      }
    }
    if (!summary.totalCount && summaryRows.length) {
      summary = summarizeExceptionRows(summaryRows)
    }
    exceptionSummary.value = { ...emptyExceptionSummary(), ...summary }
    exceptionTrend.value = { byBuilding, byClass }
  } catch (error) {
    console.error('加载统计数据失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-container { padding: 20px; }
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}
.page-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
.header-right { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}
.chart-row { margin-bottom: 16px; }
.chart-card { border-radius: var(--radius-lg, 8px); }
.card-header-title { font-weight: 600; }
</style>
