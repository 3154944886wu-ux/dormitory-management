<template>
  <div class="leave-request-container">
    <el-row :gutter="20">
      <!-- 左侧：申请表单 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>提交请假申请</span>
          </template>
          
          <el-form 
            ref="formRef" 
            :model="form" 
            :rules="rules" 
            label-width="100px"
          >
            <el-form-item label="请假类型" prop="leaveType">
              <el-select v-model="form.leaveType" placeholder="请选择请假类型">
                <el-option label="事假" :value="1" />
                <el-option label="病假" :value="2" />
                <el-option label="其他" :value="3" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="form.startTime"
                type="datetime"
                placeholder="选择开始时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
            
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="form.endTime"
                type="datetime"
                placeholder="选择结束时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
            
            <el-form-item label="请假原因" prop="reason">
              <el-input
                v-model="form.reason"
                type="textarea"
                :rows="4"
                placeholder="请输入请假原因"
              />
            </el-form-item>
            
            <el-form-item label="联系方式" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="外出期间联系电话" />
            </el-form-item>
            
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" placeholder="紧急联系人姓名及电话" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSubmit" :loading="submitting">
                提交申请
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <!-- 右侧：我的请假记录 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>我的请假记录</span>
              <el-tag>共 {{ total }} 条记录</el-tag>
            </div>
          </template>
          
          <el-table :data="requests" stripe v-loading="loading">
            <el-table-column prop="leaveType" label="类型" width="80">
              <template #default="{ row }">
                {{ getLeaveTypeText(row.leaveType) }}
              </template>
            </el-table-column>
            <el-table-column label="时间" width="200">
              <template #default="{ row }">
                <div>{{ formatDateTime(row.startTime) }}</div>
                <div>至 {{ formatDateTime(row.endTime) }}</div>
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="原因" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="approverName" label="审批人" width="100" />
            <el-table-column prop="approverNote" label="审批意见" show-overflow-tooltip />
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 0"
                  type="danger"
                  size="small"
                  @click="handleCancel(row)"
                >
                  撤销
                </el-button>
                <el-button
                  v-if="row.status === 1 && row.actualReturnTime === null"
                  type="success"
                  size="small"
                  @click="handleConfirmReturn(row)"
                >
                  销假
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="loadRequests"
            @current-change="loadRequests"
          />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  submitLeaveRequest, 
  getMyLeaveRequests, 
  cancelLeaveRequest,
  confirmReturn 
} from '@/api/leaveRequest'

const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)
const requests = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const form = ref({
  leaveType: null,
  startTime: '',
  endTime: '',
  reason: '',
  contactPhone: '',
  emergencyContact: ''
})

const rules = {
  leaveType: [
    { required: true, message: '请选择请假类型', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请输入请假原因', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系方式', trigger: 'blur' }
  ]
}

// 提交申请
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  // 验证时间
  if (new Date(form.value.startTime) >= new Date(form.value.endTime)) {
    ElMessage.error('结束时间必须晚于开始时间')
    return
  }
  
  submitting.value = true
  try {
    const res = await submitLeaveRequest(form.value)
    if (res.success) {
      ElMessage.success('提交成功，等待审批')
      resetForm()
      loadRequests()
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } catch (error) {
    ElMessage.error('提交失败：' + error.message)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
}

// 撤销申请
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要撤销这条请假申请吗？', '确认', {
      type: 'warning'
    })
    
    const res = await cancelLeaveRequest(row.id)
    if (res.success) {
      ElMessage.success('撤销成功')
      loadRequests()
    } else {
      ElMessage.error(res.message || '撤销失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('撤销失败：' + error.message)
    }
  }
}

// 销假
const handleConfirmReturn = async (row) => {
  try {
    await ElMessageBox.confirm('确认已经返回学校？', '销假确认', {
      type: 'info'
    })
    
    const res = await confirmReturn(row.id)
    if (res.success) {
      ElMessage.success('销假成功')
      loadRequests()
    } else {
      ElMessage.error(res.message || '销假失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('销假失败：' + error.message)
    }
  }
}

// 加载请假记录
const loadRequests = async () => {
  loading.value = true
  try {
    const res = await getMyLeaveRequests()
    requests.value = res.data || []
    total.value = res.data?.length || 0
  } catch (error) {
    console.error('加载记录失败', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取请假类型文本
const getLeaveTypeText = (type) => {
  const texts = { 1: '事假', 2: '病假', 3: '其他' }
  return texts[type] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = { 0: '待审批', 1: '已批准', 2: '已拒绝', 3: '已撤销' }
  return texts[status] || '未知'
}

onMounted(() => {
  loadRequests()
})
</script>

<style scoped>
.leave-request-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>