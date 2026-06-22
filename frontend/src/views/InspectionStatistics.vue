<template>
  <div class="page-container">
    <div class="page-header">
      <h2>检查统计分析</h2>
      <div class="header-right">
        <el-date-picker
          v-model="dateRange" type="daterange" range-separator="至"
          start-placeholder="开始日期" end-placeholder="结束日期"
          format="YYYY-MM-DD" value-format="YYYY-MM-DD"
          @change="loadAll"
        />
        <el-button :disabled="allRecords.length === 0" @click="exportCSV">
          <el-icon><Download /></el-icon>导出 CSV
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <SkeletonLoader v-if="loading" type="card" :count="4" />
    <div v-else class="stats-row">
      <StatCard :value="stats.totalChecks" label="总检查数" color="var(--color-primary)">
        <template #icon><el-icon :size="24"><Checked /></el-icon></template>
      </StatCard>
      <StatCard :value="stats.passRate + '%'" label="合格率" :color="stats.passRate >= 80 ? 'var(--color-success)' : 'var(--color-danger)'">
        <template #icon><el-icon :size="24"><CircleCheck /></el-icon></template>
      </StatCard>
      <StatCard :value="stats.avgScore" label="平均分">
        <template #icon><el-icon :size="24"><TrendCharts /></el-icon></template>
      </StatCard>
      <StatCard :value="stats.pendingRectification" label="待整改" :color="stats.pendingRectification > 0 ? 'var(--color-warning)' : 'var(--color-text-secondary)'">
        <template #icon><el-icon :size="24"><Warning /></el-icon></template>
      </StatCard>
    </div>

    <!-- 图表行 1 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="table-card">
          <template #header><span class="card-header-title">合格率分布</span></template>
          <EChartsWrapper :option="pieOption" height="320px" :loading="loading" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="table-card">
          <template #header><span class="card-header-title">楼栋平均分对比</span></template>
          <EChartsWrapper :option="barOption" height="320px" :loading="loading" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表行 2 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="table-card">
          <template #header><span class="card-header-title">整改状态分布</span></template>
          <EChartsWrapper :option="rectifyOption" height="300px" :loading="loading" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="table-card">
          <template #header><span class="card-header-title">检查员工作量</span></template>
          <EChartsWrapper :option="inspectorOption" height="300px" :loading="loading" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表行 3：时间趋势 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="24">
        <el-card class="table-card">
          <template #header><span class="card-header-title">检查趋势</span></template>
          <EChartsWrapper :option="trendOption" height="300px" :loading="loading" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 月度最佳宿舍 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="24">
        <el-card class="table-card">
          <template #header>
            <div class="card-header-title">
              <el-icon :size="20" color="var(--color-warning)"><Trophy /></el-icon>
              月度最佳宿舍（{{ currentMonth }} · Top 10）
            </div>
          </template>
          <SkeletonLoader v-if="loading" type="table" :count="5" />
          <div v-else-if="topRooms.length > 0" class="top-rooms-grid">
            <div v-for="(room, idx) in topRooms" :key="room.roomId" class="top-room-card" :class="{ 'top-room-card--podium': idx < 3 }">
              <div class="top-room-rank">
                <span v-if="idx === 0" class="rank-medal">🥇</span>
                <span v-else-if="idx === 1" class="rank-medal">🥈</span>
                <span v-else-if="idx === 2" class="rank-medal">🥉</span>
                <span v-else class="rank-number">{{ idx + 1 }}</span>
              </div>
              <div class="top-room-body">
                <div class="top-room-name">{{ room.buildingName }} {{ room.roomNumber }}</div>
                <div class="top-room-meta">
                  <span>检查 {{ room.checkCount }} 次</span>
                  <span class="top-room-score stat-number">{{ room.avgScore }}</span>
                  <span style="color: var(--color-text-secondary)">分</span>
                </div>
                <el-progress :percentage="room.avgScore" :color="room.avgScore >= 90 ? '#52C41A' : room.avgScore >= 70 ? '#4A6FA5' : '#E6A23C'" :stroke-width="6" :show-text="false" />
              </div>
            </div>
          </div>
          <el-empty v-else description="本月暂无检查数据" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Checked, CircleCheck, TrendCharts, Warning, Trophy, Download } from '@element-plus/icons-vue'
import { searchRecords } from '@/api/inspection'
import StatCard from '@/components/business/StatCard.vue'
import EChartsWrapper from '@/components/business/EChartsWrapper.vue'
import SkeletonLoader from '@/components/common/SkeletonLoader.vue'

const loading = ref(false)
const dateRange = ref([])

const stats = reactive({ totalChecks: 0, passRate: 0, avgScore: '0', pendingRectification: 0 })
const passCount = ref(0)
const failCount = ref(0)
const buildingNames = ref([])
const buildingScores = ref([])
const rectifyData = ref([])
const inspectorData = ref({ names: [], counts: [] })
const trendData = ref({ months: [], passRates: [], counts: [] })
const topRooms = ref([])
const allRecords = ref([])

