<template>
  <div class="student-repairs">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的报修</span>
          <el-button type="primary" @click="showDialog = true">
            <el-icon><Plus /></el-icon>
            新建报修
          </el-button>
        </div>
      </template>
      
      <el-table :data="repairs" v-loading="loading" stripe>
        <el-table-column prop="type" label="报修类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="问题描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="location" label="报修位置" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column prop="repairTime" label="维修时间" width="160">
          <template #default="{ row }">
            {{ row.repairTime || '-' }}
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadRepairs"
          @current-change="loadRepairs"
        />
      </div>
    </el-card>
    
    <!-- 新建报修对话框 -->
    <el-dialog v-model="showDialog" title="新建报修" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="报修类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="水电" :value="1" />
            <el-option label="门窗" :value="2" />
            <el-option label="家具" :value="3" />
            <el-option label="网络" :value="4" />
            <el-option label="其他" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修位置" prop="location">
          <el-input v-model="form.location" placeholder="如：宿舍内/公共区域" />
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述问题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { studentAPI } from '@/api/student'

const loading = ref(false)
const submitting = ref(false)
const repairs = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDialog = ref(false)
const formRef = ref(null)

const form = reactive({
  type: null,
  location: '',
  description: ''
})

const rules = {
  type: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  location: [{ required: true, message: '请输入报修位置', trigger: 'blur' }],
  description: [{ required: true, message: '请描述问题', trigger: 'blur' }]
}

const getTypeText = (type) => {
  const types = { 1: '水电', 2: '门窗', 3: '家具', 4: '网络', 5: '其他' }
  return types[type] || '未知'
}

const getStatusType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 0: return '待处理'
    case 1: return '已维修'
    case 2: return '已拒绝'
    default: return '未知'
  }
}

const loadRepairs = async () => {
  loading.value = true
  try {
    const res = await studentAPI.getRepairs({
      page: currentPage.value,
      size: pageSize.value
    })
    repairs.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await studentAPI.createRepair(form)
        ElMessage.success('提交成功')
        showDialog.value = false
        form.type = null
        form.location = ''
        form.description = ''
        loadRepairs()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '提交失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  loadRepairs()
})
</script>

<style scoped>
.student-repairs {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>