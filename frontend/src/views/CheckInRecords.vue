<template>
  <div class="check-in-records">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>打卡记录管理</span>
          <el-button type="success" @click="handleExport">导出</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="学生姓名">
          <el-input v-model="searchForm.studentName" placeholder="请输入学生姓名" clearable />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="searchForm.studentNo" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="请选择楼栋" clearable>
            <el-option
              v-for="building in buildings"
              :key="building.id"
              :label="building.name"
              :value="building.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="打卡日期">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="打卡状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="已归" :value="0" />
            <el-option label="晚归" :value="1" />
            <el-option label="未归" :value="2" />
            <el-option label="请假" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="buildingName" label="楼栋" width="100" />
        <el-table-column prop="roomNumber" label="房间" width="80" />
        <el-table-column prop="checkDate" label="打卡日期" width="120" />
        <el-table-column prop="checkTime" label="打卡时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.checkTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="checkType" label="打卡方式" width="100">
          <template #default="{ row }">
            <el-tag :type="getCheckTypeTag(row.checkType)">
              {{ getCheckTypeText(row.checkType) }}
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
        <el-table-column label="打卡位置" min-width="180">
          <template #default="{ row }">
            <div v-if="row.latitude && row.longitude" class="location-info">
              <el-icon><Location /></el-icon>
              <span>{{ row.latitude.toFixed(6) }}, {{ row.longitude.toFixed(6) }}</span>
              <el-button
                v-if="row.latitude && row.longitude"
                type="primary"
                link
                size="small"
                @click="showLocationOnMap(row)"
              >
                查看地图
              </el-button>
            </div>
            <span v-else class="no-location">未记录位置</span>
          </template>
        </el-table-column>
        <el-table-column prop="deviceInfo" label="设备信息" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tooltip :content="row.deviceInfo" placement="top">
              <span class="device-info">{{ getDeviceShort(row.deviceInfo) }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadRecords"
        @current-change="loadRecords"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 地图查看对话框 -->
    <el-dialog v-model="mapDialogVisible" title="打卡位置" width="600px">
      <div v-if="selectedRecord" class="map-dialog-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="学生">{{ selectedRecord.studentName }} ({{ selectedRecord.studentNo }})</el-descriptions-item>
          <el-descriptions-item label="打卡时间">{{ formatTime(selectedRecord.checkTime) }}</el-descriptions-item>
          <el-descriptions-item label="经纬度">{{ selectedRecord.latitude?.toFixed(6) }}, {{ selectedRecord.longitude?.toFixed(6) }}</el-descriptions-item>
          <el-descriptions-item label="定位精度">约 {{ Math.round(selectedRecord.locationAccuracy || 0) }} 米</el-descriptions-item>
        </el-descriptions>
        <div class="map-container">
          <div class="map-placeholder">
            <el-icon :size="48"><Location /></el-icon>
            <p>坐标：{{ selectedRecord.latitude?.toFixed(6) }}, {{ selectedRecord.longitude?.toFixed(6) }}</p>
            <el-link :href="getMapUrl(selectedRecord)" target="_blank" type="primary">
              在高德地图中查看
            </el-link>
            <el-link :href="getBaiduMapUrl(selectedRecord)" target="_blank" type="primary" style="margin-left: 10px;">
              在百度地图中查看
            </el-link>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Location } from '@element-plus/icons-vue'
import api from '@/utils/api'

const loading = ref(false)
const records = ref([])
const buildings = ref([])
const page = ref(1)
const size = ref(20)
const total = ref(0)

const searchForm = reactive({
  studentName: '',
  studentNo: '',
  buildingId: '',
  dateRange: [],
  status: ''
})

const mapDialogVisible = ref(false)
const selectedRecord = ref(null)

// 加载楼栋列表
const loadBuildings = async () => {
  try {
    const res = await api.get('/buildings')
    buildings.value = res.data || res
  } catch (error) {
    console.error('加载楼栋失败:', error)
  }
}

