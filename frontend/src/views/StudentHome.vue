<template>
  <div class="student-home">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <div class="welcome-text">
              <h2>欢迎回来，{{ studentInfo.name || '同学' }}！</h2>
              <p>{{ today }} · {{ greeting }}</p>
            </div>
            <div class="check-in-btn">
              <el-button type="success" size="large" @click="goToCheckIn">
                <el-icon><Clock /></el-icon>
                归寝打卡
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 今日打卡状态 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Clock /></el-icon>
              <span>今日打卡</span>
            </div>
          </template>
          <div class="status-card">
            <div v-if="todayCheckIn?.checkedIn" class="checked">
              <el-icon :size="48" color="#67C23A"><CircleCheckFilled /></el-icon>
              <p>已打卡</p>
              <p class="time">{{ todayCheckIn.checkTime }}</p>
            </div>
            <div v-else class="not-checked">
              <el-icon :size="48" color="#E6A23C"><WarningFilled /></el-icon>
              <p>未打卡</p>
              <el-button type="primary" @click="goToCheckIn" style="margin-top: 10px">
                立即打卡
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 本月打卡统计 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Calendar /></el-icon>
              <span>本月打卡</span>
            </div>
          </template>
          <div class="stats-card">
            <div class="month-stat-item" v-for="item in monthStatItems" :key="item.key">
              <div class="month-stat-head">
                <span class="month-stat-label">{{ item.label }}</span>
                <span class="month-stat-value" :style="{ color: item.color }">{{ item.value }}</span>
              </div>
              <el-progress
                :percentage="item.percent"
                :color="item.color"
                :stroke-width="10"
                :show-text="false"
              />
            </div>
            <el-empty v-if="monthTotal === 0" description="本月暂无打卡记录" :image-size="64" />
          </div>
        </el-card>
      </el-col>
      
      <!-- 待处理事项 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Bell /></el-icon>
              <span>待处理</span>
            </div>
          </template>
          <div class="todo-card">
            <div class="todo-item clickable" v-if="dormTodo.survey" @click="goToMyRoom">
              <span>待填写选宿问卷</span>
              <el-badge value="1" type="danger" />
            </div>
            <div class="todo-item clickable" v-if="dormTodo.confirm" @click="goToMyRoom">
              <span>待确认分配结果</span>
              <el-badge value="1" type="danger" />
            </div>
            <div class="todo-item" v-if="pendingLeave > 0">
              <span>待审批请假</span>
              <el-badge :value="pendingLeave" type="warning" />
            </div>
            <div class="todo-item" v-if="pendingRepair > 0">
              <span>待处理报修</span>
              <el-badge :value="pendingRepair" type="warning" />
            </div>
            <div v-if="!dormTodo.survey && !dormTodo.confirm && pendingLeave === 0 && pendingRepair === 0" class="empty">
              <el-icon :size="32" color="#67C23A"><CircleCheckFilled /></el-icon>
              <p>暂无待处理事项</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 最近公告 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Notification /></el-icon>
              <span>最近公告</span>
              <el-button text type="primary" @click="goToAnnouncements">查看全部</el-button>
            </div>
          </template>
          <div class="announcement-list">
            <div v-for="item in announcements" :key="item.id" class="announcement-item">
              <div class="title">{{ item.title }}</div>
              <div class="date">{{ item.createTime }}</div>
            </div>
            <el-empty v-if="announcements.length === 0" description="暂无公告" />
          </div>
        </el-card>
      </el-col>
      
      <!-- 我的宿舍信息 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><House /></el-icon>
              <span>我的宿舍</span>
              <el-button text type="primary" @click="goToMyRoom">详细信息</el-button>
            </div>
          </template>
          <div class="room-info" v-if="roomInfo">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="楼栋">{{ roomInfo.buildingName }}</el-descriptions-item>
              <el-descriptions-item label="房间号">{{ roomInfo.roomNumber }}</el-descriptions-item>
              <el-descriptions-item label="床位">{{ roomInfo.bedNumber }}</el-descriptions-item>
              <el-descriptions-item label="室友">{{ roomInfo.roommateNames || '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>
          <el-empty v-else description="暂未分配宿舍" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Clock, CircleCheckFilled, WarningFilled, Calendar, Bell, Notification, House } from '@element-plus/icons-vue'
import { studentAPI } from '@/api/student'
import { dormSelectionAPI } from '@/api/dormSelection'

const router = useRouter()

const today = new Date().toLocaleDateString('zh-CN', { 
  year: 'numeric', 
  month: 'long', 
  day: 'numeric',
  weekday: 'long'
})

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，注意休息'
  if (hour < 9) return '早上好，新的一天开始了'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好，记得归寝打卡'
  return '夜深了，早点休息'
})

