<template>
  <div class="repair-management">
    <div class="page-header">
      <h2>报修管理</h2>
      <div class="header-actions">
        <el-input
          v-model="searchText"
          placeholder="搜索房间号"
          style="width: 200px; margin-right: 10px"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="filterStatus" placeholder="状态筛选" style="width: 120px; margin-right: 10px" @change="handleFilter">
          <el-option label="全部" :value="null" />
          <el-option label="待处理" :value="0" />
          <el-option label="处理中" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="已关闭" :value="3" />
        </el-select>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增报修
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <el-card class="stat-card pending">
        <div class="stat-content">
          <div class="stat-number">{{ pendingCount }}</div>
          <div class="stat-label">待处理</div>
        </div>
      </el-card>
      <el-card class="stat-card processing">
        <div class="stat-content">
          <div class="stat-number">{{ processingCount }}</div>
          <div class="stat-label">处理中</div>
        </div>
      </el-card>
      <el-card class="stat-card completed">
        <div class="stat-content">
          <div class="stat-number">{{ completedCount }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </el-card>
      <el-card class="stat-card closed">
        <div class="stat-content">
          <div class="stat-number">{{ closedCount }}</div>
          <div class="stat-label">已关闭</div>
        </div>
      </el-card>
    </div>

    <!-- 报修列表 -->
    <el-card>
      <el-table :data="repairs" stripe style="width: 100%">
        <el-table-column prop="id" label="报修ID" width="80" />
        <el-table-column label="房间号" width="120">
          <template #default="{ row }">
            {{ row.buildingName }} - {{ row.roomNumber }}
          </template>
        </el-table-column>
        <el-table-column label="报修类型" width="120">
          <template #default="{ row }">
            {{ getRepairTypeName(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 0" 
              type="warning" 
              size="small"
              @click="handleStatusChange(row, 1)"
            >
              开始处理
            </el-button>
            <el-button 
              v-if="row.status === 1" 
              type="success" 
              size="small"
              @click="handleStatusChange(row, 2)"
            >
              完成处理
            </el-button>
            <el-button 
              v-if="row.status === 2" 
              type="info" 
              size="small"
              @click="handleStatusChange(row, 3)"
            >
              关闭
            </el-button>
            <el-button type="primary" size="small" @click="showEditDialog(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑报修' : '新增报修'"
      width="600px"
    >
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="房间" prop="roomId">
          <el-cascader
            v-model="selectedRoom"
            :options="roomOptions"
            :props="{ value: 'id', label: 'name', children: 'rooms' }"
            placeholder="选择楼栋/房间"
            style="width: 100%"
            @change="handleRoomChange"
          />
        </el-form-item>
        <el-form-item label="报修类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择报修类型" style="width: 100%">
            <el-option label="水电维修" value="水电维修" />
            <el-option label="门窗维修" value="门窗维修" />
            <el-option label="家具维修" value="家具维修" />
            <el-option label="网络问题" value="网络问题" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请描述报修问题" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已关闭" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getRepairs, createRepair, updateRepair, handleRepair, completeRepair, closeRepair, deleteRepair } from '@/api/repair'
import { buildingAPI } from '@/api/building'
import { getRooms } from '@/api/room'

const repairs = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const searchText = ref('')
const filterStatus = ref(null)
const selectedRoom = ref([])
const roomOptions = ref([])

// 统计数据
const pendingCount = computed(() => repairs.value.filter(r => r.status === 0).length)
const processingCount = computed(() => repairs.value.filter(r => r.status === 1).length)
const completedCount = computed(() => repairs.value.filter(r => r.status === 2).length)
const closedCount = computed(() => repairs.value.filter(r => r.status === 3).length)

const form = reactive({
  id: null,
  roomId: null,
  type: null,
  description: '',
  status: 0
})

const rules = {
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  type: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}

// 报修类型映射
const repairTypes = {
  1: '水电维修',
  2: '门窗维修',
  3: '家具维修',
  4: '网络问题',
  5: '其他'
}

const getRepairTypeName = (type) => repairTypes[type] || type || '未知'

// 状态映射
const getStatusType = (status) => {
  const types = { 0: 'danger', 1: 'warning', 2: 'success', 3: 'info' }
  return types[status] || 'info'
}

const getStatusName = (status) => {
  const names = { 0: '待处理', 1: '处理中', 2: '已完成', 3: '已关闭' }
  return names[status] || '未知'
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 16)
}

// 加载报修列表
const loadRepairs = async () => {
  try {
    const res = await getRepairs()
    if (res.code === 200) {
      repairs.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载报修列表失败')
  }
}

// 加载房间选项
const loadRoomOptions = async () => {
  try {
    const [buildingsRes, roomsRes] = await Promise.all([
      buildingAPI.list(),
      getRooms({ pageSize: 1000 })
    ])
    const buildings = buildingsRes.data || []
    const rooms = roomsRes.data?.content || roomsRes.data?.list || []

    roomOptions.value = buildings.map(building => ({
      id: building.id,
      name: building.name,
      rooms: rooms.filter(r => r.buildingId === building.id).map(r => ({
        id: r.id,
        name: r.roomNumber
      }))
    }))
  } catch (error) {
    ElMessage.error('加载房间选项失败')
  }
}

// 房间选择改变
const handleRoomChange = (value) => {
  if (value && value.length === 2) {
    form.roomId = value[1]
  }
}

// 搜索
const handleSearch = async () => {
  if (!searchText.value) {
    loadRepairs()
    return
  }
  try {
    const res = await getRepairs({ roomNumber: searchText.value })
    if (res.code === 200) {
      repairs.value = res.data
    }
  } catch (error) {
    ElMessage.error('搜索失败')
  }
}

// 筛选
const handleFilter = async () => {
  if (filterStatus.value === null) {
    loadRepairs()
    return
  }
  try {
    const res = await getRepairs({ status: filterStatus.value })
    if (res.code === 200) {
      repairs.value = res.data
    }
  } catch (error) {
    ElMessage.error('筛选失败')
  }
}

// 显示新增对话框
const showAddDialog = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    roomId: null,
    type: null,
    description: '',
    status: 0
  })
  selectedRoom.value = []
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    roomId: row.roomId,
    type: row.type,
    description: row.description,
    status: row.status
  })
  // 设置级联选择器值
  if (row.buildingId && row.roomId) {
    selectedRoom.value = [row.buildingId, row.roomId]
  }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      if (isEdit.value) {
        await updateRepair(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await createRepair(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadRepairs()
    } catch (error) {
      ElMessage.error(error.message || '操作失败')
    }
  })
}

// 状态更新
const handleStatusChange = async (row, status) => {
  try {
    const statusNames = { 1: '开始处理', 2: '完成处理', 3: '关闭' }
    const statusName = statusNames[status] || '更新'
    await ElMessageBox.confirm(`确认${statusName}该报修？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    if (status === 1) {
      await handleRepair(row.id, '管理员', null)
    } else if (status === 2) {
      await completeRepair(row.id, null)
    } else if (status === 3) {
      await closeRepair(row.id, null)
    }
    ElMessage.success('状态更新成功')
    loadRepairs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该报修记录？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteRepair(row.id)
    ElMessage.success('删除成功')
    loadRepairs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadRepairs()
  loadRoomOptions()
})
</script>

<style scoped>
.repair-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
}

.stats-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  max-width: 200px;
}

.stat-content {
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.pending .stat-number { color: #f56c6c; }
.processing .stat-number { color: #e6a23c; }
.completed .stat-number { color: #67c23a; }
.closed .stat-number { color: #909399; }
</style>