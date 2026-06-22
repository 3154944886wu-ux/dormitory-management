<template>
  <div class="inspection-record-list">
    <div class="page-header">
      <h2>检查记录与整改跟踪</h2>
      <el-button type="primary" @click="loadRecords">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filters">
        <el-form-item label="楼栋">
          <el-select v-model="filters.buildingId" placeholder="全部楼栋" clearable style="width: 160px" @change="loadRecords">
            <el-option
              v-for="b in buildings"
              :key="b.id"
              :label="b.name"
              :value="b.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="检查结果">
          <el-select v-model="filters.result" placeholder="全部" clearable style="width: 110px" @change="loadRecords">
            <el-option label="合格" value="PASS" />
            <el-option label="不合格" value="FAIL" />
          </el-select>
        </el-form-item>
        <el-form-item label="整改状态">
          <el-select v-model="filters.rectificationStatus" placeholder="全部" clearable style="width: 120px" @change="loadRecords">
            <el-option label="无需整改" value="NONE" />
            <el-option label="待整改" value="PENDING" />
            <el-option label="已整改" value="COMPLETED" />
            <el-option label="已核实" value="VERIFIED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 记录列表 -->
    <el-card>
      <el-table :data="records" stripe v-loading="loading" max-height="calc(100vh - 340px)">
        <el-table-column prop="id" label="ID" width="65" />
        <el-table-column prop="roomNumber" label="房间号" width="95" />
        <el-table-column prop="buildingName" label="楼栋" width="110" />
        <el-table-column prop="inspectorName" label="检查人" width="100" />
        <el-table-column label="评分" width="80">
          <template #default="{ row }">
            <span :style="{ color: row.overallScore != null && row.overallScore < 60 ? '#f56c6c' : '#303133' }">
              {{ row.overallScore != null ? row.overallScore : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="结果" width="85">
          <template #default="{ row }">
            <el-tag :type="row.result === 'PASS' ? 'success' : 'danger'" size="small">
              {{ row.result === 'PASS' ? '合格' : '不合格' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="整改状态" width="105">
          <template #default="{ row }">
            <el-tag
              :type="getRectificationTagType(row.rectificationStatus)"
              size="small"
              effect="plain"
            >
              {{ getRectificationName(row.rectificationStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="检查时间" width="165">
          <template #default="{ row }">
            {{ row.inspectionTime ? row.inspectionTime.substring(0, 16) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <!-- 待整改：显示整改按钮 -->
            <el-button
              v-if="row.rectificationStatus === 'PENDING'"
              type="warning"
              size="small"
              @click="showRectifyDialog(row)"
            >
              整改
            </el-button>
            <!-- 已整改：显示验证按钮 -->
            <el-button
              v-if="row.rectificationStatus === 'COMPLETED'"
              type="success"
              size="small"
              @click="handleVerify(row)"
            >
              验证
            </el-button>
            <!-- 详情按钮 -->
            <el-button type="info" size="small" plain @click="showDetailDialog(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadRecords"
          @current-change="loadRecords"
        />
      </div>
    </el-card>

    <!-- 整改对话框 -->
    <el-dialog
      v-model="rectifyDialogVisible"
      title="提交整改"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form :model="rectifyForm" label-width="90px" :rules="rectifyRules" ref="rectifyFormRef">
        <el-form-item label="整改房间">
          <span>{{ rectifyForm.roomNumber }} ({{ rectifyForm.buildingName }})</span>
        </el-form-item>
        <el-form-item label="整改说明" prop="rectifyRemark">
          <el-input
            v-model="rectifyForm.rectifyRemark"
            type="textarea"
            :rows="3"
            placeholder="请描述整改措施和结果..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rectifyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitRectify" :loading="rectifySubmitting">
          确认提交整改
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="检查记录详情" width="650px">
      <template v-if="detailRecord">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="记录ID">{{ detailRecord.id }}</el-descriptions-item>
          <el-descriptions-item label="检查计划ID">{{ detailRecord.planId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ detailRecord.roomNumber }}</el-descriptions-item>
          <el-descriptions-item label="楼栋">{{ detailRecord.buildingName }}</el-descriptions-item>
          <el-descriptions-item label="检查人">{{ detailRecord.inspectorName }}</el-descriptions-item>
          <el-descriptions-item label="检查时间">
            {{ detailRecord.inspectionTime ? detailRecord.inspectionTime.substring(0, 19) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="总评分">{{ detailRecord.overallScore }}</el-descriptions-item>
          <el-descriptions-item label="检查结果">
            <el-tag :type="detailRecord.result === 'PASS' ? 'success' : 'danger'">
              {{ detailRecord.result === 'PASS' ? '合格' : '不合格' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="整改状态">
            <el-tag :type="getRectificationTagType(detailRecord.rectificationStatus)">
              {{ getRectificationName(detailRecord.rectificationStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="整改截止日期">{{ detailRecord.rectificationDeadline || '-' }}</el-descriptions-item>
          <el-descriptions-item label="整改时间">
            {{ detailRecord.rectificationTime ? detailRecord.rectificationTime.substring(0, 19) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="核实人">{{ detailRecord.verifiedBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="核实时间">
            {{ detailRecord.verifiedTime ? detailRecord.verifiedTime.substring(0, 19) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailRecord.remark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="检查项详情" :span="2">
            <pre class="items-json">{{ formatItemsJson(detailRecord.itemsJson) }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import {
  getInspectionRecords,
  searchRecords,
  submitRectification,
  approveRectification
} from '@/api/inspection'
import api from '@/utils/api'

// 列表数据
const records = ref([])
const buildings = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 筛选条件
const filters = reactive({
  buildingId: null,
  result: '',
  rectificationStatus: ''
})

// 整改对话框
const rectifyDialogVisible = ref(false)
const rectifySubmitting = ref(false)
const rectifyFormRef = ref(null)
const rectifyForm = reactive({
  id: null,
  roomNumber: '',
  buildingName: '',
  rectifyRemark: ''
})

const rectifyRules = {
  rectifyRemark: [{ required: true, message: '请填写整改说明', trigger: 'blur' }]
}

// 详情对话框
const detailDialogVisible = ref(false)
const detailRecord = ref(null)

// 状态映射
const getRectificationTagType = (status) => {
  const types = { NONE: 'info', PENDING: 'warning', COMPLETED: 'success', VERIFIED: '' }
  return types[status] || 'info'
}

const getRectificationName = (status) => {
  const names = { NONE: '无需整改', PENDING: '待整改', COMPLETED: '已整改', VERIFIED: '已核实' }
  return names[status] || status || '-'
}

// 格式化检查项JSON
const formatItemsJson = (json) => {
  if (!json) return '-'
  try {
    const items = typeof json === 'string' ? JSON.parse(json) : json
    return JSON.stringify(items, null, 2)
  } catch {
    return json
  }
}

// 加载楼栋列表
const loadBuildings = async () => {
  try {
    const res = await api.get('/buildings')
    buildings.value = res.data || []
  } catch {
    // 静默失败
  }
}

// 加载记录
const loadRecords = async () => {
  loading.value = true
  try {
    const hasFilter = filters.buildingId || filters.result || filters.rectificationStatus

    if (hasFilter) {
      const res = await searchRecords({
        buildingId: filters.buildingId,
        result: filters.result || undefined,
        rectificationStatus: filters.rectificationStatus || undefined
      })
      records.value = res.data || []
      total.value = (res.data || []).length
    } else {
      const res = await getInspectionRecords({ page: currentPage.value, size: pageSize.value })
      records.value = res.data || []
      total.value = res.total || 0
    }
  } catch (error) {
    ElMessage.error('加载检查记录失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选
const resetFilters = () => {
  filters.buildingId = null
  filters.result = ''
  filters.rectificationStatus = ''
  currentPage.value = 1
  loadRecords()
}

// 显示整改对话框
const showRectifyDialog = (row) => {
  rectifyForm.id = row.id
  rectifyForm.roomNumber = row.roomNumber
  rectifyForm.buildingName = row.buildingName
  rectifyForm.rectifyRemark = ''
  rectifyDialogVisible.value = true
}

// 提交整改
const handleSubmitRectify = async () => {
  if (!rectifyFormRef.value) return

  await rectifyFormRef.value.validate(async (valid) => {
    if (!valid) return

    rectifySubmitting.value = true
    try {
      await submitRectification(rectifyForm.id, {
        rectifyRemark: rectifyForm.rectifyRemark
      })
      ElMessage.success('整改提交成功')
      rectifyDialogVisible.value = false
      loadRecords()
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '提交整改失败')
    } finally {
      rectifySubmitting.value = false
    }
  })
}

// 验证整改
const handleVerify = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认验证房间「${row.roomNumber}」的整改已到位？`,
      '整改验证确认',
      {
        confirmButtonText: '确认验证',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await approveRectification(row.id)
    ElMessage.success('整改验证通过')
    loadRecords()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '验证失败')
    }
  }
}

// 显示详情
const showDetailDialog = (row) => {
  detailRecord.value = row
  detailDialogVisible.value = true
}

onMounted(() => {
  loadBuildings()
  loadRecords()
})
</script>

<style scoped>
.inspection-record-list {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.filter-card {
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.items-json {
  font-size: 12px;
  background: #f5f7fa;
  padding: 8px;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
