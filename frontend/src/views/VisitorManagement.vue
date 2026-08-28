<template>
  <div class="visitor-management">
    <div class="page-header">
      <h2>访客管理</h2>
      <div class="header-actions">
        <el-input
          v-model="searchText"
          placeholder="搜索访客姓名"
          style="width: 200px; margin-right: 10px"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="filterStatus" placeholder="状态筛选" style="width: 120px; margin-right: 10px" @change="handleFilter">
          <el-option label="全部" :value="null" />
          <el-option label="在访" :value="1" />
          <el-option label="已离开" :value="0" />
        </el-select>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          登记访客
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ activeCount }}</div>
          <div class="stat-label">当前在访</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ visitors.length }}</div>
          <div class="stat-label">总记录数</div>
        </div>
      </el-card>
    </div>

    <!-- 访客列表 -->
    <el-card>
      <el-table :data="visitors" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="visitorName" label="访客姓名" width="120" />
        <el-table-column prop="visitorPhone" label="联系电话" width="130" />
        <el-table-column prop="buildingName" label="楼栋" width="100" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="relation" label="关系" width="100" />
        <el-table-column prop="purpose" label="来访目的" />
        <el-table-column label="来访时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.visitTime) }}
          </template>
        </el-table-column>
        <el-table-column label="离开时间" width="160">
          <template #default="{ row }">
            {{ row.leaveTime ? formatDateTime(row.leaveTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在访' : '已离开' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 1" 
              type="warning" 
              size="small"
              @click="handleLeave(row)"
            >
              离开登记
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
      :title="isEdit ? '编辑访客' : '登记访客'"
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
        <el-form-item label="访客姓名" prop="visitorName">
          <el-input v-model="form.visitorName" />
        </el-form-item>
        <el-form-item label="联系电话" prop="visitorPhone">
          <el-input v-model="form.visitorPhone" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.visitorIdCard" />
        </el-form-item>
        <el-form-item label="与被访人关系">
          <el-input v-model="form.relation" />
        </el-form-item>
        <el-form-item label="来访目的">
          <el-input v-model="form.purpose" type="textarea" />
        </el-form-item>
        <el-form-item label="来访时间" prop="visitTime">
          <el-date-picker
            v-model="form.visitTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.note" type="textarea" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import api from '@/utils/api'
import { buildingAPI } from '@/api/building'
import { getRooms } from '@/api/room'

const visitors = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const searchText = ref('')
const filterStatus = ref(null)
const activeCount = ref(0)
const selectedRoom = ref([])

// 房间选项
const roomOptions = ref([])

const form = reactive({
  id: null,
  roomId: null,
  visitorName: '',
  visitorPhone: '',
  visitorIdCard: '',
  relation: '',
  purpose: '',
  visitTime: null,
  note: ''
})

const rules = {
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  visitorName: [{ required: true, message: '请输入访客姓名', trigger: 'blur' }],
  visitorPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  visitTime: [{ required: true, message: '请选择来访时间', trigger: 'change' }]
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 16)
}

// 加载访客列表
const loadVisitors = async () => {
  try {
    const res = await api.get('/visitors')
    if (res.code === 200) {
      visitors.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载访客列表失败')
  }
}

// 加载在访数量
const loadActiveCount = async () => {
  try {
    const res = await api.get('/visitors/active/count')
    if (res.code === 200) {
      activeCount.value = res.data.count
    }
  } catch (error) {
    console.error('加载在访数量失败')
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
    loadVisitors()
    return
  }
  try {
    const res = await api.get(`/visitors?name=${searchText.value}`)
    if (res.code === 200) {
      visitors.value = res.data
    }
  } catch (error) {
    ElMessage.error('搜索失败')
  }
}

// 筛选
const handleFilter = async () => {
  if (filterStatus.value === null) {
    loadVisitors()
    return
  }
  try {
    const res = await api.get(`/visitors?status=${filterStatus.value}`)
    if (res.code === 200) {
      visitors.value = res.data
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
    visitorName: '',
    visitorPhone: '',
    visitorIdCard: '',
    relation: '',
    purpose: '',
    visitTime: new Date(),
    note: ''
  })
  selectedRoom.value = []
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(form, row)
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
        await api.put(`/visitors/${form.id}`, form)
        ElMessage.success('更新成功')
      } else {
        await api.post('/visitors', form)
        ElMessage.success('登记成功')
      }
      dialogVisible.value = false
      loadVisitors()
      loadActiveCount()
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  })
}

// 离开登记
const handleLeave = async (row) => {
  try {
    await ElMessageBox.confirm('确认该访客已离开？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await api.post(`/visitors/${row.id}/leave`)
    ElMessage.success('已登记离开')
    loadVisitors()
    loadActiveCount()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该访客记录？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await api.delete(`/visitors/${row.id}`)
    ElMessage.success('删除成功')
    loadVisitors()
    loadActiveCount()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadVisitors()
  loadActiveCount()
  loadRoomOptions()
})
</script>

<style scoped>
.visitor-management {
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
  color: #409eff;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
</style>