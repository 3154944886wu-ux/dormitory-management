<template>
  <div class="student-room">
    <!-- ====== 选宿流程区 ====== -->
    <div v-if="loading" class="state-box">
      <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      <p>加载中...</p>
    </div>

    <div v-else-if="!student.dormBatchId" class="state-box">
      <el-empty description="暂未分配选宿批次" />
    </div>

    <div v-else-if="batchRunning && !batchStarted" class="state-box">
      <el-empty description="问卷尚未开放">
        <template #image>
          <el-icon :size="60"><Clock /></el-icon>
        </template>
        <p class="state-hint">开放时间：{{ formatTime(batch.startTime) }} — {{ formatTime(batch.endTime) }}</p>
      </el-empty>
    </div>

    <div v-else-if="batchRunning && batchStarted && !batchEnded" class="dorm-section">
      <el-card class="batch-info-card">
        <div class="batch-header">
          <div>
            <h2>{{ batch.name }}</h2>
            <p class="batch-time">
              问卷开放：{{ formatTime(batch.startTime) }} — {{ formatTime(batch.endTime) }}
            </p>
          </div>
          <el-tag v-if="hasSubmitted" type="success">已提交</el-tag>
          <el-tag v-else type="warning">未提交</el-tag>
        </div>
        <el-alert
          v-if="hasSubmitted"
          title="你已提交过问卷，截止时间前可修改并重新提交"
          type="info"
          show-icon
          :closable="false"
        />
      </el-card>

      <el-card class="form-card">
        <template #header><span>选宿问卷</span></template>
        <el-form ref="formRef" :model="formData" label-position="top">
          <div v-for="q in questions" :key="q.id" class="question-block">
            <div class="question-header">
              <span class="question-text">{{ q.questionText }}</span>
              <el-tag v-if="q.isRequired" type="danger" size="small">必答</el-tag>
              <el-tag v-else type="info" size="small">选答</el-tag>
            </div>
            <el-radio-group v-model="formData[q.id]" class="option-group">
              <el-radio v-for="opt in q.options" :key="opt.id" :value="opt.id" class="option-item">
                {{ opt.optionText }}
              </el-radio>
            </el-radio-group>
          </div>
        </el-form>
        <div class="form-footer">
          <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">提交问卷</el-button>
          <el-button size="large" @click="handleReset">重置</el-button>
        </div>
      </el-card>
    </div>

    <div v-else-if="batchConfirming || batchFinished" class="dorm-section">
      <el-card class="batch-info-card">
        <div class="batch-header">
          <div>
            <h2>{{ batch.name }}</h2>
            <p class="batch-time">确认截止：{{ formatTime(batch.confirmDeadline) }}</p>
          </div>
          <el-tag :type="batchConfirming ? 'warning' : 'info'">
            {{ batchConfirming ? '确认中' : '已结束' }}
          </el-tag>
        </div>
      </el-card>

      <el-card v-if="allocation" class="result-card">
        <template #header><span>分配结果</span></template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="楼栋">{{ buildingName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ allocation.roomNumber || '-' }}</el-descriptions-item>
          <el-descriptions-item label="床位号">{{ allocation.bedNumber || '-' }}</el-descriptions-item>
          <el-descriptions-item label="匹配度">{{ allocation.matchScore }}%</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="allocationStatusTag(allocation.status)">
              {{ allocationStatusLabel(allocation.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="dormRoommates.length > 0" class="roommate-section">
          <h4 class="roommate-title">室友信息</h4>
          <div class="roommate-grid">
            <div v-for="(rm, idx) in dormRoommates" :key="idx" class="roommate-card">
              <div class="roommate-card-header">
                <span class="roommate-index">室友 {{ idx + 1 }}</span>
                <span class="roommate-major">{{ rm.majorName }}</span>
              </div>
              <div class="roommate-tags">
                <el-tag v-for="(tag, tIdx) in rm.tags" :key="tIdx" size="small" type="info" class="roommate-tag">{{ tag }}</el-tag>
                <el-tag v-if="!rm.tags || rm.tags.length === 0" size="small" type="info">未填写问卷</el-tag>
              </div>
            </div>
          </div>
        </div>

        <div v-if="batchConfirming && allocation.status === 'recommended'" class="action-bar">
          <el-alert title="你的宿舍推荐已生成！请在截止时间前确认或调整" type="success" show-icon :closable="true" style="margin-bottom: 16px;" />
          <el-button type="primary" @click="handleConfirm" :loading="confirmLoading">确认入住</el-button>
          <el-button type="warning" @click="handleReallocate" :loading="reallocateLoading" :disabled="reallocationUsed >= 1">换一个宿舍</el-button>
          <span v-if="reallocationUsed >= 1" class="text-hint">(已使用重新匹配机会)</span>
        </div>

        <div v-if="allocation && (allocation.status === 'confirmed' || allocation.status === 'auto_confirmed')" class="action-bar">
          <el-button
            v-if="relocationInfo && relocationInfo.relocationUsedThisYear < relocationInfo.maxRelocationPerYear && !relocationInfo.hasPendingApplication"
            type="primary"
            @click="showRelocationDialog = true"
          >申请调换</el-button>
          <el-tag v-else-if="relocationInfo && relocationInfo.hasPendingApplication" type="warning">调换申请处理中</el-tag>
          <el-tag v-else type="info">本年度调换次数已用完</el-tag>
          <span class="text-hint" v-if="relocationInfo">(本年度已使用 {{ relocationInfo.relocationUsedThisYear }}/{{ relocationInfo.maxRelocationPerYear }} 次)</span>
        </div>
      </el-card>

      <el-card v-else class="result-card">
        <el-empty description="暂无分配结果" />
      </el-card>
    </div>

    <!-- ====== 房间信息区 ====== -->
    <el-card v-if="roomInfo" :style="{ marginTop: hasDormSelection ? '20px' : '0' }">
      <template #header><span>我的宿舍</span></template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="楼栋">{{ roomInfo.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ roomInfo.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ roomInfo.floor }}层</el-descriptions-item>
        <el-descriptions-item label="床位号">{{ roomInfo.bedNumber }}</el-descriptions-item>
        <el-descriptions-item label="房间类型">{{ roomInfo.roomType }}</el-descriptions-item>
        <el-descriptions-item label="入住人数">{{ roomInfo.occupancy }}/{{ roomInfo.capacity }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 安全检查记录 -->
    <el-card v-if="roomInfo" style="margin-top: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>安全检查记录</span>
          <el-tag v-if="inspectionRecords.length > 0" type="info" size="small">共 {{ inspectionRecords.length }} 条</el-tag>
        </div>
      </template>
      <el-table :data="inspectionRecords" stripe v-loading="loadingInspections" size="small">
        <el-table-column label="检查时间" width="170">
          <template #default="{ row }">{{ row.inspectionTime ? row.inspectionTime.substring(0, 16) : '-' }}</template>
        </el-table-column>
        <el-table-column label="检查类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.inspectionType === 'SAFETY' ? 'danger' : 'success'" size="small">
              {{ row.inspectionType === 'SAFETY' ? '安全' : '卫生' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="80">
          <template #default="{ row }">{{ row.overallScore != null ? row.overallScore : '-' }}</template>
        </el-table-column>
        <el-table-column label="结果" width="80">
          <template #default="{ row }">
            <el-tag :type="row.result === 'PASS' ? 'success' : 'danger'" size="small">
              {{ row.result === 'PASS' ? '合格' : '不合格' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="整改状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.rectificationStatus === 'PENDING'" type="warning" size="small">待整改</el-tag>
            <el-tag v-else-if="row.rectificationStatus === 'COMPLETED'" type="success" size="small">已整改</el-tag>
            <el-tag v-else-if="row.rectificationStatus === 'VERIFIED'" size="small">已核实</el-tag>
            <span v-else style="color: #c0c4cc;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="inspectorName" label="检查人" width="100" />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="110">
          <template #default="{ row }">
            <el-button
              v-if="row.rectificationStatus === 'PENDING'"
              type="primary"
              link
              size="small"
              @click="openRectify(row)"
            >提交整改</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loadingInspections && inspectionRecords.length === 0" description="暂无检查记录" :image-size="80" />
    </el-card>

    <el-card style="margin-top: 20px" v-if="roommates.length > 0 && !(batchConfirming || batchFinished)">
      <template #header><span>室友信息</span></template>
      <el-table :data="roommates" stripe>
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="bedNumber" label="床位号" width="100" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="className" label="班级" />
      </el-table>
    </el-card>

    <el-empty v-if="!roomInfo && !student.dormBatchId" description="暂未分配宿舍" />

    <!-- 调换申请对话框 -->
    <el-dialog v-model="showRelocationDialog" title="申请调换宿舍" width="500px">
      <el-form :model="relocationForm" label-width="100px">
        <el-form-item label="申请理由" required>
          <el-input v-model="relocationForm.reason" type="textarea" :rows="3" placeholder="请详细说明调换原因..." maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRelocationDialog = false">取消</el-button>
        <el-button type="primary" @click="handleRelocationApply" :loading="relocationApplying">提交申请</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showRectifyDialog" title="提交整改" width="480px">
      <el-form :model="rectifyForm" label-width="90px">
        <el-form-item label="整改说明" required>
          <el-input v-model="rectifyForm.rectifyRemark" type="textarea" :rows="4" placeholder="请填写整改说明" maxlength="300" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRectifyDialog = false">取消</el-button>
        <el-button type="primary" :loading="rectifySubmitting" @click="handleRectify">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading, Clock } from '@element-plus/icons-vue'
import { studentAPI } from '@/api/student'
import { dormSelectionAPI } from '@/api/dormSelection'
import { relocationAPI } from '@/api/relocation'
import { getRecordsByRoom, submitRectification } from '@/api/inspection'

// --- 选宿状态 ---
const loading = ref(true)
const submitting = ref(false)
const confirmLoading = ref(false)
const reallocateLoading = ref(false)
const formRef = ref(null)
const student = reactive({ dormBatchId: null })
const batch = reactive({ id: null, name: '', startTime: '', endTime: '', confirmDeadline: '', matchStatus: '' })
const allocation = ref(null)
const questions = ref([])
const hasSubmitted = ref(false)
const reallocationUsed = ref(0)
const formData = reactive({})
const dormRoommates = ref([]) // roommates from dorm selection
const buildingName = ref('')
const relocationInfo = ref(null)

const inspectionRecords = ref([])
const loadingInspections = ref(false)

const showRelocationDialog = ref(false)
const relocationApplying = ref(false)
const relocationForm = reactive({ reason: '' })
const showRectifyDialog = ref(false)
const rectifySubmitting = ref(false)
const rectifyForm = reactive({ id: null, rectifyRemark: '' })

const batchRunning = computed(() => batch.matchStatus === 'running')
const batchConfirming = computed(() => batch.matchStatus === 'confirming')
const batchFinished = computed(() => batch.matchStatus === 'finished')
const hasDormSelection = computed(() => batch.matchStatus === 'running' || batchConfirming.value || batchFinished.value)

const batchStarted = computed(() => {
  if (!batch.startTime) return false
  return new Date() >= new Date(batch.startTime)
})
const batchEnded = computed(() => {
  if (!batch.endTime) return false
  return new Date() > new Date(batch.endTime)
})

// --- 房间信息 ---
const roomInfo = ref(null)
const roommates = ref([])

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

const allocationStatusTag = (s) => ({ recommended: 'warning', confirmed: 'success', auto_confirmed: 'info', adjusted: 'primary' }[s] || 'info')
const allocationStatusLabel = (s) => ({ recommended: '待确认', confirmed: '已确认', auto_confirmed: '自动确认', adjusted: '已调整' }[s] || s)

// --- 选宿方法 ---
const loadSurvey = async () => {
  loading.value = true
  try {
    const res = await dormSelectionAPI.mySurvey()
    const data = res.data
    Object.assign(student, data.student || {})
    if (data.batch) {
      Object.assign(batch, data.batch)
      reallocationUsed.value = data.batch.reallocationUsed || 0
    }
    hasSubmitted.value = data.student?.hasSubmitted || false
    allocation.value = data.allocation || null
    buildingName.value = data.allocation?.buildingName || ''
    dormRoommates.value = data.roommates || []
    relocationInfo.value = data.relocationInfo || null

    const qs = data.questions || []
    questions.value = qs
    qs.forEach(q => {
      const selectedOpt = q.options?.find(o => o.selected)
      if (selectedOpt) formData[q.id] = selectedOpt.id
    })
  } catch (error) {
    console.error('加载选宿数据失败', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  const requiredQIds = questions.value.filter(q => q.isRequired).map(q => q.id)
  for (const qId of requiredQIds) {
    if (!formData[qId]) {
      const q = questions.value.find(q => q.id === qId)
      ElMessage.warning(`请回答必答题：${q?.questionText || qId}`)
      return
    }
  }
  const answers = Object.entries(formData)
    .filter(([, optionId]) => optionId != null)
    .map(([qId, optionId]) => ({ qId: Number(qId), optionId: Number(optionId) }))
  if (answers.length === 0) { ElMessage.warning('请至少回答一道题目'); return }
  submitting.value = true
  try {
    await dormSelectionAPI.submitAnswers(answers)
    ElMessage.success('提交成功')
    hasSubmitted.value = true
    loadSurvey()
  } catch (error) { ElMessage.error(error.message || '提交失败') }
  finally { submitting.value = false }
}

const handleReset = () => {
  questions.value.forEach(q => { formData[q.id] = null })
  loadSurvey()
}

const handleConfirm = async () => {
  confirmLoading.value = true
  try {
    await dormSelectionAPI.confirmAllocation()
    ElMessage.success('确认入住成功')
    loadSurvey()
    loadRoomData()
  } catch (error) { ElMessage.error(error.message || '确认失败') }
  finally { confirmLoading.value = false }
}

const handleReallocate = async () => {
  reallocateLoading.value = true
  try {
    await dormSelectionAPI.requestReallocation()
    ElMessage.success('已申请重新匹配')
    loadSurvey()
  } catch (error) { ElMessage.error(error.message || '重新匹配失败') }
  finally { reallocateLoading.value = false }
}

const handleRelocationApply = async () => {
  if (!relocationForm.reason.trim()) { ElMessage.warning('请填写申请理由'); return }
  relocationApplying.value = true
  try {
    await relocationAPI.apply({ reason: relocationForm.reason })
    ElMessage.success('调换申请已提交')
    showRelocationDialog.value = false
    relocationForm.reason = ''
    loadSurvey()
  } catch (error) { ElMessage.error(error.response?.data?.message || '申请失败') }
  finally { relocationApplying.value = false }
}

// --- 房间数据 ---
const loadRoomData = async () => {
  try {
    const roomRes = await studentAPI.getMyRoom()
    roomInfo.value = roomRes.data
    roommates.value = roomRes.data?.roommates || []

    // 加载该房间的检查记录
    if (roomInfo.value?.roomId) {
      loadInspections(roomInfo.value.roomId)
    }
  } catch (error) { console.error('加载房间数据失败', error) }
}

const loadInspections = async (roomId) => {
  loadingInspections.value = true
  try {
    const res = await getRecordsByRoom(roomId)
    inspectionRecords.value = res.data || []
  } catch { /* ignore */ }
  finally { loadingInspections.value = false }
}

const openRectify = (row) => {
  rectifyForm.id = row.id
  rectifyForm.rectifyRemark = ''
  showRectifyDialog.value = true
}

const handleRectify = async () => {
  if (!rectifyForm.rectifyRemark.trim()) {
    ElMessage.warning('请填写整改说明')
    return
  }
  rectifySubmitting.value = true
  try {
    await submitRectification(rectifyForm.id, { rectifyRemark: rectifyForm.rectifyRemark })
    ElMessage.success('整改已提交')
    showRectifyDialog.value = false
    if (roomInfo.value?.roomId) {
      loadInspections(roomInfo.value.roomId)
    }
  } catch (error) {
    ElMessage.error(error.message || '提交整改失败')
  } finally {
    rectifySubmitting.value = false
  }
}

onMounted(() => {
  loadSurvey()
  loadRoomData()
})
</script>

<style scoped>
.student-room { padding: 0; }

/* --- 选宿样式 --- */
.state-box {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 80px 20px; background: #fff; border-radius: 8px; margin-bottom: 20px;
}
.state-hint { margin-top: 12px; color: #909399; font-size: 14px; }
.dorm-section { max-width: 800px; margin: 0 auto 20px; }
.batch-info-card { margin-bottom: 20px; }
.batch-header { display: flex; justify-content: space-between; align-items: flex-start; }
.batch-header h2 { margin: 0 0 8px 0; font-size: 20px; }
.batch-time { margin: 0; color: #909399; font-size: 14px; }
.form-card { margin-bottom: 20px; }
.result-card { margin-bottom: 20px; }

.roommate-section { margin-top: 24px; }
.roommate-title { margin: 0 0 12px 0; font-size: 16px; font-weight: 600; color: #303133; }
.roommate-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 12px; }
.roommate-card { border: 1px solid #ebeef5; border-radius: 8px; padding: 16px; background: #fafafa; }
.roommate-card-header { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; padding-bottom: 10px; border-bottom: 1px solid #ebeef5; }
.roommate-index { font-size: 13px; font-weight: 600; color: #409eff; }
.roommate-major { font-size: 13px; color: #606266; }
.roommate-tags { display: flex; flex-wrap: wrap; gap: 6px; }
.roommate-tag { font-size: 12px; }

.action-bar { margin-top: 20px; display: flex; align-items: center; gap: 12px; }
.text-hint { color: #909399; font-size: 13px; }

.question-block { margin-bottom: 24px; padding-bottom: 20px; border-bottom: 1px solid #ebeef5; }
.question-block:last-child { border-bottom: none; }
.question-header { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.question-text { font-size: 15px; font-weight: 500; }
.option-group { display: flex; flex-direction: column; gap: 10px; }
.option-item { padding: 10px 16px; border: 1px solid #dcdfe6; border-radius: 6px; margin: 0; width: 100%; }
.option-item:hover { border-color: #409eff; }
.form-footer { display: flex; gap: 12px; padding-top: 20px; }
</style>