const studentInfo = ref({})
const todayCheckIn = ref(null)
const monthStats = reactive({ normal: 0, late: 0, absent: 0, leave: 0 })

const monthTotal = computed(() => monthStats.normal + monthStats.late + monthStats.absent + monthStats.leave)

const monthStatItems = computed(() => {
  const total = monthTotal.value || 1
  return [
    { key: 'normal', label: '已归', value: monthStats.normal, color: '#52C41A', percent: Math.round(monthStats.normal / total * 100) },
    { key: 'late', label: '晚归', value: monthStats.late, color: '#E6A23C', percent: Math.round(monthStats.late / total * 100) },
    { key: 'absent', label: '未归', value: monthStats.absent, color: '#F56C6C', percent: Math.round(monthStats.absent / total * 100) },
    { key: 'leave', label: '请假', value: monthStats.leave, color: '#909399', percent: Math.round(monthStats.leave / total * 100) }
  ]
})
const pendingLeave = ref(0)
const pendingRepair = ref(0)
const announcements = ref([])
const roomInfo = ref(null)
const dormTodo = reactive({ survey: false, confirm: false })

const goToCheckIn = () => router.push('/student/check-in')
const goToAnnouncements = () => router.push('/student/announcements')
const goToMyRoom = () => router.push('/student/my-room')

const loadData = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    
    // 并行请求（allSettled：单项失败不影响其他）
    const results = await Promise.allSettled([
      studentAPI.getProfile(),
      studentAPI.getTodayCheckIn(),
      studentAPI.getMonthStats(),
      studentAPI.getMyRoom(),
      studentAPI.getAnnouncements(3)
    ])
    const ok = (i) => results[i].status === 'fulfilled' ? results[i].value?.data : null

    studentInfo.value = ok(0) || {}
    todayCheckIn.value = ok(1)
    const statsData = ok(2) || []
    if (Array.isArray(statsData)) {
      monthStats.normal = statsData.filter(r => Number(r.status) === 0).length
      monthStats.late = statsData.filter(r => Number(r.status) === 1).length
      monthStats.absent = statsData.filter(r => Number(r.status) === 2).length
      monthStats.leave = statsData.filter(r => Number(r.status) === 3).length
    } else if (statsData) {
      monthStats.normal = statsData.normal || statsData.normalCount || 0
      monthStats.late = statsData.late || statsData.lateCount || 0
      monthStats.absent = statsData.absent || statsData.absentCount || 0
      monthStats.leave = statsData.leave || statsData.leaveCount || 0
    }
    roomInfo.value = ok(3)
    announcements.value = ok(4) || []
  } catch (error) {
    console.error('加载数据失败', error)
  }
}

const loadDormTodo = async () => {
  try {
    const res = await dormSelectionAPI.mySurvey()
    const data = res.data
    const st = data.batch?.matchStatus
    const submitted = data.student?.hasSubmitted
    const allocStatus = data.allocation?.status
    dormTodo.survey = st === 'running' && !submitted
    dormTodo.confirm = st === 'confirming' && allocStatus === 'recommended'
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  loadData()
  loadDormTodo()
})
</script>

<style scoped>
.student-home {
  padding: 0;
}

.welcome-card {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
  color: white;
}

.welcome-card :deep(.el-card__body) {
  padding: 30px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.welcome-text p {
  margin: 0;
  opacity: 0.9;
}

.check-in-btn .el-button {
  padding: 15px 30px;
  font-size: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-header .el-button {
  margin-left: auto;
}

.status-card {
  text-align: center;
  padding: 20px 0;
}

.status-card .checked p,
.status-card .not-checked p {
  margin: 10px 0;
}

.status-card .time {
  color: #909399;
  font-size: 14px;
}

.stats-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 6px 0 2px;
  min-height: 120px;
}

.month-stat-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.month-stat-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.month-stat-label {
  font-size: 14px;
  color: #606266;
}

.month-stat-value {
  font-size: 18px;
  font-weight: 600;
}

.todo-card {
  min-height: 100px;
}

.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.todo-item:last-child {
  border-bottom: none;
}

.todo-item.clickable {
  cursor: pointer;
}

.todo-item.clickable:hover {
  background-color: #f5f7fa;
}

.todo-card .empty {
  text-align: center;
  padding: 30px 0;
  color: #909399;
}

.announcement-list {
  max-height: 200px;
  overflow-y: auto;
}

.announcement-item {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-item:hover .title {
  color: #409EFF;
}

.announcement-item .title {
  font-weight: 500;
  margin-bottom: 5px;
}

.announcement-item .date {
  font-size: 12px;
  color: #909399;
}
</style>