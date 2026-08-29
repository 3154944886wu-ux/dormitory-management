<template>
  <div class="check-in-page">
    <el-card class="mobile-card">
      <div class="hero">
        <div class="date">{{ currentDate }}</div>
        <div class="time">{{ currentTime }}</div>
        <el-tag size="large" :type="statusMeta.type">{{ statusMeta.text }}</el-tag>
      </div>

      <el-alert
        v-if="locationError"
        :title="locationError"
        type="error"
        show-icon
        :closable="false"
        class="mt"
      />
      <el-alert
        v-else-if="currentLocation"
        :title="`定位成功：${currentLocation.latitude.toFixed(6)}, ${currentLocation.longitude.toFixed(6)}`"
        type="success"
        show-icon
        :closable="false"
        class="mt"
      >
        <template #default>精度约 {{ Math.round(currentLocation.accuracy) }} 米</template>
      </el-alert>
      <el-alert
        v-else
        title="打卡需要浏览器定位权限，请在宿舍电子围栏范围内操作"
        type="info"
        show-icon
        :closable="false"
        class="mt"
      />

      <el-button
        type="primary"
        size="large"
        class="check-button"
        :loading="checking || locating"
        :disabled="todayStatus.checkedIn || todayStatus.status === 3 || todayStatus.status == null"
        @click="handleCheckIn"
      >
        {{ checkButtonText }}
      </el-button>

      <div v-if="rule" class="rule">
        <div>打卡时段：{{ rule.checkStartTime }} 起至未归截止 {{ rule.absentDeadline || '-' }}</div>
        <div>{{ rule.checkEndTime }} 前为已归，之后至未归截止为晚归</div>
        <div>围栏半径：{{ rule.allowedRadius || 500 }} 米</div>
        <div>最大定位误差：{{ rule.maxLocationAccuracy || 200 }} 米</div>
      </div>
    </el-card>

    <el-card class="records-card">
      <template #header>
        <div class="card-header">
          <span>我的归寝记录</span>
          <el-button link type="primary" @click="loadRecords">刷新</el-button>
        </div>
      </template>

      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="checkDate" label="日期" width="110" />
        <el-table-column prop="checkTime" label="打卡时间" min-width="170">
          <template #default="{ row }">{{ formatDateTime(row.checkTime) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusInfo(row.status).type">{{ statusInfo(row.status).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="locationAccuracy" label="定位精度" width="110">
          <template #default="{ row }">{{ row.locationAccuracy ? `${Math.round(row.locationAccuracy)}米` : '-' }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { checkIn, getMyRecords, getTodayStatus } from '@/api/checkIn'

const currentTime = ref('')
const currentDate = ref('')
const checking = ref(false)
const locating = ref(false)
const loading = ref(false)
const currentLocation = ref(null)
const locationError = ref('')
const todayStatus = ref({ checkedIn: false, status: 0 })
const records = ref([])
const rule = ref(null)

let timer = null

const statusInfo = (status) => {
  const map = {
    0: { text: '已归', type: 'success' },
    1: { text: '晚归', type: 'warning' },
    2: { text: '未归', type: 'danger' },
    3: { text: '请假', type: 'info' }
  }
  return map[Number(status)] || { text: '未知', type: 'info' }
}

const statusMeta = computed(() => {
  if (todayStatus.value.status === 3) {
    return statusInfo(3)
  }
  if (!todayStatus.value.checkedIn && (todayStatus.value.status === 0 || todayStatus.value.status === 1)) {
    return todayStatus.value.status === 1
      ? statusInfo(1)
      : { text: '待打卡', type: 'info' }
  }
  if (!todayStatus.value.checkedIn && (todayStatus.value.status === null || todayStatus.value.status === undefined)) {
    return { text: '非打卡时段', type: 'info' }
  }
  return statusInfo(todayStatus.value.status)
})

const checkButtonText = computed(() => {
  if (todayStatus.value.status === 3) return '请假中无需打卡'
  if (todayStatus.value.checkedIn) return '今日已打卡'
  if (todayStatus.value.status === null || todayStatus.value.status === undefined) return '非打卡时段'
  if (rule.value && rule.value.requireLocation === 0) return '立即打卡'
  return '获取定位并打卡'
})

const updateClock = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
}

const getLocation = () => new Promise((resolve, reject) => {
  if (!navigator.geolocation) {
    reject(new Error('当前浏览器不支持定位功能'))
    return
  }
  locating.value = true
  locationError.value = ''
  navigator.geolocation.getCurrentPosition(
    (position) => {
      const coords = {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
        accuracy: position.coords.accuracy
      }
      currentLocation.value = coords
      locating.value = false
      resolve(coords)
    },
    (error) => {
      locating.value = false
      const messages = {
        1: '定位权限被拒绝，请在浏览器设置中允许定位',
        2: '无法获取当前位置，请检查定位服务是否开启',
        3: '获取定位超时，请重试'
      }
      locationError.value = messages[error.code] || error.message || '定位失败'
      reject(new Error(locationError.value))
    },
    { enableHighAccuracy: true, timeout: 12000, maximumAge: 0 }
  )
})

const handleCheckIn = async () => {
  checking.value = true
  try {
    const requireLocation = rule.value == null || rule.value.requireLocation !== 0
    const location = requireLocation
      ? await getLocation()
      : { latitude: null, longitude: null, accuracy: null }
    const res = await checkIn({
      checkType: 0,
      latitude: location.latitude,
      longitude: location.longitude,
      locationAccuracy: location.accuracy,
      deviceInfo: navigator.userAgent
    })
    ElMessage.success(res.message || '打卡成功')
    await Promise.all([loadTodayStatus(), loadRecords()])
  } catch (error) {
    ElMessage.error(error.message || '打卡失败')
  } finally {
    checking.value = false
  }
}

const loadTodayStatus = async () => {
  const res = await getTodayStatus()
  const data = res.data || { checkedIn: false, status: 0 }
  todayStatus.value = data
  if (data.rule) {
    rule.value = data.rule
  }
}

const loadRecords = async () => {
  loading.value = true
  try {
    const res = await getMyRecords()
    records.value = res.data || []
  } finally {
    loading.value = false
  }
}

const formatDateTime = (value) => {
  if (!value) return '-'
  return new Date(value).toLocaleString('zh-CN')
}

onMounted(() => {
  updateClock()
  timer = setInterval(updateClock, 1000)
  loadTodayStatus()
  loadRecords()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.check-in-page {
  display: grid;
  gap: 20px;
  padding: 16px;
}

.mobile-card {
  max-width: 560px;
  width: 100%;
  margin: 0 auto;
}

.hero {
  text-align: center;
}

.date {
  color: #606266;
}

.time {
  font-size: 44px;
  font-weight: 700;
  color: #409eff;
  margin: 8px 0 14px;
}

.mt {
  margin-top: 16px;
}

.check-button {
  width: 100%;
  height: 56px;
  margin-top: 18px;
  font-size: 18px;
}

.rule {
  margin-top: 18px;
  line-height: 1.9;
  color: #606266;
  font-size: 14px;
}

.records-card {
  width: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

@media (max-width: 768px) {
  .check-in-page {
    padding: 10px;
  }

  .time {
    font-size: 36px;
  }
}
</style>
