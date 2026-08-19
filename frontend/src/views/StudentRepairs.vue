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
        <el-table-column prop="type" label="报修类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="问题描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="完成时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.completeTime) }}
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

    <el-dialog v-model="showDialog" title="新建报修" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="报修类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option v-for="item in repairTypeOptions" :key="item" :label="item" :value="item" />
          </el-select>
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

const repairTypeOptions = ['水电维修', '门窗维修', '家具维修', '网络问题', '其他']
const legacyTypeMap = { 1: '水电维修', 2: '门窗维修', 3: '家具维修', 4: '网络问题', 5: '其他' }

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
  description: ''
})

const rules = {
  type: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  description: [{ required: true, message: '请描述问题', trigger: 'blur' }]
}

const getTypeText = (type) => legacyTypeMap[type] || type || '未知'

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const names = { 0: '待处理', 1: '处理中', 2: '已完成', 3: '已关闭' }
  return names[status] || '未知'
}

const formatDateTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ').substring(0, 16)
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
    ElMessage.error(error.message || '加载失败')
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
        await studentAPI.createRepair({ type: form.type, description: form.description })
        ElMessage.success('提交成功')
        showDialog.value = false
        form.type = null
        form.description = ''
        loadRepairs()
      } catch (error) {
        ElMessage.error(error.message || '提交失败')
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
