<template>
  <div class="check-exception-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考勤异常记录</span>
          <div class="header-actions">
            <el-select 
              v-model="filters.type" 
              placeholder="异常类型" 
              clearable
              style="width: 120px; margin-right: 10px;"
            >
              <el-option label="迟到" value="1" />
              <el-option label="缺卡" value="2" />
              <el-option label="早退" value="3" />
            </el-select>
            <el-select 
              v-model="filters.status" 
              placeholder="处理状态" 
              clearable
              style="width: 120px; margin-right: 10px;"
            >
              <el-option label="待处理" value="0" />
              <el-option label="已处理" value="1" />
            </el-select>
            <el-button type="primary" @click="loadExceptions">查询</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="exceptions" stripe v-loading="loading">
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="buildingName" label="楼栋" width="100" />
        <el-table-column prop="roomNo" label="房间" width="80" />
        <el-table-column prop="checkDate" label="日期" width="120" />
        <el-table-column prop="exceptionType" label="异常类型" width="80">
          <template #default="{ row }">
            <el-tag :type="getExceptionTypeTag(row.exceptionType)">
              {{ getExceptionTypeText(row.exceptionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="exceptionTime" label="打卡时间" width="100">
          <template #default="{ row }">
            {{ row.exceptionTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="exceptionReason" label="异常原因" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handleNote" label="处理意见" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 0"
              type="primary" 
              size="small" 
              link 
              @click="handleProcess(row)"
            >
              处理
            </el-button>
            <el-button type="info" size="small" link @click="handleView(row)">
              详情
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
        @size-change="loadExceptions"
        @current-change="loadExceptions"
      />
    </el-card>
    
    <!-- 处理对话框 -->
    <el-dialog v-model="processDialogVisible" title="处理异常" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学生">{{ currentException.studentName }} ({{ currentException.studentNo }})</el-descriptions-item>
        <el-descriptions-item label="房间">{{ currentException.buildingName }} - {{ currentException.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ currentException.checkDate }}</el-descriptions-item>
        <el-descriptions-item label="异常类型">
          <el-tag :type="getExceptionTypeTag(currentException.exceptionType)">
            {{ getExceptionTypeText(currentException.exceptionType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="打卡时间">{{ currentException.exceptionTime || '未打卡' }}</el-descriptions-item>
        <el-descriptions-item label="异常原因">{{ currentException.exceptionReason || '-' }}</el-descriptions-item>
      </el-descriptions>
      
      <el-form 
        ref="processFormRef" 
        :model="processForm" 
        :rules="processRules" 
        label-width="100px"
        style="margin-top: 20px;"
      >
        <el-form-item label="处理结果" prop="result">
          <el-radio-group v-model="processForm.result">
            <el-radio value="normal">标记正常</el-radio>
            <el-radio value="warning">口头警告</el-radio>
            <el-radio value="record">记过处分</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理意见">
          <el-input 
            v-model="processForm.handleNote" 
            type="textarea" 
            :rows="3"
            placeholder="选填"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProcess" :loading="processing">
          确定
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="异常详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学生">{{ currentException.studentName }} ({{ currentException.studentNo }})</el-descriptions-item>
        <el-descriptions-item label="房间">{{ currentException.buildingName }} - {{ currentException.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ currentException.checkDate }}</el-descriptions-item>
        <el-descriptions-item label="异常类型">
          <el-tag :type="getExceptionTypeTag(currentException.exceptionType)">
            {{ getExceptionTypeText(currentException.exceptionType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="打卡时间">{{ currentException.exceptionTime || '未打卡' }}</el-descriptions-item>
        <el-descriptions-item label="异常原因">{{ currentException.exceptionReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentException.status === 1 ? 'success' : 'warning'">
            {{ currentException.status === 1 ? '已处理' : '待处理' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentException.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ currentException.handleTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理意见">{{ currentException.handleNote || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  getExceptions, 
  handleException 
} from '@/api/checkException'

const loading = ref(false)
const processing = ref(false)
const exceptions = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const filters = reactive({
  type: '',
  status: ''
})

const processDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const currentException = ref({})
const processFormRef = ref(null)

const processForm = ref({
  result: 'normal',
  handleNote: ''
})

const processRules = {
  result: [
    { required: true, message: '请选择处理结果', trigger: 'change' }
  ]
}

// 加载异常列表
const loadExceptions = async () => {
  loading.value = true
  try {
    const res = await getExceptions({
      page: page.value,
      size: size.value,
      type: filters.type,
      status: filters.status
    })
    exceptions.value = res.data?.records || res.data || []
    total.value = res.data?.total || exceptions.value.length
  } catch (error) {
    ElMessage.error('加载异常列表失败')
  } finally {
    loading.value = false
  }
}

// 处理异常
const handleProcess = (row) => {
  currentException.value = row
  processForm.value = {
    result: 'normal',
    handleNote: ''
  }
  processDialogVisible.value = true
}

// 提交处理
const submitProcess = async () => {
  const valid = await processFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  processing.value = true
  try {
    const res = await handleException(currentException.value.id, {
      result: processForm.value.result,
      handleNote: processForm.value.handleNote
    })
    if (res.success) {
      ElMessage.success('处理成功')
      processDialogVisible.value = false
      loadExceptions()
    } else {
      ElMessage.error(res.message || '处理失败')
    }
  } catch (error) {
    ElMessage.error('处理失败：' + error.message)
  } finally {
    processing.value = false
  }
}

// 查看详情
const handleView = (row) => {
  currentException.value = row
  viewDialogVisible.value = true
}

// 获取异常类型文本
const getExceptionTypeText = (type) => {
  const texts = { 1: '迟到', 2: '缺卡', 3: '早退' }
  return texts[type] || '未知'
}

// 获取异常类型标签
const getExceptionTypeTag = (type) => {
  const tags = { 1: 'warning', 2: 'danger', 3: 'info' }
  return tags[type] || 'info'
}

onMounted(() => {
  loadExceptions()
})
</script>

<style scoped>
.check-exception-container {
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