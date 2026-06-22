<template>
  <div class="allocation-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>房源划拨</span>
        </div>
      </template>

      <div class="filter-bar">
        <el-select v-model="filterAllocStatus" placeholder="划拨状态" clearable @change="loadBatches" style="width: 140px;">
          <el-option label="全部" value="" />
          <el-option label="已划拨" value="allocated" />
          <el-option label="未划拨" value="unallocated" />
        </el-select>
        <el-select v-model="selectedBatchId" placeholder="请选择批次" @change="onBatchChange" style="width: 280px; margin-left: 10px;">
          <el-option v-for="b in filteredBatches" :key="b.id" :label="`${b.name} (${b.collegeName}) - ${b.studentCount || 0}人`" :value="b.id" />
        </el-select>
        <el-tag v-if="selectedBatch" :type="batchStatusType" style="margin-left: 10px;">
          {{ batchStatusLabel }}
        </el-tag>
      </div>

      <div v-if="selectedBatchId" class="batch-info">
        该批次关联学生 <strong>{{ selectedBatch?.studentCount || 0 }}</strong> 人，
        至少需要划拨 <strong>{{ minRooms }}</strong> 间房（按每间4人估算）
      </div>

      <el-row :gutter="20" style="margin-top: 15px;" v-if="selectedBatchId">
        <el-col :span="12">
          <el-card shadow="never">
            <template #header>
              <div class="panel-header">
                <span>已划拨房源 ({{ assignedRooms.length }})</span>
                <el-button
                  type="danger"
                  size="small"
                  :disabled="selectedBatch?.matchStatus !== 'pending' || selectedRoomIds.length === 0"
                  @click="handleBatchRemove"
                >
                  移除选中
                </el-button>
              </div>
            </template>
            <el-table
              :data="assignedRooms"
              v-loading="assignedLoading"
              size="small"
              @selection-change="onAssignedSelect"
              max-height="450"
            >
              <el-table-column type="selection" width="45" />
              <el-table-column prop="roomNumber" label="房间号" width="100" />
              <el-table-column prop="buildingName" label="楼栋" width="120" />
              <el-table-column label="容量" width="70">
                <template #default="{ row }">
                  {{ row.currentCount || 0 }} / {{ row.capacity || '--' }}
                </template>
              </el-table-column>
            </el-table>
            <div v-if="assignedRooms.length === 0" class="empty-hint">暂无划拨房源</div>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card shadow="never">
            <template #header>
              <div class="panel-header">
                <span>可选房源 ({{ availableRooms.length }})</span>
              </div>
            </template>

            <div class="available-filters">
              <el-select v-model="availBuildingId" placeholder="楼栋" clearable @change="loadAvailable" size="small" style="width: 140px;">
                <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
              </el-select>
              <el-input-number v-model="availFloor" placeholder="楼层" :min="1" :max="20" clearable @change="loadAvailable" size="small" style="width: 100px; margin-left: 8px;" />
              <el-input-number v-model="minBeds" placeholder="最小可用床位" :min="1" :max="maxCapacity" size="small" style="width: 130px; margin-left: 8px;" />
              <el-button type="primary" size="small" @click="loadAvailable" style="margin-left: 8px;">查询</el-button>
            </div>

            <el-table
              :data="filteredAvailable"
              v-loading="availableLoading"
              size="small"
              @selection-change="onAvailableSelect"
              style="margin-top: 10px;"
              max-height="380"
            >
              <el-table-column type="selection" width="45" />
              <el-table-column prop="roomNumber" label="房间号" width="100" />
              <el-table-column prop="buildingName" label="楼栋" width="120" />
              <el-table-column prop="floor" label="楼层" width="60" />
              <el-table-column label="状态" width="90" align="center">
                <template #default="{ row }">
                  <el-tag v-if="row.currentCount > 0" type="warning" size="small">
                    已住{{ row.currentCount }}人
                  </el-tag>
                  <el-tag v-else type="success" size="small">空房</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="剩余床位" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.availableBeds >= 2 ? 'success' : 'warning'" size="small">
                    {{ row.availableBeds }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>

            <div style="margin-top: 12px; text-align: center;">
              <el-button
                type="primary"
                :disabled="selectedBatch?.matchStatus !== 'pending' || selectedAvailableIds.length === 0"
                @click="handleBatchAdd"
              >
                添加选中到房源池 ({{ selectedAvailableIds.length }})
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <div v-else class="empty-hint">请先选择一个批次</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { batchRoomAPI } from '@/api/batchRoom'
import { dormBatchAPI } from '@/api/dormBatch'
import { buildingAPI } from '@/api/building'

const batches = ref([])
const buildings = ref([])
const selectedBatchId = ref(null)
const assignedRooms = ref([])
const availableRooms = ref([])
const assignedLoading = ref(false)
const availableLoading = ref(false)
const selectedRoomIds = ref([])
const selectedAvailableIds = ref([])
const availBuildingId = ref(null)
const availFloor = ref(null)
const minBeds = ref(null)

const filterAllocStatus = ref('')
const selectedBatch = computed(() => batches.value.find(b => b.id === selectedBatchId.value))

const batchStatusType = computed(() => {
  const s = selectedBatch.value?.matchStatus
  return { pending: 'info', running: 'success', finished: 'warning' }[s] || 'info'
})

const batchStatusLabel = computed(() => {
  const s = selectedBatch.value?.matchStatus
  return { pending: '待启动', running: '运行中', finished: '已结束' }[s] || s
})

const filteredBatches = computed(() => {
  if (!filterAllocStatus.value) return batches.value
  if (filterAllocStatus.value === 'allocated')
    return batches.value.filter(b => (b.roomCount || 0) > 0)
  if (filterAllocStatus.value === 'unallocated')
    return batches.value.filter(b => (b.roomCount || 0) === 0)
  return batches.value
})

const minRooms = computed(() => {
  const count = selectedBatch.value?.studentCount || 0
  return Math.ceil(count / 4)
})

const maxCapacity = computed(() => {
  if (availableRooms.value.length === 0) return 10
  return Math.max(...availableRooms.value.map(r => r.capacity || 4))
})

const filteredAvailable = computed(() => {
  let list = availableRooms.value
  if (minBeds.value && minBeds.value > 0) {
    list = list.filter(r => r.availableBeds >= minBeds.value)
  }
  return list
})

const loadBatches = async () => {
  try {
    const res = await dormBatchAPI.list({})
    batches.value = res.data || []
  } catch (error) {
    ElMessage.error('加载批次列表失败')
  }
}

const loadBuildings = async () => {
  try {
    const res = await buildingAPI.list()
    buildings.value = res.data || []
  } catch (error) {
    // ignore
  }
}

const loadAssigned = async () => {
  if (!selectedBatchId.value) return
  assignedLoading.value = true
  try {
    const res = await batchRoomAPI.list(selectedBatchId.value)
    assignedRooms.value = res.data || []
  } catch (error) {
    ElMessage.error('加载已划拨房源失败')
  } finally {
    assignedLoading.value = false
  }
}

const loadAvailable = async () => {
  if (!selectedBatchId.value) return
  availableLoading.value = true
  try {
    const params = { batchId: selectedBatchId.value }
    if (availBuildingId.value) params.buildingId = availBuildingId.value
    if (availFloor.value) params.floor = availFloor.value
    const res = await batchRoomAPI.available(params)
    availableRooms.value = res.data || []
  } catch (error) {
    ElMessage.error('加载可选房源失败')
  } finally {
    availableLoading.value = false
  }
}

const onBatchChange = () => {
  selectedRoomIds.value = []
  selectedAvailableIds.value = []
  loadAssigned()
  loadAvailable()
}

const onAssignedSelect = (rows) => {
  selectedRoomIds.value = rows.map(r => r.id)
}

const onAvailableSelect = (rows) => {
  selectedAvailableIds.value = rows.map(r => r.id)
}

const handleBatchAdd = async () => {
  if (selectedAvailableIds.value.length === 0) return
  try {
    const res = await batchRoomAPI.addRooms(selectedBatchId.value, selectedAvailableIds.value)
    if (res.data.failed > 0) {
      let details = ''
      for (const [id, reason] of Object.entries(res.data.failureDetails || {})) {
        details += `房间ID ${id}: ${reason}\n`
      }
      ElMessage.warning(`成功添加 ${res.data.added} 个，失败 ${res.data.failed} 个\n${details}`)
    } else {
      ElMessage.success(`成功添加 ${res.data.added} 个房源`)
    }
    selectedAvailableIds.value = []
    loadAssigned()
    loadAvailable()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleBatchRemove = async () => {
  if (selectedRoomIds.value.length === 0) return
  try {
    await ElMessageBox.confirm(
      `确定要从房源池中移除选中的 ${selectedRoomIds.value.length} 个房间吗？`,
      '提示',
      { type: 'warning' }
    )
    await batchRoomAPI.removeRooms(selectedBatchId.value, selectedRoomIds.value)
    ElMessage.success('移除成功')
    selectedRoomIds.value = []
    loadAssigned()
    loadAvailable()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

onMounted(() => {
  loadBatches()
  loadBuildings()
})
</script>

<style scoped>
.allocation-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-bar {
  display: flex;
  align-items: center;
}

.batch-info {
  margin-top: 12px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.available-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.empty-hint {
  text-align: center;
  color: #c0c4cc;
  padding: 40px 0;
  font-size: 14px;
}
</style>
