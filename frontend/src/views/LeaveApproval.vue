<template>
  <div class="leave-approval-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>请假审批</span>
          <div class="header-actions">
            <el-select 
              v-model="filters.status" 
              placeholder="审批状态" 
              clearable
              style="width: 120px; margin-right: 10px;"
            >
              <el-option label="待审批" value="0" />
              <el-option label="已批准" value="1" />
              <el-option label="已拒绝" value="2" />
            </el-select>
            <el-button type="primary" @click="loadRequests">查询</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="requests" stripe v-loading="loading">
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="leaveType" label="请假类型" width="80">
          <template #default="{ row }">
            {{ getLeaveTypeText(row.leaveType) }}
          </template>
        </el-table-column>
        <el-table-column label="请假时间" width="220">
          <template #default="{ row }">
            <div>起：{{ formatDateTime(row.startTime) }}</div>
            <div>止：{{ formatDateTime(row.endTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="80">
          <template #default="{ row }">
            {{ calculateDuration(row.startTime, row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="请假原因" show-overflow-tooltip />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" size="small" @click="handleApprove(row)">
                批准
              </el-button>
              <el-button type="danger" size="small" @click="handleReject(row)">
                拒绝
              </el-button>
            </template>
            <template v-else>
              <el-button type="info" size="small" link @click="handleView(row)">
                查看
              </el-button>
            </template>
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
    
    <!-- 审批对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学生">{{ currentRequest.studentName }} ({{ currentRequest.studentNo }})</el-descriptions-item>
        <el-descriptions-item label="请假类型">{{ getLeaveTypeText(currentRequest.leaveType) }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDateTime(currentRequest.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDateTime(currentRequest.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="请假时长">{{ calculateDuration(currentRequest.startTime, currentRequest.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="请假原因">{{ currentRequest.reason }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRequest.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ currentRequest.emergencyContact || '-' }}</el-descriptions-item>
      </el-descriptions>
      
      <el-form 
        v-if="actionType !== 'view'"
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="100px"
        style="margin-top: 20px;"
      >
        <el-form-item label="审批意见" prop="approverNote">
          <el-input 
            v-model="form.approverNote" 
            type="textarea" 
            :rows="3"
            :placeholder="actionType === 'approve' ? '批准意见（可选）' : '拒绝原因'"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button 
          v-if="actionType !== 'view'"
          :type="actionType === 'approve' ? 'success' : 'danger'"
          @click="submitApproval"
          :loading="submitting"
        >
          {{ actionType === 'approve' ? '确认批准' : '确认拒绝' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getLeaveRequestsForApproval, 
  getLeaveRequests,
  getLeaveRequestsByStatus,
  approveLeaveRequest, 
  rejectLeaveRequest 
} from '@/api/leaveRequest'

const loading = ref(false)
const submitting = ref(false)
const requests = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const filters = reactive({
  status: ''
})

const dialogVisible = ref(false)
const actionType = ref('approve') // approve, reject, view
const currentRequest = ref({})
const formRef = ref(null)

const form = ref({
  approverNote: ''
})

const rules = {
  approverNote: [
    { 
      required: true, 
      message: '请输入拒绝原因', 
      trigger: 'blur',
      validator: (rule, value, callback) => {
        if (actionType.value === 'reject' && !value) {
          callback(new Error('请输入拒绝原因'))
        } else {
          callback()
        }
      }
    }
  ]
}

const dialogTitle = computed(() => {
  const titles = {
    approve: '批准请假',
    reject: '拒绝请假',
    view: '请假详情'
  }
  return titles[actionType.value]
})

// 加载请假申请列表
const loadRequests = async () => {
  loading.value = true
  try {
    // 待审批(0) 用分页 pending 接口；指定其它状态用按状态查询；未选状态用分页全部查询
    let res
    if (filters.status === '' || filters.status === null || filters.status === undefined) {
      res = await getLeaveRequests({ page: page.value, size: size.value })
    } else if (String(filters.status) === '0') {
      res = await getLeaveRequestsForApproval({ page: page.value, size: size.value })
    } else {
      res = await getLeaveRequestsByStatus(filters.status)
    }
    requests.value = res.data?.records || res.data?.list || res.data || []
    total.value = res.total ?? res.data?.total ?? requests.value.length
  } catch (error) {
    ElMessage.error('加载请假申请列表失败')
  } finally {
    loading.value = false
  }
}

// 批准
const handleApprove = (row) => {
  currentRequest.value = row
  actionType.value = 'approve'
  form.value = { approverNote: '' }
  dialogVisible.value = true
}

// 拒绝
const handleReject = (row) => {
  currentRequest.value = row
  actionType.value = 'reject'
  form.value = { approverNote: '' }
  dialogVisible.value = true
}

// 查看
const handleView = (row) => {
  currentRequest.value = row
  actionType.value = 'view'
  dialogVisible.value = true
}

// 提交审批
const submitApproval = async () => {
  if (actionType.value === 'reject') {
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return
  }
  
  submitting.value = true
  try {
    let res
    if (actionType.value === 'approve') {
      res = await approveLeaveRequest(currentRequest.value.id, form.value.approverNote)
    } else {
      res = await rejectLeaveRequest(currentRequest.value.id, form.value.approverNote)
    }
    
    if (res.success) {
      ElMessage.success(actionType.value === 'approve' ? '批准成功' : '拒绝成功')
      dialogVisible.value = false
      loadRequests()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  } finally {
    submitting.value = false
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

// 计算请假时长
const calculateDuration = (startTime, endTime) => {
  if (!startTime || !endTime) return '-'
  const start = new Date(startTime)
  const end = new Date(endTime)
  const diff = end - start
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  
  if (days > 0) {
    return `${days}天${hours}小时`
  }
  return `${hours}小时`
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
.leave-approval-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>