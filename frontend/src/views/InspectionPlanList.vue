<template>
  <div class="page-container">
    <div class="page-header">
      <h2>检查计划管理</h2>
      <div class="header-actions">
        <el-select v-model="filterType" placeholder="检查类型" clearable style="width:130px" @change="onFilterChange">
          <el-option v-for="(label, key) in INSPECTION_TYPE_MAP" :key="key" :label="label" :value="key" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width:120px" @change="onFilterChange">
          <el-option v-for="(label, key) in INSPECTION_STATUS_MAP" :key="key" :label="label" :value="key" />
        </el-select>
        <el-button type="primary" @click="showAdd">
          <el-icon><Plus /></el-icon>新增计划
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <StatCard :value="draftCount" label="草稿" color="var(--color-info)" :icon="Document" iconBg="rgba(144,147,153,0.1)" valueColor="var(--color-text-primary)" />
      <StatCard :value="scheduledCount" label="已安排" color="var(--color-warning)" :icon="Calendar" iconBg="rgba(230,162,60,0.1)" valueColor="var(--color-text-primary)" />
      <StatCard :value="inProgressCount" label="进行中" color="var(--color-danger)" :icon="Loading" iconBg="rgba(245,108,108,0.1)" valueColor="var(--color-danger)" />
      <StatCard :value="completedCount" label="已完成" color="var(--color-success)" :icon="CircleCheck" iconBg="rgba(82,196,26,0.1)" valueColor="var(--color-success)" />
    </div>

    <!-- 表格 -->
    <el-card>
      <SkeletonLoader v-if="loading && plans.length === 0" type="table" :count="5" />
      <template v-else>
        <el-table :data="plans" stripe v-loading="loading">
          <el-table-column prop="id" label="ID" width="56" align="center" />
          <el-table-column prop="name" label="计划名称" min-width="180" show-overflow-tooltip />
          <el-table-column label="检查类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getTagType(INSPECTION_TYPE_TAG, row.inspectionType)" size="small">{{ getLabel(INSPECTION_TYPE_MAP, row.inspectionType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="涉及楼栋" width="130">
            <template #default="{ row }">
              <span v-if="row.buildingNames">{{ row.buildingNames }}</span>
              <span v-else style="color: var(--color-text-disabled)">-</span>
            </template>
          </el-table-column>
          <el-table-column label="楼层范围" width="90" align="center">
            <template #default="{ row }">
              <span v-if="row.floorRange">{{ row.floorRange }}</span>
              <span v-else style="color: var(--color-text-disabled)">全部</span>
            </template>
          </el-table-column>
          <el-table-column label="计划日期" width="110" align="center">
            <template #default="{ row }">{{ row.scheduledDate || '-' }}</template>
          </el-table-column>
          <el-table-column label="完成进度" width="140">
            <template #default="{ row }">
              <div v-if="row.totalRooms > 0" class="progress-cell">
                <el-progress :percentage="Math.round((row.completedRooms || 0) / row.totalRooms * 100)" :stroke-width="8" :show-text="false" />
                <span class="progress-text">{{ row.completedRooms || 0 }}/{{ row.totalRooms }}</span>
              </div>
              <span v-else style="color: var(--color-text-disabled); font-size: var(--font-size-xs);">0/0</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="getTagType(INSPECTION_STATUS_TAG, row.status)" size="small">{{ getLabel(INSPECTION_STATUS_MAP, row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="creatorName" label="创建人" width="90" align="center" />
          <el-table-column label="操作" width="240" fixed="right" align="center">
            <template #default="{ row }">
              <el-button v-if="row.status === 'DRAFT' || row.status === 'SCHEDULED'" type="success" size="small" @click="handleStart(row)">开始</el-button>
              <el-button v-if="row.status === 'IN_PROGRESS'" type="warning" size="small" @click="handleComplete(row)">完成</el-button>
              <el-button type="primary" size="small" plain @click="showEdit(row)">编辑</el-button>
              <el-button v-if="row.status !== 'COMPLETED'" type="danger" size="small" plain @click="handleCancel(row)">{{ row.status === 'CANCELLED' ? '删除' : '取消' }}</el-button>
            </template>
          </el-table-column>
        </el-table>
        <EmptyState v-if="!loading && plans.length === 0" description="暂无检查计划" action-text="创建第一个计划" @action="showAdd" />
        <div class="pagination-wrapper">
          <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" background @size-change="loadPlans" @current-change="loadPlans" />
        </div>
      </template>
    </el-card>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑计划' : '新增计划'" width="560px" @closed="resetForm">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="计划名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="检查类型" prop="inspectionType">
          <el-select v-model="form.inspectionType" style="width:100%">
            <el-option v-for="(label, key) in INSPECTION_TYPE_MAP" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="检查楼栋" prop="buildingIds">
          <el-select v-model="form.buildingIds" multiple style="width:100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层范围"><el-input v-model="form.floorRange" placeholder="如: 1-6" /></el-form-item>
        <el-form-item label="计划日期" prop="scheduledDate">
          <el-date-picker v-model="form.scheduledDate" type="date" style="width:100%" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="计划描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Document, Calendar, Loading, CircleCheck } from '@element-plus/icons-vue'
import {
  getInspectionPlans, getPlansByType, getPlansByStatus,
  createInspectionPlan, updateInspectionPlan,
  startInspectionPlan, completeInspectionPlan, cancelInspectionPlan, deleteInspectionPlan
} from '@/api/inspection'
import api from '@/utils/api'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import { useConfirm } from '@/composables/useConfirm'
import { INSPECTION_TYPE_MAP, INSPECTION_TYPE_TAG, INSPECTION_STATUS_MAP, INSPECTION_STATUS_TAG, getLabel, getTagType } from '@/constants/status'
import StatCard from '@/components/business/StatCard.vue'
import SkeletonLoader from '@/components/common/SkeletonLoader.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const { loading, data: plans, total, currentPage, pageSize, loadData: loadTable } = useTable({ fetchFn: getInspectionPlans })
const { dialogVisible, isEdit, form, formRef, submitting } = useForm({
  id: null, name: '', inspectionType: '', buildingIds: [], floorRange: '', scheduledDate: '', description: ''
})
const { confirm, confirmDanger } = useConfirm()

const buildings = ref([])
const filterType = ref('')
const filterStatus = ref('')

const draftCount = computed(() => plans.value.filter(p => p.status === 'DRAFT').length)
const scheduledCount = computed(() => plans.value.filter(p => p.status === 'SCHEDULED').length)
const inProgressCount = computed(() => plans.value.filter(p => p.status === 'IN_PROGRESS').length)
const completedCount = computed(() => plans.value.filter(p => p.status === 'COMPLETED').length)

const rules = {
  name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  inspectionType: [{ required: true, message: '请选择检查类型', trigger: 'change' }],
  buildingIds: [{ required: true, message: '请选择至少一个楼栋', trigger: 'change' }],
  scheduledDate: [{ required: true, message: '请选择计划日期', trigger: 'change' }]
}

const loadPlans = () => {
  if (filterType.value) {
    getPlansByType(filterType.value).then(res => { plans.value = res.data || []; total.value = (res.data || []).length })
  } else if (filterStatus.value) {
    getPlansByStatus(filterStatus.value).then(res => { plans.value = res.data || []; total.value = (res.data || []).length })
  } else {
    loadTable()
  }
}

const onFilterChange = () => { currentPage.value = 1; loadPlans() }

const resetForm = () => { formRef.value?.resetFields() }

const showAdd = () => {
  isEdit.value = false; Object.assign(form, { id: null, name: '', inspectionType: '', buildingIds: [], floorRange: '', scheduledDate: '', description: '' })
  dialogVisible.value = true
}

const showEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, name: row.name, inspectionType: row.inspectionType, buildingIds: row.buildingIds ? row.buildingIds.split(',').map(Number) : [], floorRange: row.floorRange || '', scheduledDate: row.scheduledDate || '', description: row.description || '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const data = { name: form.name, inspectionType: form.inspectionType, buildingIds: form.buildingIds.join(','), floorRange: form.floorRange, scheduledDate: form.scheduledDate, description: form.description }
      if (isEdit.value) { await updateInspectionPlan(form.id, data); ElMessage.success('更新成功') }
      else { await createInspectionPlan(data); ElMessage.success('创建成功') }
      dialogVisible.value = false; loadPlans()
    } catch (e) { ElMessage.error(e.response?.data?.message || '操作失败') }
    finally { submitting.value = false }
  })
}

const handleStart = async (row) => {
  if (!(await confirm('开始执行', `确认开始执行「${row.name}」？`))) return
  try { await startInspectionPlan(row.id); ElMessage.success('已开始'); loadPlans() } catch (e) { ElMessage.error(e.response?.data?.message || '操作失败') }
}

const handleComplete = async (row) => {
  if (!(await confirm('完成检查', `确认完成「${row.name}」？`))) return
  try { await completeInspectionPlan(row.id); ElMessage.success('已完成'); loadPlans() } catch (e) { ElMessage.error(e.response?.data?.message || '操作失败') }
}

const handleCancel = async (row) => {
  const isCancel = row.status !== 'CANCELLED'
  if (!(await confirm(isCancel ? '取消计划' : '删除计划', `确认${isCancel ? '取消' : '删除'}「${row.name}」？`))) return
  try {
    if (isCancel) { await cancelInspectionPlan(row.id); ElMessage.success('已取消') }
    else { await deleteInspectionPlan(row.id); ElMessage.success('已删除') }
    loadPlans()
  } catch (e) { ElMessage.error(e.response?.data?.message || '操作失败') }
}

const loadBuildings = async () => { try { const r = await api.get('/buildings'); buildings.value = r.data || [] } catch {} }

onMounted(() => { loadPlans(); loadBuildings() })
</script>

<style scoped>
.progress-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.progress-cell .el-progress {
  flex: 1;
}
.progress-text {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
  white-space: nowrap;
  min-width: 32px;
  text-align: right;
}
</style>
