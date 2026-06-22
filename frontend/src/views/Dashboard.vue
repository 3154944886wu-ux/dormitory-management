<template>
  <div class="page-container">
    <!-- 统计卡片（单一 Grid 容器，保证所有卡片宽度一致） -->
    <el-row :gutter="16" class="stats-grid">
      <el-col :xs="12" :sm="6" v-for="card in allStatCards" :key="card.label">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: card.gradient }">
              <el-icon :size="28"><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value stat-number">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 中间三卡片：快捷操作 / 最新公告 / 房间入住 -->
    <el-row :gutter="16" class="middle-row">
      <!-- 快捷操作 -->
      <el-col :xs="24" :md="8">
        <el-card class="panel-card">
          <template #header><span class="panel-title">快捷操作</span></template>
          <div class="quick-actions">
            <el-button v-for="act in quickActions" :key="act.label" :type="act.type" @click="$router.push(act.path)">
              <el-icon><component :is="act.icon" /></el-icon>{{ act.label }}
            </el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 最新公告 -->
      <el-col :xs="24" :md="8">
        <el-card class="panel-card">
          <template #header>
            <div class="panel-header">
              <span class="panel-title">最新公告</span>
              <el-button type="primary" link size="small" @click="$router.push('/admin/announcements')">查看全部</el-button>
            </div>
          </template>
          <div class="announcement-list" v-loading="loadingAnnouncements">
            <div v-for="item in announcements" :key="item.id" class="announcement-item" @click="viewAnnouncement(item)">
              <div class="announcement-title">
                <el-tag v-if="item.isTop === 1" type="danger" size="small">置顶</el-tag>
                <el-tag :type="getTypeTag(item.type)" size="small">{{ getTypeLabel(item.type) }}</el-tag>
                <span>{{ item.title }}</span>
              </div>
              <div class="announcement-time">{{ formatDate(item.publishTime) }}</div>
            </div>
            <el-empty v-if="announcements.length === 0" description="暂无公告" :image-size="80" />
          </div>
        </el-card>
      </el-col>

      <!-- 房间入住 -->
      <el-col :xs="24" :md="8">
        <el-card class="panel-card">
          <template #header><span class="panel-title">房间入住情况</span></template>
          <div class="room-stats">
            <div class="room-item" v-for="r in roomStats" :key="r.label">
              <div class="room-item__header">
                <span class="room-item__label">{{ r.label }}</span>
                <span class="room-item__count">{{ r.count }} 间</span>
              </div>
              <el-progress :percentage="r.pct" :stroke-width="16" :status="r.status" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部：报修 + 水电费 -->
    <el-row :gutter="16" class="bottom-row">
      <el-col :xs="24" :md="12">
        <el-card class="panel-card">
          <template #header><span class="panel-title">报修状态分布</span></template>
          <div class="repair-stats">
            <div class="repair-item" v-for="r in repairList" :key="r.label">
              <div class="repair-item__header">
                <el-tag :type="r.tagType" size="small">{{ r.label }}</el-tag>
                <span class="repair-item__count">{{ r.count }}</span>
              </div>
              <el-progress :percentage="r.pct" :show-text="false" :color="r.color" />
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card class="panel-card">
          <template #header><span class="panel-title">水电费概览</span></template>
          <div class="utility-stats">
            <div class="utility-item" v-for="u in utilityList" :key="u.label">
              <span class="utility-item__label">{{ u.label }}</span>
              <span class="utility-item__value" :style="{ color: u.color }">¥{{ u.value }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 安全检查概览 -->
    <el-row :gutter="16" class="bottom-row" v-if="inspectionStats.totalChecks > 0">
      <el-col :span="24">
        <el-card class="panel-card">
          <template #header>
            <div class="panel-header">
              <span class="panel-title">安全检查概览（本月）</span>
              <el-button type="primary" link size="small" @click="$router.push('/admin/inspection/statistics')">查看详情</el-button>
            </div>
          </template>
          <el-row :gutter="16">
            <el-col :xs="12" :sm="6">
              <div style="text-align:center; padding:var(--spacing-md);">
                <div style="font-size:var(--font-size-xxl);font-weight:600;color:var(--color-primary);">{{ inspectionStats.totalChecks }}</div>
                <div style="font-size:var(--font-size-xs);color:var(--color-text-secondary);">本月检查次</div>
              </div>
            </el-col>
            <el-col :xs="12" :sm="6">
              <div style="text-align:center; padding:var(--spacing-md);">
                <div style="font-size:var(--font-size-xxl);font-weight:600;" :style="{color: inspectionStats.passRate >= 80 ? 'var(--color-success)' : 'var(--color-danger)'}">{{ inspectionStats.passRate }}%</div>
                <div style="font-size:var(--font-size-xs);color:var(--color-text-secondary);">合格率</div>
              </div>
            </el-col>
            <el-col :xs="12" :sm="6">
              <div style="text-align:center; padding:var(--spacing-md);">
                <div style="font-size:var(--font-size-xxl);font-weight:600;color:var(--color-text-primary);">{{ inspectionStats.avgScore }}</div>
                <div style="font-size:var(--font-size-xs);color:var(--color-text-secondary);">平均分</div>
              </div>
            </el-col>
            <el-col :xs="12" :sm="6">
              <div style="text-align:center; padding:var(--spacing-md);">
                <div style="font-size:var(--font-size-xxl);font-weight:600;color:var(--color-warning);">{{ inspectionStats.pending }}</div>
                <div style="font-size:var(--font-size-xs);color:var(--color-text-secondary);">待整改</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 公告详情 -->
    <el-dialog v-model="announcementDialogVisible" title="公告详情" width="600px">
      <div v-if="currentAnnouncement" class="announcement-detail">
        <h2>{{ currentAnnouncement.title }}</h2>
        <div class="meta">
          <el-tag :type="getTypeTag(currentAnnouncement.type)">{{ getTypeLabel(currentAnnouncement.type) }}</el-tag>
          <span>{{ currentAnnouncement.publisherName }}</span>
          <span>{{ formatDate(currentAnnouncement.publishTime) }}</span>
        </div>
        <el-divider />
        <div class="content">{{ currentAnnouncement.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  OfficeBuilding, House, User, Tools, Avatar, Coin,
  Bell, Calendar, Checked, TrendCharts, Clock
} from '@element-plus/icons-vue'
import announcementAPI from '@/api/announcement'
import api from '@/utils/api'

const loadingAnnouncements = ref(false)
const announcements = ref([])
const currentAnnouncement = ref(null)
const announcementDialogVisible = ref(false)

const stats = reactive({
  buildingCount: 0, roomCount: 0, studentCount: 0,
  freeRooms: 0, partialRooms: 0, fullRooms: 0, pendingRepairs: 0
})
const repairStats = reactive({ pending: 0, processing: 0, completed: 0, closed: 0 })
const utilityStats = reactive({ waterFee: '0.00', electricFee: '0.00', paidAmount: '0.00', unpaidAmount: '0.00' })
const dormStats = reactive({ activeBatches: undefined, totalAllocated: 0, avgMatchScore: '0.0', pendingConfirm: 0 })
const inspectionStats = reactive({ totalChecks: 0, passRate: 0, avgScore: '0', pending: 0 })

// 合并所有统计卡片（单一数据源，保证宽度一致）
const allStatCards = computed(() => {
  const cards = [
    { icon: OfficeBuilding, value: stats.buildingCount, label: '楼栋总数', gradient: 'linear-gradient(135deg, #667eea, #764ba2)' },
    { icon: House, value: stats.roomCount, label: '房间总数', gradient: 'linear-gradient(135deg, #11998e, #38ef7d)' },
    { icon: User, value: stats.studentCount, label: '入住学生', gradient: 'linear-gradient(135deg, #f093fb, #f5576c)' },
    { icon: Tools, value: stats.pendingRepairs, label: '待处理报修', gradient: 'linear-gradient(135deg, #fa709a, #fee140)' }
  ]
  if (dormStats.activeBatches !== undefined) {
    cards.push(
      { icon: Calendar, value: dormStats.activeBatches, label: '活跃批次', gradient: 'linear-gradient(135deg, #667eea, #764ba2)' },
      { icon: Checked, value: dormStats.totalAllocated, label: '总分配人数', gradient: 'linear-gradient(135deg, #11998e, #38ef7d)' },
      { icon: TrendCharts, value: dormStats.avgMatchScore, label: '平均匹配度', gradient: 'linear-gradient(135deg, #f093fb, #f5576c)' },
      { icon: Clock, value: dormStats.pendingConfirm, label: '待确认人数', gradient: 'linear-gradient(135deg, #fa709a, #fee140)' }
    )
  }
  return cards
})

const quickActions = [
  { label: '楼栋管理', path: '/admin/buildings', type: 'primary', icon: OfficeBuilding },
  { label: '房间管理', path: '/admin/rooms', type: 'success', icon: House },
  { label: '学生管理', path: '/admin/students', type: 'warning', icon: User },
  { label: '访客登记', path: '/admin/visitors', type: '', icon: Avatar },
  { label: '报修管理', path: '/admin/repairs', type: 'danger', icon: Tools },
  { label: '水电费用', path: '/admin/utility-fees', type: 'info', icon: Coin },
  { label: '公告通知', path: '/admin/announcements', type: '', icon: Bell },
  { label: '批次管理', path: '/admin/dorm-batches', type: 'primary', icon: Calendar }
]

const roomTotal = computed(() => stats.roomCount || 1)
const roomStats = computed(() => [
  { label: '空闲房间', count: stats.freeRooms, pct: Math.round((stats.freeRooms / roomTotal.value) * 100), status: 'success' },
  { label: '部分入住', count: stats.partialRooms, pct: Math.round((stats.partialRooms / roomTotal.value) * 100), status: '' },
  { label: '满员房间', count: stats.fullRooms, pct: Math.round((stats.fullRooms / roomTotal.value) * 100), status: 'exception' }
])

const repairTotal = computed(() =>
  (repairStats.pending || 0) + (repairStats.processing || 0) +
  (repairStats.completed || 0) + (repairStats.closed || 0) || 1
)
const repairList = computed(() => [
  { label: '待处理', count: repairStats.pending || 0, tagType: 'info', color: '#909399', pct: Math.round(((repairStats.pending || 0) / repairTotal.value) * 100) },
  { label: '处理中', count: repairStats.processing || 0, tagType: 'warning', color: '#e6a23c', pct: Math.round(((repairStats.processing || 0) / repairTotal.value) * 100) },
  { label: '已完成', count: repairStats.completed || 0, tagType: 'primary', color: '#409eff', pct: Math.round(((repairStats.completed || 0) / repairTotal.value) * 100) },
  { label: '已关闭', count: repairStats.closed || 0, tagType: 'success', color: '#67c23a', pct: Math.round(((repairStats.closed || 0) / repairTotal.value) * 100) }
])

const utilityList = computed(() => [
  { label: '本月应收水费', value: utilityStats.waterFee || '0.00', color: 'var(--color-text-primary)' },
  { label: '本月应收电费', value: utilityStats.electricFee || '0.00', color: 'var(--color-text-primary)' },
  { label: '本月已缴费', value: utilityStats.paidAmount || '0.00', color: 'var(--color-success)' },
  { label: '本月待缴费', value: utilityStats.unpaidAmount || '0.00', color: 'var(--color-danger)' }
])

const getTypeTag = (t) => ({ 0: 'info', 1: 'warning', 2: 'danger' }[t] || 'info')
const getTypeLabel = (t) => ({ 0: '普通', 1: '重要', 2: '紧急' }[t] || '未知')
const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }) : '-'

