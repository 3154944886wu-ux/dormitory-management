<template>
  <div class="utility-fee-management">
    <div class="page-header">
      <h2>水电费管理</h2>
      <div class="header-actions">
        <el-select v-model="filterBuilding" placeholder="选择楼栋" clearable style="width: 150px; margin-right: 10px" @change="handleFilter">
          <el-option
            v-for="building in buildings"
            :key="building.id"
            :label="building.name"
            :value="building.id"
          />
        </el-select>
        <el-date-picker
          v-model="filterMonth"
          type="month"
          placeholder="选择月份"
          format="YYYY-MM"
          value-format="YYYY-MM"
          style="width: 150px; margin-right: 10px"
          @change="handleFilter"
        />
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增费用
        </el-button>
      </div>
    </div>

    <!-- 费用统计卡片 -->
    <div class="stats-row">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ totalElectric }}</div>
          <div class="stat-label">总电费 (元)</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ totalWater }}</div>
          <div class="stat-label">总水费 (元)</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ totalFee }}</div>
          <div class="stat-label">总费用 (元)</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ paidCount }}</div>
          <div class="stat-label">已缴费</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ unpaidCount }}</div>
          <div class="stat-label">待缴费</div>
        </div>
      </el-card>
    </div>

    <!-- 费用列表 -->
    <el-card>
      <el-table :data="utilityFees" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="房间号" width="150">
          <template #default="{ row }">
            {{ row.buildingName }} - {{ row.roomNumber }}
          </template>
        </el-table-column>
        <el-table-column prop="month" label="月份" width="100" />
        <el-table-column label="电费" width="100">
          <template #default="{ row }">
            ¥{{ (row.electricityFee != null ? row.electricityFee : 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="水费" width="100">
          <template #default="{ row }">
            ¥{{ row.waterFee?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column label="总费用" width="100">
          <template #default="{ row }">
            <span class="total-fee">¥{{ ((row.electricityFee || 0) + (row.waterFee || 0)).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '已缴费' : '待缴费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 0" 
              type="success" 
              size="small"
              @click="handlePay(row)"
            >
              确认缴费
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
      :title="isEdit ? '编辑费用' : '新增费用'"
      width="600px"
      destroy-on-close
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
        <el-form-item label="月份" prop="month">
          <el-date-picker
            v-model="form.month"
            type="month"
            placeholder="选择月份"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="电费" prop="electricFee">
          <el-input-number :model-value="feeElectric" @update:model-value="onElectricChange" :min="0" :precision="2" :step="10" style="width:100%" />
        </el-form-item>
        <el-form-item label="水费" prop="waterFee">
          <el-input-number :model-value="feeWater" @update:model-value="onWaterChange" :min="0" :precision="2" :step="10" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status" id="fee-status">
            <el-radio :value="0">待缴费</el-radio>
            <el-radio :value="1">已缴费</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="总费用">
          <span class="total-preview">¥{{ feeTotal }}</span>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getUtilityFees, createUtilityFee, updateUtilityFee, payUtilityFee, deleteUtilityFee } from '@/api/utilityFee'
import { buildingAPI } from '@/api/building'
import { getRooms } from '@/api/room'

const utilityFees = ref([])
const buildings = ref([])
const roomOptions = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const selectedRoom = ref([])
const feeElectric = ref(0)
const feeWater = ref(0)
const feeTotal = computed(() => {
  const e = Number(feeElectric.value) || 0
  const w = Number(feeWater.value) || 0
  return (e + w).toFixed(2)
})
// 输入变更处理
const onElectricChange = (v) => { feeElectric.value = Number(v) || 0 }
const onWaterChange = (v) => { feeWater.value = Number(v) || 0 }

const filterBuilding = ref(null)
const filterMonth = ref(null)

// 统计数据
const totalElectric = computed(() => {
  return utilityFees.value.reduce((sum, item) => sum + (item.electricityFee || 0), 0).toFixed(2)
})

const totalWater = computed(() => {
  return utilityFees.value.reduce((sum, item) => sum + (item.waterFee || 0), 0).toFixed(2)
})

const totalFee = computed(() => {
  return utilityFees.value.reduce((sum, item) => sum + (item.electricityFee || 0) + (item.waterFee || 0), 0).toFixed(2)
})

const paidCount = computed(() => utilityFees.value.filter(f => f.status === 1).length)
const unpaidCount = computed(() => utilityFees.value.filter(f => f.status === 0).length)

const form = reactive({
  id: null,
  roomId: null,
  month: null,
  electricFee: 0,
  waterFee: 0,
  status: 0
})

const rules = {
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  month: [{ required: true, message: '请选择月份', trigger: 'change' }],
  electricFee: [{ required: true, message: '请输入电费', trigger: 'blur' }],
  waterFee: [{ required: true, message: '请输入水费', trigger: 'blur' }]
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 16)
}

// 加载费用列表
const loadUtilityFees = async () => {
  try {
    const res = await getUtilityFees()
    if (res.code === 200) {
      utilityFees.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载费用列表失败')
  }
}

// 加载楼栋
const loadBuildings = async () => {
  try {
    const res = await buildingAPI.list()
    buildings.value = res.data || []
  } catch (error) {
    ElMessage.error('加载楼栋失败')
  }
}

// 加载房间选项
const loadRoomOptions = async () => {
  try {
    const [buildingsRes, roomsRes] = await Promise.all([
      buildingAPI.list(),
      getRooms({ pageSize: 1000 })
    ])
    const buildingsData = buildingsRes.data || []
    const rooms = roomsRes.data?.content || roomsRes.data?.list || []

    roomOptions.value = buildingsData.map(building => ({
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

// 筛选
const handleFilter = async () => {
  try {
    const params = {}
    if (filterBuilding.value) params.buildingId = filterBuilding.value
    if (filterMonth.value) params.month = filterMonth.value
    
    const res = await getUtilityFees(params)
    if (res.code === 200) {
      utilityFees.value = res.data
    }
  } catch (error) {
    ElMessage.error('筛选失败')
  }
}

// 显示新增对话框
const showAddDialog = () => {
  isEdit.value = false
  const now = new Date()
  const defaultMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`

  Object.assign(form, {
    id: null,
    roomId: null,
    month: defaultMonth,
    electricFee: 0,
    waterFee: 0,
    status: 0
  })
  // 只在按钮没被测试过时重置（测试按钮会改 feeElectric）
  // 正常新增时应该是 0
  selectedRoom.value = []
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEdit.value = true
  const monthStr = row.year && row.month
    ? `${row.year}-${String(row.month).padStart(2, '0')}`
    : row.month || null
  Object.assign(form, {
    id: row.id,
    roomId: row.roomId,
    month: monthStr,
    electricFee: row.electricityFee || 0,
    waterFee: row.waterFee || 0,
    status: row.status
  })
  feeElectric.value = row.electricityFee || 0
  feeWater.value = row.waterFee || 0
  // 设置级联选择器值
  if (row.buildingId && row.roomId) {
    selectedRoom.value = [row.buildingId, row.roomId]
  }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch {
    return // 校验不通过
  }

  // 提交前将输入值同步到 form
  form.electricFee = Number(feeElectric.value) || 0
  form.waterFee = Number(feeWater.value) || 0

  try {
    if (isEdit.value) {
      await updateUtilityFee(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createUtilityFee(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadUtilityFees()
  } catch (error) {
    ElMessage.error(error?.message || '操作失败')
  }
}

// 确认缴费
const handlePay = async (row) => {
  try {
    await ElMessageBox.confirm('确认该费用已缴费？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await payUtilityFee(row.id)
    ElMessage.success('缴费确认成功')
    loadUtilityFees()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该费用记录？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteUtilityFee(row.id)
    ElMessage.success('删除成功')
    loadUtilityFees()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadUtilityFees()
  loadBuildings()
  loadRoomOptions()
})
</script>

<style scoped>
.utility-fee-management {
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
  max-width: 180px;
}

.stat-content {
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.total-fee {
  font-weight: bold;
  color: #e6a23c;
}

.total-preview {
  font-size: 18px;
  font-weight: bold;
  color: #e6a23c;
}

.native-number-input {
  width: 100%; height: 32px; padding: 0 11px;
  border: 1px solid #dcdfe6; border-radius: 4px;
  font-size: 14px; color: #606266; outline: none;
  box-sizing: border-box;
}
.native-number-input:focus {
  border-color: #409eff; box-shadow: 0 0 0 2px rgba(64,158,255,0.15);
}
</style>