// 加载打卡记录
const loadRecords = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value
    }
    if (searchForm.studentName) params.studentName = searchForm.studentName
    if (searchForm.studentNo) params.studentNo = searchForm.studentNo
    if (searchForm.buildingId !== '' && searchForm.buildingId != null) {
      params.buildingId = searchForm.buildingId
    }
    if (searchForm.status !== '' && searchForm.status != null) {
      params.status = searchForm.status
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }

    const res = await api.get('/checkin/records', { params })
    records.value = res.data?.records || res.records || []
    total.value = res.data?.total || res.total || 0
  } catch (error) {
    ElMessage.error(error.message || '加载打卡记录失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadRecords()
}

const resetSearch = () => {
  searchForm.studentName = ''
  searchForm.studentNo = ''
  searchForm.buildingId = ''
  searchForm.dateRange = []
  searchForm.status = ''
  handleSearch()
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const getCheckTypeText = (type) => {
  const types = {
    0: '定位打卡',
    1: '人脸打卡',
    2: '手动打卡'
  }
  return types[type] || '未知'
}

const getCheckTypeTag = (type) => {
  const tags = {
    0: 'success',
    1: 'primary',
    2: 'warning'
  }
  return tags[type] || 'info'
}

const getStatusText = (status) => {
  const statuses = {
    0: '已归',
    1: '晚归',
    2: '未归',
    3: '请假'
  }
  return statuses[status] || '未知'
}

const getStatusType = (status) => {
  const types = {
    0: 'success',
    1: 'warning',
    2: 'danger',
    3: 'info'
  }
  return types[status] || 'info'
}

const handleExport = async () => {
  const params = {}
  if (searchForm.studentName) params.studentName = searchForm.studentName
  if (searchForm.studentNo) params.studentNo = searchForm.studentNo
  if (searchForm.buildingId !== '' && searchForm.buildingId != null) {
    params.buildingId = searchForm.buildingId
  }
  if (searchForm.status !== '' && searchForm.status != null) {
    params.status = searchForm.status
  }
  if (searchForm.dateRange && searchForm.dateRange.length === 2) {
    params.startDate = searchForm.dateRange[0]
    params.endDate = searchForm.dateRange[1]
  }
  const blob = await api.get('/checkin/export', { params, responseType: 'blob' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '归寝打卡记录.csv'
  a.click()
  URL.revokeObjectURL(url)
}

const getDeviceShort = (deviceInfo) => {
  if (!deviceInfo) return '-'
  // 简化显示设备信息
  if (deviceInfo.includes('Android')) return 'Android设备'
  if (deviceInfo.includes('iPhone')) return 'iPhone'
  if (deviceInfo.includes('Windows')) return 'Windows电脑'
  if (deviceInfo.includes('Mac')) return 'Mac电脑'
  return '其他设备'
}

const showLocationOnMap = (record) => {
  selectedRecord.value = record
  mapDialogVisible.value = true
}

// 生成高德地图链接
const getMapUrl = (record) => {
  if (!record.latitude || !record.longitude) return '#'
  return `https://uri.amap.com/marker?position=${record.longitude},${record.latitude}&name=打卡位置`
}

// 生成百度地图链接
const getBaiduMapUrl = (record) => {
  if (!record.latitude || !record.longitude) return '#'
  return `https://api.map.baidu.com/marker?location=${record.latitude},${record.longitude}&title=打卡位置&output=html`
}

onMounted(() => {
  loadBuildings()
  loadRecords()
})
</script>

<style scoped>
.check-in-records {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.location-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

.no-location {
  color: #909399;
  font-size: 12px;
}

.device-info {
  color: #606266;
  font-size: 12px;
}

.map-dialog-content {
  padding: 10px 0;
}

.map-container {
  margin-top: 20px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
}

.map-placeholder {
  text-align: center;
  color: #606266;
}

.map-placeholder p {
  margin: 10px 0;
}
</style>