const viewAnnouncement = async (item) => {
  try { const r = await announcementAPI.getById(item.id); currentAnnouncement.value = r.data; announcementDialogVisible.value = true } catch { ElMessage.error('加载失败') }
}

const fetchJson = async (url) => { const r = await api.get(url); return r.data || r }
const loadStats = async () => { try { const d = await fetchJson('/dashboard/overview'); Object.assign(stats, d) } catch {} }
const loadRepairStats = async () => { try { const d = await fetchJson('/dashboard/repair'); Object.assign(repairStats, d) } catch {} }
const loadUtilityStats = async () => { try { const d = await fetchJson('/dashboard/utility'); Object.assign(utilityStats, d) } catch {} }
const loadAnnouncements = async () => {
  loadingAnnouncements.value = true
  try { const r = await announcementAPI.getPublished(); announcements.value = (r.data || []).slice(0, 5) } catch {}
  finally { loadingAnnouncements.value = false }
}
const loadDormStats = async () => { try { const d = await fetchJson('/dashboard/dorm-stats'); Object.assign(dormStats, d) } catch {} }

const loadInspectionStats = async () => {
  try {
    const now = new Date(); const m = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}`
    const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate()
    const r = await api.get('/inspection/records/search', { params: { startDate: m + '-01', endDate: m + '-' + lastDay } })
    const records = r.data || []
    inspectionStats.totalChecks = records.length
    const pass = records.filter(x => x.result === 'PASS').length
    inspectionStats.passRate = records.length > 0 ? Math.round(pass / records.length * 100) : 0
    const scores = records.filter(x => x.overallScore != null).map(x => Number(x.overallScore))
    inspectionStats.avgScore = scores.length > 0 ? (scores.reduce((a,b)=>a+b,0)/scores.length).toFixed(1) : '0'
    inspectionStats.pending = records.filter(x => x.rectificationStatus === 'PENDING').length
  } catch {}
}

onMounted(() => { loadStats(); loadRepairStats(); loadUtilityStats(); loadAnnouncements(); loadDormStats(); loadInspectionStats() })
</script>

<style scoped>
/* 统计卡片 Grid */
.stats-grid { margin-bottom: var(--spacing-md); }
.middle-row { margin-bottom: var(--spacing-md); }
.bottom-row { margin-bottom: 0; }

/* 单张统计卡片 */
.stat-card { border: 1px solid var(--color-border); border-radius: var(--radius-lg); }
.stat-card :deep(.el-card__body) { padding: var(--spacing-lg); }
.stat-content { display: flex; align-items: center; gap: var(--spacing-md); }
.stat-icon {
  width: 56px; height: 56px; border-radius: var(--radius-lg);
  display: flex; align-items: center; justify-content: center; color: white; flex-shrink: 0;
}
.stat-info { flex: 1; min-width: 0; }
.stat-value { font-size: var(--font-size-xxl); font-weight: var(--font-weight-bold); color: var(--color-text-primary); }
.stat-label { font-size: var(--font-size-sm); color: var(--color-text-secondary); margin-top: 2px; }

/* 三列面板卡片 — 统一高度 */
.panel-card {
  height: 100%;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
}
.panel-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  flex: 1;
}
.panel-header { display: flex; justify-content: space-between; align-items: center; }
.panel-title { font-size: var(--font-size-base); font-weight: var(--font-weight-semibold); }

/* 快捷操作 — 两列网格等宽对齐 */
.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-xs);
}
.quick-actions .el-button {
  justify-content: flex-start;
  height: 40px;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 公告列表 */
.announcement-list { flex: 1; min-height: 0; }
.announcement-item { padding: var(--spacing-sm) 0; border-bottom: 1px solid var(--color-border-light); cursor: pointer; transition: background var(--transition-fast); }
.announcement-item:last-child { border-bottom: none; }
.announcement-item:hover { background: var(--color-bg-hover); }
.announcement-title { display: flex; align-items: center; gap: var(--spacing-xs); font-size: var(--font-size-sm); color: var(--color-text-primary); }
.announcement-title .el-tag { flex-shrink: 0; }
.announcement-time { font-size: var(--font-size-xs); color: var(--color-text-secondary); margin-top: 4px; }

/* 房间入住 */
.room-item { margin-bottom: var(--spacing-md); }
.room-item:last-child { margin-bottom: 0; }
.room-item__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; }
.room-item__label { font-size: var(--font-size-sm); color: var(--color-text-regular); }
.room-item__count { font-size: var(--font-size-sm); color: var(--color-text-secondary); }

/* 报修 */
.repair-item { margin-bottom: var(--spacing-sm); }
.repair-item:last-child { margin-bottom: 0; }
.repair-item__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; }
.repair-item__count { font-weight: var(--font-weight-bold); color: var(--color-text-primary); }

/* 水电费 */
.utility-item { display: flex; justify-content: space-between; padding: var(--spacing-sm) 0; border-bottom: 1px solid var(--color-border-light); }
.utility-item:last-child { border-bottom: none; }
.utility-item__label { color: var(--color-text-regular); font-size: var(--font-size-sm); }
.utility-item__value { font-size: var(--font-size-lg); font-weight: var(--font-weight-bold); }

/* 公告详情 */
.announcement-detail h2 { margin: 0 0 var(--spacing-md); font-size: var(--font-size-xl); color: var(--color-text-primary); }
.announcement-detail .meta { display: flex; align-items: center; gap: var(--spacing-sm); color: var(--color-text-secondary); font-size: var(--font-size-sm); }
.announcement-detail .content { line-height: var(--line-height-relaxed); white-space: pre-wrap; color: var(--color-text-regular); }

/* 响应式 */
@media (max-width: 768px) {
  .stat-content { gap: var(--spacing-sm); }
  .stat-icon { width: 44px; height: 44px; }
  .stat-value { font-size: var(--font-size-xl); }
}
</style>
