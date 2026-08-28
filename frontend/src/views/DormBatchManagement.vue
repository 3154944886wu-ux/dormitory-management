<template>
  <div class="batch-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>选宿批次管理</span>
          <el-button type="primary" @click="handleAdd" v-if="isAdmin">
            <el-icon><Plus /></el-icon>
            新增批次
          </el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-select v-model="filterCollegeId" placeholder="按学院筛选" clearable @change="loadBatches" style="width: 200px;">
          <el-option v-for="c in colleges" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="按状态筛选" clearable @change="loadBatches" style="width: 140px; margin-left: 10px;">
          <el-option label="待启动" value="pending" />
          <el-option label="运行中" value="running" />
          <el-option label="匹配中" value="matching" />
          <el-option label="确认中" value="confirming" />
          <el-option label="已结束" value="finished" />
          <el-option label="已归档" value="archived" />
          <el-option label="已作废" value="cancelled" />
        </el-select>
      </div>

      <el-table :data="batches" v-loading="loading" stripe style="margin-top: 15px;">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="name" label="批次名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="collegeName" label="所属学院" width="150" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.matchStatus)">{{ statusLabel(row.matchStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="问卷时间" width="240">
          <template #default="{ row }">
            <span v-if="row.startTime" style="font-size: 12px;">
              {{ row.startTime?.substring(0, 16) }} ~ {{ row.endTime?.substring(0, 16) }}
            </span>
            <span v-else style="color: #c0c4cc;">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="majorBonus" label="匹配置信度" width="120" align="center" />
        <el-table-column label="操作" width="320" fixed="right" v-if="isAdmin">
          <template #default="{ row }">
            <template v-if="row.matchStatus === 'pending'">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="success" link @click="handleStart(row)">启动</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
            <template v-else-if="row.matchStatus === 'running'">
              <el-button type="warning" link @click="handleCutoff(row)">截止问卷</el-button>
              <el-button type="primary" link @click="handleTriggerMatching(row)">触发匹配</el-button>
            </template>
            <template v-else-if="row.matchStatus === 'confirming'">
              <el-button type="danger" link @click="handleFinish(row)">结束批次</el-button>
            </template>
            <template v-else-if="row.matchStatus === 'finished'">
              <el-button type="success" link @click="handleArchive(row)">归档</el-button>
              <el-button type="info" link @click="handleReset(row)">重置</el-button>
            </template>
            <template v-else-if="row.matchStatus === 'archived'">
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
            <template v-else-if="row.matchStatus === 'cancelled'">
              <el-button type="info" link @click="handleReset(row)">重置</el-button>
            </template>
            <template v-else-if="row.matchStatus === 'matching'">
              <el-button type="primary" link @click="handleTriggerMatching(row)">重新匹配</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑批次' : '新增批次'"
      width="550px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="批次名称" prop="name">
          <el-input v-model="form.name" placeholder="如：计算机学院2026级" />
        </el-form-item>
        <el-form-item label="所属学院" prop="collegeId">
          <el-select v-model="form.collegeId" placeholder="请选择学院" style="width: 100%;">
            <el-option v-for="c in colleges" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="问卷开始时间" prop="startTime">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择开始时间" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="问卷结束时间" prop="endTime">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择结束时间" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="确认截止时间" prop="confirmDeadline">
          <el-date-picker v-model="form.confirmDeadline" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择截止时间" style="width: 100%;" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="匹配置信度" prop="majorBonus">
              <el-input-number v-model="form.majorBonus" :min="0" :max="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="跨专业混住" prop="allowMixMajor">
              <el-switch v-model="form.allowMixMajor" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { dormBatchAPI } from '@/api/dormBatch'
import { collegeAPI } from '@/api/college'

const loading = ref(false)
const batches = ref([])
const colleges = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const filterCollegeId = ref(null)
const filterStatus = ref(null)

const form = reactive({
  id: null,
  name: '',
  collegeId: null,
  startTime: null,
  endTime: null,
  confirmDeadline: null,
  majorBonus: 10,
  allowMixMajor: 0
})

const rules = {
  name: [{ required: true, message: '请输入批次名称', trigger: 'blur' }],
  collegeId: [{ required: true, message: '请选择学院', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  confirmDeadline: [{ required: true, message: '请选择确认截止时间', trigger: 'change' }]
}

const isAdmin = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.role?.toUpperCase() === 'ADMIN'
})

const statusType = (s) => ({ pending: 'info', running: 'success', matching: 'warning', confirming: '', finished: 'warning', archived: 'info', cancelled: 'danger' }[s] || 'info')
const statusLabel = (s) => ({ pending: '待启动', running: '运行中', matching: '匹配中', confirming: '确认中', finished: '已结束', archived: '已归档', cancelled: '已作废' }[s] || s)

const loadColleges = async () => {
  try {
    const res = await collegeAPI.list()
    colleges.value = res.data || []
  } catch (error) {
    // ignore
  }
}

const loadBatches = async () => {
  loading.value = true
  try {
    const params = {}
    if (filterCollegeId.value) params.collegeId = filterCollegeId.value
    if (filterStatus.value) params.matchStatus = filterStatus.value
    const res = await dormBatchAPI.list(params)
    batches.value = res.data || []
  } catch (error) {
    ElMessage.error('加载批次列表失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null, name: '', collegeId: null,
    startTime: null, endTime: null, confirmDeadline: null,
    majorBonus: 10, allowMixMajor: 0
  })
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  resetForm()
  Object.assign(form, {
    id: row.id, name: row.name, collegeId: row.collegeId,
    startTime: row.startTime, endTime: row.endTime,
    confirmDeadline: row.confirmDeadline,
    majorBonus: row.majorBonus, allowMixMajor: row.allowMixMajor
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const data = {
    name: form.name,
    collegeId: form.collegeId,
    startTime: form.startTime,
    endTime: form.endTime,
    confirmDeadline: form.confirmDeadline,
    majorBonus: form.majorBonus,
    allowMixMajor: form.allowMixMajor
  }

  try {
    if (isEdit.value) {
      await dormBatchAPI.update(form.id, data)
      ElMessage.success('更新成功')
    } else {
      await dormBatchAPI.create(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadBatches()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要启动批次"${row.name}"吗？`, '提示', { type: 'warning' })
    await dormBatchAPI.start(row.id)
    ElMessage.success('批次已启动')
    loadBatches()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const handleReset = async (row) => {
  try {
    await ElMessageBox.confirm(
      `重置批次将清空所有分配结果，确定要重置批次"${row.name}"吗？`,
      '警告',
      { type: 'warning', confirmButtonText: '确定重置' }
    )
    await dormBatchAPI.reset(row.id)
    ElMessage.success('批次已重置')
    loadBatches()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const handleCutoff = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要手动截止批次"${row.name}"吗？手动截止将作废该批次，不触发自动匹配。`, '警告', { type: 'warning', confirmButtonText: '确定作废' })
    await dormBatchAPI.cutoff(row.id)
    ElMessage.warning('批次已作废')
    loadBatches()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const handleTriggerMatching = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要对批次"${row.name}"触发匹配吗？匹配完成后批次将进入确认阶段。`,
      '提示',
      { type: 'warning' }
    )
    await dormBatchAPI.triggerMatching(row.id)
    ElMessage.success('匹配完成')
    loadBatches()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '匹配失败')
    }
  }
}

const handleFinish = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要结束批次"${row.name}"吗？未确认的分配将自动确认。`, '提示', { type: 'warning' })
    await dormBatchAPI.finish(row.id)
    ElMessage.success('批次已结束')
    loadBatches()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const handleArchive = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要将批次"${row.name}"归档吗？归档后不可再修改分配结果。`, '提示', { type: 'info' })
    await dormBatchAPI.archive(row.id)
    ElMessage.success('批次已归档')
    loadBatches()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '归档失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除批次"${row.name}"吗？`, '提示', { type: 'warning' })
    await dormBatchAPI.delete(row.id)
    ElMessage.success('删除成功')
    loadBatches()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadColleges()
  loadBatches()
})
</script>

<style scoped>
.batch-container {
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
</style>