const exportCSV = () => {
  if (allRecords.value.length === 0) return
  const headers = ['检查时间', '楼栋', '房间号', '检查人', '评分', '结果', '整改状态', '备注']
  const rows = allRecords.value.map(r => [
    r.inspectionTime?.substring(0, 16) || '', r.buildingName || '', r.roomNumber || '',
    r.inspectorName || '', r.overallScore ?? '', r.result === 'PASS' ? '合格' : '不合格',
    { NONE: '无需整改', PENDING: '待整改', COMPLETED: '已整改', VERIFIED: '已核实' }[r.rectificationStatus] || '',
    r.remark || ''
  ])
  const csv = [headers, ...rows].map(row => row.map(c => `"${String(c).replace(/"/g, '""')}"`).join(',')).join('\n')
  const blob = new Blob(['﻿' + csv], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a'); a.href = url; a.download = `检查记录_${new Date().toISOString().slice(0,10)}.csv`; a.click()
  URL.revokeObjectURL(url)
}
const currentMonth = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月`
})

// ---- 图表配置 ----
const pieOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  legend: { bottom: 0 },
  series: [{
    type: 'pie', radius: ['45%', '70%'], center: ['50%', '45%'],
    itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
    label: { show: false },
    emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
    data: [
      { value: passCount.value, name: '合格', itemStyle: { color: '#52C41A' } },
      { value: failCount.value, name: '不合格', itemStyle: { color: '#F56C6C' } }
    ]
  }]
}))

const barOption = computed(() => ({
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  grid: { left: '3%', right: '4%', bottom: 0, containLabel: true },
  xAxis: { type: 'category', data: buildingNames.value, axisLabel: { rotate: 15, fontSize: 11 } },
  yAxis: { type: 'value', name: '平均分', max: 100 },
  series: [{
    type: 'bar', data: buildingScores.value,
    itemStyle: { borderRadius: [6, 6, 0, 0], color: '#4A6FA5' }, barMaxWidth: 40
  }]
}))

const rectifyOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  legend: { bottom: 0 },
  series: [{
    type: 'pie', radius: ['40%', '65%'], center: ['50%', '45%'],
    itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
    label: { show: true, formatter: '{b}\n{d}%', fontSize: 11 },
    data: [
      { value: rectifyData.value.NONE || 0, name: '无需整改', itemStyle: { color: '#909399' } },
      { value: rectifyData.value.PENDING || 0, name: '待整改', itemStyle: { color: '#E6A23C' } },
      { value: rectifyData.value.COMPLETED || 0, name: '已整改', itemStyle: { color: '#52C41A' } },
      { value: rectifyData.value.VERIFIED || 0, name: '已核实', itemStyle: { color: '#4A6FA5' } }
    ]
  }]
}))

const inspectorOption = computed(() => ({
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  grid: { left: '3%', right: '8%', bottom: 0, containLabel: true },
  xAxis: { type: 'value', name: '检查次数' },
  yAxis: { type: 'category', data: inspectorData.value.names, axisLabel: { fontSize: 11 }, inverse: true },
  series: [{
    type: 'bar', data: inspectorData.value.counts,
    itemStyle: { borderRadius: [0, 6, 6, 0], color: '#4A6FA5' }, barMaxWidth: 24
  }]
}))

const trendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['检查数', '合格率'] },
  grid: { left: '3%', right: '4%', bottom: 0, containLabel: true },
  xAxis: { type: 'category', data: trendData.value.months },
  yAxis: [
    { type: 'value', name: '检查数' },
    { type: 'value', name: '合格率(%)', max: 100 }
  ],
  series: [
    {
      name: '检查数', type: 'bar', data: trendData.value.counts,
      itemStyle: { borderRadius: [6, 6, 0, 0], color: '#A0B8D4' }, barMaxWidth: 32
    },
    {
      name: '合格率', type: 'line', yAxisIndex: 1, data: trendData.value.passRates,
      smooth: true, lineStyle: { color: '#52C41A', width: 3 },
      itemStyle: { color: '#52C41A' }, symbol: 'circle', symbolSize: 8
    }
  ]
}))

// ---- 数据加载 ----
const loadAll = async () => {
  loading.value = true
  try {
    const params = {}
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await searchRecords(params)
    const records = res.data || []
    allRecords.value = records

    // 统计卡片
    stats.totalChecks = records.length
    passCount.value = records.filter(r => r.result === 'PASS').length
    failCount.value = records.filter(r => r.result === 'FAIL').length
    stats.passRate = records.length > 0 ? Math.round((passCount.value / records.length) * 100) : 0
    stats.pendingRectification = records.filter(r => r.rectificationStatus === 'PENDING').length

    const scores = records.filter(r => r.overallScore != null).map(r => Number(r.overallScore))
    stats.avgScore = scores.length > 0 ? (scores.reduce((a, b) => a + b, 0) / scores.length).toFixed(1) : '0'

    // 楼栋聚合
    const bMap = {}
    records.forEach(r => {
      const n = r.buildingName || '未知'
      if (!bMap[n]) bMap[n] = []
      if (r.overallScore != null) bMap[n].push(Number(r.overallScore))
    })
    buildingNames.value = Object.keys(bMap)
    buildingScores.value = buildingNames.value.map(n => +(bMap[n].reduce((a, b) => a + b, 0) / bMap[n].length).toFixed(1))

    // 整改状态分布
    const rMap = { NONE: 0, PENDING: 0, COMPLETED: 0, VERIFIED: 0 }
    records.forEach(r => { if (rMap[r.rectificationStatus] !== undefined) rMap[r.rectificationStatus]++ })
    rectifyData.value = rMap

    // 检查员工作量
    const iMap = {}
    records.forEach(r => {
      const n = r.inspectorName || '未知'
      iMap[n] = (iMap[n] || 0) + 1
    })
    const sorted = Object.entries(iMap).sort((a, b) => b[1] - a[1]).slice(0, 10)
    inspectorData.value = { names: sorted.map(s => s[0]), counts: sorted.map(s => s[1]) }

    // 时间趋势（按月聚合）
    const tMap = {}
    records.forEach(r => {
      if (!r.inspectionTime) return
      const m = r.inspectionTime.substring(0, 7) // YYYY-MM
      if (!tMap[m]) tMap[m] = { total: 0, pass: 0 }
      tMap[m].total++
      if (r.result === 'PASS') tMap[m].pass++
    })
    const months = Object.keys(tMap).sort()
    trendData.value = {
      months,
      counts: months.map(m => tMap[m].total),
      passRates: months.map(m => Math.round((tMap[m].pass / tMap[m].total) * 100))
    }

    // 月度最佳宿舍 Top 10（按平均分排名）
    const now = new Date()
    const thisMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
    const monthRecords = records.filter(r => r.inspectionTime && r.inspectionTime.startsWith(thisMonth))
    const roomMap = {}
    monthRecords.forEach(r => {
      if (!r.roomId || r.overallScore == null) return
      const key = r.roomId
      if (!roomMap[key]) roomMap[key] = { roomId: r.roomId, roomNumber: r.roomNumber || '-', buildingName: r.buildingName || '-', scores: [] }
      roomMap[key].scores.push(Number(r.overallScore))
    })
    topRooms.value = Object.values(roomMap)
      .map(r => ({ ...r, avgScore: (r.scores.reduce((a, b) => a + b, 0) / r.scores.length).toFixed(1), checkCount: r.scores.length }))
      .sort((a, b) => b.avgScore - a.avgScore)
      .slice(0, 10)
  } catch (e) { console.error('加载统计失败', e) }
  finally { loading.value = false }
}

onMounted(() => loadAll())
</script>

<style scoped>
.header-right { display: flex; align-items: center; gap: var(--spacing-sm); }
.card-header-title { display: flex; align-items: center; gap: var(--spacing-xs); font-size: var(--font-size-base); font-weight: var(--font-weight-semibold); }
.chart-row { margin-bottom: var(--spacing-md); }
.chart-row:last-child { margin-bottom: 0; }

/* 月度最佳宿舍 */
.top-rooms-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: var(--spacing-sm); }
.top-room-card { display: flex; align-items: center; gap: var(--spacing-md); padding: var(--spacing-md); border: 1px solid var(--color-border); border-radius: var(--radius-lg); transition: all var(--transition-normal); }
.top-room-card:hover { border-color: var(--color-primary); box-shadow: var(--shadow-sm); transform: translateY(-1px); }
.top-room-card--podium { border-color: var(--color-warning); background: linear-gradient(135deg, #fff9e6, #fff); }
.top-room-rank { width: 44px; text-align: center; flex-shrink: 0; }
.rank-medal { font-size: 28px; }
.rank-number { font-size: var(--font-size-xl); font-weight: var(--font-weight-bold); color: var(--color-text-secondary); }
.top-room-body { flex: 1; min-width: 0; }
.top-room-name { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-text-primary); margin-bottom: 4px; }
.top-room-meta { display: flex; align-items: baseline; gap: var(--spacing-xs); font-size: var(--font-size-xs); color: var(--color-text-secondary); margin-bottom: 6px; }
.top-room-score { font-size: var(--font-size-xl); font-weight: var(--font-weight-bold); color: var(--color-primary); }

@media (max-width: 768px) {
  .stats-row > * { max-width: 100% !important; flex: 1 1 100% !important; }
}
</style>
