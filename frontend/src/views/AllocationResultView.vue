<template>
  <div class="view-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分配结果查看</span>
          <span v-if="currentBatch" class="batch-info">
            当前批次：{{ currentBatch.name }}
            <el-tag :type="statusType(currentBatch.matchStatus)" size="small" style="margin-left: 8px;">
              {{ statusLabel(currentBatch.matchStatus) }}
            </el-tag>
          </span>
        </div>
      </template>

      <div class="filter-bar">
        <el-select v-model="selectedBatchId" placeholder="选择批次" @change="onBatchChange" style="width: 260px;">
          <el-option v-for="b in batches" :key="b.id" :label="b.name" :value="b.id" />
        </el-select>
        <el-select v-model="filterBuildingId" placeholder="楼栋" clearable @change="loadRooms" style="width: 160px; margin-left: 10px;">
          <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
        </el-select>
        <el-input v-model="filterRoomNumber" placeholder="房号（支持模糊搜索）" clearable @input="loadRooms" style="width: 200px; margin-left: 10px;" />
        <el-radio-group v-model="filterOccupancy" @change="loadRooms" style="margin-left: 10px;">
          <el-radio-button value="all">全部</el-radio-button>
          <el-radio-button value="empty">空闲</el-radio-button>
          <el-radio-button value="partial">部分入住</el-radio-button>
          <el-radio-button value="full">已满</el-radio-button>
        </el-radio-group>
      </div>

      <div v-if="rooms.length > 0" class="room-grid">
        <el-card
          v-for="room in rooms"
          :key="room.id"
          shadow="hover"
          class="room-card"
          :class="{ 'room-full': room.currentCount >= room.capacity, 'room-empty': room.currentCount === 0 }"
          @click="openRoomDetail(room)"
        >
          <div class="room-header">
            <span class="building-name">{{ room.buildingName }}</span>
            <el-tag v-if="room.specialTag" size="small" type="danger">{{ room.specialTag }}</el-tag>
          </div>
          <div class="room-number">{{ room.roomNumber }}</div>
          <div class="room-floor">{{ room.floor }}层 · {{ room.roomType || '4人间' }}</div>
          <div class="room-occupancy">
            <el-progress
              :percentage="room.capacity ? Math.round(room.currentCount / room.capacity * 100) : 0"
              :stroke-width="10"
              :color="occupancyColor(room)"
            />
          </div>
          <div class="room-count">
            <span class="count-text">{{ room.currentCount }}/{{ room.capacity }}</span>
            <span class="count-label">已入住/总床位</span>
          </div>
        </el-card>
      </div>

      <el-empty v-else-if="selectedBatchId" description="该批次无符合条件的房源" />
    </el-card>

    <!-- 床位详情弹窗 -->
    <el-dialog v-model="roomDialogVisible" :title="'房间 ' + selectedRoom?.roomNumber + ' 床位详情'" width="500px">
      <div v-if="beds.length > 0" class="bed-list">
        <div
          v-for="bed in beds"
          :key="bed.bedId"
          class="bed-item"
          :class="{ 'bed-occupied': bed.isOccupied, 'bed-empty': !bed.isOccupied }"
          @click="bed.student ? showStudentDetail(bed) : null"
        >
          <div class="bed-info">
            <span class="bed-number">床位 {{ bed.bedNumber }}</span>
            <el-tag :type="bed.bedType === 'window' ? 'success' : 'info'" size="small">
              {{ bed.bedType === 'window' ? '靠窗' : '靠走廊' }}
            </el-tag>
          </div>
          <div v-if="bed.student" class="bed-student">
            <el-icon><User /></el-icon>
            <span>{{ bed.student.name }}</span>
            <el-tag :type="statusType(bed.student.allocationStatus)" size="small" style="margin-left: 6px;">
              {{ statusLabel(bed.student.allocationStatus) }}
            </el-tag>
          </div>
          <div v-else class="bed-student empty-text">空闲</div>
        </div>
      </div>
      <el-empty v-else description="暂无床位数据" />
    </el-dialog>

    <!-- 学生详情弹窗 -->
    <el-dialog v-model="studentDialogVisible" title="学生信息" width="400px">
      <el-descriptions v-if="selectedStudent" :column="1" border>
        <el-descriptions-item label="学号">{{ selectedStudent.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ selectedStudent.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ selectedStudent.gender }}</el-descriptions-item>
        <el-descriptions-item label="专业ID">{{ selectedStudent.majorId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="匹配度">{{ selectedStudent.matchScore || '-' }}</el-descriptions-item>
        <el-descriptions-item label="分配状态">
          <el-tag :type="statusType(selectedStudent.allocationStatus)" size="small">
            {{ statusLabel(selectedStudent.allocationStatus) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { dormBatchAPI } from '@/api/dormBatch'
import { buildingAPI } from '@/api/building'
import { allocationResultAPI } from '@/api/allocationResult'

const batches = ref([])
const buildings = ref([])
const selectedBatchId = ref(null)
const currentBatch = ref(null)
const rooms = ref([])
const beds = ref([])
const selectedRoom = ref(null)
const selectedStudent = ref(null)

const filterBuildingId = ref(null)
const filterRoomNumber = ref('')
const filterOccupancy = ref('all')

const roomDialogVisible = ref(false)
const studentDialogVisible = ref(false)

const statusType = (s) => ({
  pending: 'info', running: 'success', matching: 'warning', confirming: '', finished: 'warning', archived: 'info', cancelled: 'danger',
  recommended: 'info', confirmed: 'success', auto_confirmed: 'success', manual_assigned: 'warning', adjusted: 'warning'
}[s] || 'info')

const statusLabel = (s) => ({
  pending: '待启动', running: '运行中', matching: '匹配中', confirming: '确认中', finished: '已结束', archived: '已归档', cancelled: '已作废',
  recommended: '推荐', confirmed: '已确认', auto_confirmed: '自动确认', manual_assigned: '手动分配', adjusted: '已调换'
}[s] || s)

const occupancyColor = (room) => {
  if (room.currentCount === 0) return '#909399'
  if (room.currentCount >= room.capacity) return '#f56c6c'
  return '#409EFF'
}

const loadBatches = async () => {
  try {
    const res = await dormBatchAPI.list({})
    batches.value = res.data || []
  } catch (e) { /* ignore */ }
}

const loadBuildings = async () => {
  try {
    const res = await buildingAPI.list()
    buildings.value = res.data || []
  } catch (e) { /* ignore */ }
}

const onBatchChange = () => {
  currentBatch.value = batches.value.find(b => b.id === selectedBatchId.value) || null
  loadRooms()
}

const loadRooms = async () => {
  if (!selectedBatchId.value) return
  try {
    const params = {
      buildingId: filterBuildingId.value || undefined,
      roomNumber: filterRoomNumber.value || undefined,
      occupancyStatus: filterOccupancy.value === 'all' ? undefined : filterOccupancy.value
    }
    const res = await allocationResultAPI.viewRooms(selectedBatchId.value, params)
    rooms.value = res.data || []
  } catch (e) {
    ElMessage.error('加载房源失败')
  }
}

const openRoomDetail = async (room) => {
  selectedRoom.value = room
  roomDialogVisible.value = true
  try {
    const res = await allocationResultAPI.viewRoomBeds(selectedBatchId.value, room.id)
    beds.value = res.data?.beds || []
  } catch (e) {
    beds.value = []
  }
}

const showStudentDetail = (bed) => {
  selectedStudent.value = bed.student
  studentDialogVisible.value = true
}

onMounted(() => {
  loadBatches()
  loadBuildings()
})
</script>

<style scoped>
.view-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.batch-info { font-size: 14px; color: #606266; }
.filter-bar { display: flex; align-items: center; margin-bottom: 15px; }

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 15px;
}

.room-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.room-card:hover { transform: translateY(-2px); }
.room-card.room-full { border-left: 4px solid #f56c6c; }
.room-card.room-empty { border-left: 4px solid #909399; }
.room-card:not(.room-full):not(.room-empty) { border-left: 4px solid #409EFF; }

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.building-name { font-size: 12px; color: #909399; }
.room-number { font-size: 24px; font-weight: bold; color: #303133; }
.room-floor { font-size: 12px; color: #909399; margin-top: 4px; }
.room-occupancy { margin-top: 10px; }
.room-count {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 6px;
}
.count-text { font-size: 16px; font-weight: bold; color: #303133; }
.count-label { font-size: 11px; color: #c0c4cc; }

.bed-list { display: flex; flex-direction: column; gap: 10px; }
.bed-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: default;
}
.bed-item.bed-occupied { background: #f0f9eb; border: 1px solid #e1f3d8; }
.bed-item.bed-empty { background: #f5f7fa; border: 1px solid #e4e7ed; }
.bed-item.bed-occupied:has(.bed-student) { cursor: pointer; }
.bed-info { display: flex; align-items: center; gap: 8px; }
.bed-number { font-weight: 600; font-size: 15px; }
.bed-student { display: flex; align-items: center; gap: 4px; font-size: 14px; }
.bed-student.empty-text { color: #c0c4cc; }
</style>
