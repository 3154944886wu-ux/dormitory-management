<template>
  <div class="execute-page">
    <div class="execute-container">
      <!-- 页头 -->
      <div class="page-header">
        <h2>执行检查</h2>
      </div>

      <!-- 初始空状态：未选计划 -->
      <div v-if="!selectedPlanId" class="init-state">
        <el-empty description="请先选择检查计划和房间以开始检查" :image-size="160">
          <template #image>
            <el-icon :size="80" color="var(--color-text-disabled)"><Checked /></el-icon>
          </template>
        </el-empty>
      </div>

      <!-- 选择器卡片 -->
      <el-card class="select-card" shadow="never">
        <el-form :inline="true" label-width="70px" class="select-form">
          <el-form-item label="检查计划">
            <el-select v-model="selectedPlanId" placeholder="请选择检查计划" style="width: 240px" @change="onPlanSelect">
              <el-option v-for="plan in availablePlans" :key="plan.id" :label="`${plan.name} (${plan.scheduledDate || '-'})`" :value="plan.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="检查房间">
            <el-select v-model="selectedRoomIds" placeholder="请先选择计划" style="width: 260px" filterable multiple collapse-tags collapse-tags-tooltip :disabled="!selectedPlanId">
              <el-option v-for="room in availableRooms" :key="room.id" :label="`${room.buildingName || ''} ${room.roomNumber}`" :value="room.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :disabled="!selectedPlanId || selectedRoomIds.length === 0" :loading="loadingItems" @click="startInspection">
              <el-icon><Search /></el-icon>
              {{ selectedRoomIds.length > 1 ? `批量检查（${selectedRoomIds.length}个房间）` : '开始检查' }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 计划详情卡（选了计划但还没开始检查） -->
      <el-card v-if="selectedPlanId && currentPlan && inspectionItems.length === 0 && !loadingItems" class="plan-info-card" shadow="never">
        <template #header><span class="card-title">计划详情</span></template>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="计划名称">{{ currentPlan.name }}</el-descriptions-item>
          <el-descriptions-item label="检查日期">{{ currentPlan.scheduledDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ currentPlan.creatorName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="涉及楼栋">{{ currentPlan.buildingNames || currentPlan.buildingIds || '全部' }}</el-descriptions-item>
          <el-descriptions-item label="检查类型">
            <el-tag :type="currentPlan.inspectionType === 'SAFETY' ? 'danger' : 'success'" size="small">{{ getTypeName(currentPlan.inspectionType) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="楼层范围">{{ currentPlan.floorRange || '全部' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 检查项列表（点击"开始检查"后显示） -->
      <el-card v-if="inspectionItems.length > 0" class="items-card" shadow="never" ref="itemsCardRef">
        <template #header>
          <span>检查项目（共 {{ inspectionItems.length }} 项）</span>
        </template>
        <div class="item-list">
          <div v-for="(item, index) in evaluationList" :key="item.itemId" class="inspect-item" :class="{ 'inspect-item--fail': item.result === 'FAIL' }">
            <!-- 卡片头部 -->
            <div class="item-hd">
              <span class="item-num">{{ index + 1 }}</span>
              <div class="item-title">
                <div class="item-name">{{ item.itemName }}</div>
                <div class="item-std">{{ item.standard || '无标准描述' }}</div>
              </div>
              <div class="item-score-badge" :class="item.result === 'PASS' ? 'score--pass' : 'score--fail'">
                {{ item.result === 'PASS' ? `+${item.maxScore}` : `0/${item.maxScore}` }}
              </div>
            </div>

            <!-- 判定按钮组 -->
            <div class="item-verdict">
              <button
                class="verdict-btn verdict-btn--pass"
                :class="{ active: item.result === 'PASS' }"
                @click="setItemResult(item, 'PASS')"
              >
                <el-icon><CircleCheck /></el-icon>
                <span>合格</span>
              </button>
              <button
                class="verdict-btn verdict-btn--fail"
                :class="{ active: item.result === 'FAIL' }"
                @click="setItemResult(item, 'FAIL')"
              >
                <el-icon><CircleClose /></el-icon>
                <span>不合格</span>
              </button>
            </div>

            <!-- 不合格展开区 -->
            <div v-if="item.result === 'FAIL'" class="item-fail-area">
              <div class="fail-field">
                <label class="fail-label">扣分原因 <span class="required">*</span></label>
                <el-input v-model="item.remark" type="textarea" :rows="2" :placeholder="`请描述 ${item.itemName} 不合格的具体原因...`" maxlength="200" show-word-limit />
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 总体备注 -->
      <el-card v-if="inspectionItems.length > 0" class="remark-card" shadow="never">
        <el-form label-width="80px">
          <el-form-item label="总体备注">
            <el-input v-model="overallRemark" type="textarea" :rows="2" placeholder="检查总体评价或备注（可选）" />
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 吸底提交栏 -->
      <div v-if="inspectionItems.length > 0" class="sticky-footer">
        <div class="footer-stats">
          <!-- 检查进度条 -->
          <div class="footer-progress">
            <span class="footer-label">检查进度</span>
            <el-progress :percentage="checkProgress" :stroke-width="8" :show-text="false" />
            <span class="footer-hint">{{ checkedCount }} / {{ evaluationList.length }}</span>
          </div>
          <!-- 总分大字 -->
          <div class="footer-score">
            <span class="footer-label">当前总分</span>
            <span class="footer-score-num">{{ totalScore }}</span>
            <span class="footer-score-div">/</span>
            <span class="footer-score-max">{{ maxTotalScore }}</span>
          </div>
          <!-- 合格/不合格 -->
          <div class="footer-passfail">
            <span class="passfail passfail--pass"><el-icon><CircleCheck /></el-icon>{{ passCount }}</span>
            <span class="passfail passfail--fail"><el-icon><CircleClose /></el-icon>{{ failCount }}</span>
          </div>
        </div>
        <el-button type="primary" size="large" :loading="submitting" class="submit-btn" @click="handleSubmit">
          <span v-if="!submitting">提交检查结果（{{ totalScore }}分）</span>
          <span v-else>提交中...</span>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Checked, Search, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import {
  getInspectionPlans, getPendingPlans, getActiveItems, getItemsByCategory,
  createInspectionRecord, startInspectionPlan
} from '@/api/inspection'
import api from '@/utils/api'

const availablePlans = ref([])
const availableRooms = ref([])
const inspectionItems = ref([])
const loadingItems = ref(false)
const selectedPlanId = ref(null)
const selectedRoomIds = ref([])
const currentPlan = computed(() => availablePlans.value.find(p => p.id === selectedPlanId.value))
const overallRemark = ref('')
const submitting = ref(false)
const evaluationList = ref([])
const itemsCardRef = ref(null)

const passCount = computed(() => evaluationList.value.filter(e => e.result === 'PASS').length)
const failCount = computed(() => evaluationList.value.filter(e => e.result === 'FAIL').length)
const checkedCount = computed(() => passCount.value + failCount.value)
const checkProgress = computed(() => evaluationList.value.length > 0 ? Math.round((checkedCount.value / evaluationList.value.length) * 100) : 0)
const totalScore = computed(() => evaluationList.value.reduce((s, e) => s + (Number(e.score) || 0), 0).toFixed(1))
const maxTotalScore = computed(() => evaluationList.value.reduce((s, e) => s + (Number(e.maxScore) || 0), 0).toFixed(1))

const getTypeName = (t) => ({ SAFETY: '安全检查', HYGIENE: '卫生检查', COMPREHENSIVE: '综合检查' }[t] || t)

const loadAvailablePlans = async () => {
  try {
    const [a, b] = await Promise.allSettled([getInspectionPlans({ page: 1, size: 50 }), getPendingPlans()])
    const plans = []
    if (a.status === 'fulfilled') plans.push(...(a.value.data || []).filter(p => p.status === 'SCHEDULED' || p.status === 'IN_PROGRESS'))
    if (b.status === 'fulfilled') (b.value.data || []).forEach(p => { if (!plans.find(x => x.id === p.id)) plans.push(p) })
    availablePlans.value = plans
  } catch { ElMessage.error('加载计划列表失败') }
}

// 选择计划 → 只加载房间，不自动加载检查项
const onPlanSelect = async (planId) => {
  selectedRoomIds.value = []
  evaluationList.value = []
  inspectionItems.value = []
  if (!planId) return
  const plan = currentPlan.value
  if (!plan) return
  await loadRoomsForPlan(plan)
}

// 设定检查项结果：合格=满分，不合格=0分
const setItemResult = (item, result) => {
  item.result = result
  item.score = result === 'PASS' ? (item.maxScore || 10) : 0
}

// 点击"开始检查" → 加载检查项 + 自动开始计划
const startInspection = async () => {
  const plan = currentPlan.value
  if (!plan || selectedRoomIds.value.length === 0) return
  loadingItems.value = true
  try {
    let items = []
    if (plan.inspectionType === 'COMPREHENSIVE') items = await loadItemsForPlan(null)
    else items = await loadItemsForPlan(plan.inspectionType)
    inspectionItems.value = items
    evaluationList.value = items.map(item => ({
      itemId: item.id, itemName: item.name, standard: item.standard || '',
      maxScore: item.maxScore || 10, score: item.maxScore || 10, result: 'PASS', remark: ''
    }))
    // 自动开始计划
    if (plan.status === 'DRAFT' || plan.status === 'SCHEDULED') {
      try { await startInspectionPlan(plan.id); plan.status = 'IN_PROGRESS' } catch {}
    }
    // 平滑滚动到检查项
    await nextTick()
    itemsCardRef.value?.$el?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  } catch { ElMessage.error('加载检查项失败') }
  finally { loadingItems.value = false }
}

const loadRoomsForPlan = async (plan) => {
  try {
    const buildingIds = plan.buildingIds ? plan.buildingIds.split(',').map(s => s.trim()) : []
    const rooms = []
    for (const bId of buildingIds) {
      try { const r = await api.get(`/rooms/building/${bId}`); rooms.push(...(r.data || [])) } catch {}
    }
    availableRooms.value = rooms
  } catch { availableRooms.value = [] }
}

const loadItemsForPlan = async (cat) => {
  try {
    const r = cat ? await getItemsByCategory(cat) : await getActiveItems()
    return r.data || []
  } catch { return [] }
}

const handleSubmit = async () => {
  if (!selectedPlanId.value) return ElMessage.warning('请选择检查计划')
  if (selectedRoomIds.value.length === 0) return ElMessage.warning('请选择检查房间')
  if (evaluationList.value.length === 0) return ElMessage.warning('没有可提交的检查项')
  const unfilled = evaluationList.value.find(e => e.result === 'FAIL' && !e.remark?.trim())
  if (unfilled) return ElMessage.warning(`「${unfilled.itemName}」不合格，请填写扣分原因`)
  const roomCount = selectedRoomIds.value.length
  try { await ElMessageBox.confirm(`确认为 ${roomCount} 个房间提交检查结果？`, '确认提交', { confirmButtonText: '确定提交', cancelButtonText: '取消', type: 'warning' }) } catch { return }
  submitting.value = true
  try {
    const plan = currentPlan.value
    const itemsJson = JSON.stringify(evaluationList.value.map(e => ({ itemId: e.itemId, itemName: e.itemName, score: e.score, result: e.result, remark: e.remark })))
    // 为每个选中房间创建一条记录
    for (const roomId of selectedRoomIds.value) {
      const room = availableRooms.value.find(r => r.id === roomId)
      if (!room) continue
      await createInspectionRecord({
        planId: plan.id, buildingId: room.buildingId, roomId,
        overallScore: Number(totalScore.value), result: failCount.value === 0 ? 'PASS' : 'FAIL',
        itemsJson, remark: overallRemark.value || null
      })
    }
    ElMessage.success(`已为 ${roomCount} 个房间提交检查结果`)
    selectedPlanId.value = null; selectedRoomIds.value = []; evaluationList.value = []; inspectionItems.value = []; overallRemark.value = ''
    loadAvailablePlans()
  } catch (e) { ElMessage.error(e.response?.data?.message || '提交失败') }
  finally { submitting.value = false }
}

onMounted(() => loadAvailablePlans())
</script>

<style scoped>
.execute-page { padding: var(--page-padding); }
.execute-container { max-width: 800px; margin: 0 auto; }

/* 空状态 */
.init-state { padding: 80px 0; }

/* 选择器卡片 */
.select-card { margin-bottom: var(--spacing-md); border: 1px solid var(--color-border); border-radius: var(--radius-lg); }
.select-form { display: flex; align-items: center; flex-wrap: wrap; gap: var(--spacing-xs); }
.select-form .el-form-item { margin-bottom: 0; }

/* 计划详情 */
.plan-info-card { margin-bottom: var(--spacing-md); border: 1px solid var(--color-border); border-radius: var(--radius-lg); }
.card-title { font-weight: var(--font-weight-semibold); }

/* 检查项卡片 */
.items-card { margin-bottom: var(--spacing-md); }
.item-list { display: flex; flex-direction: column; gap: var(--spacing-md); }

.inspect-item {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);
  padding: var(--spacing-lg);
  background: var(--color-bg-white);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
}
.inspect-item--fail { border-color: #fbc4c4; background: #fff5f5; }

/* 卡片头部 */
.item-hd { display: flex; align-items: flex-start; gap: var(--spacing-sm); margin-bottom: var(--spacing-md); }
.item-num { display: flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: var(--color-primary); color: #fff; border-radius: 50%; font-size: var(--font-size-sm); flex-shrink: 0; }
.item-title { flex: 1; }
.item-name { font-size: var(--font-size-base); font-weight: var(--font-weight-semibold); color: var(--color-text-primary); }
.item-std { font-size: var(--font-size-xs); color: var(--color-text-secondary); margin-top: 2px; }

/* 分数徽标 */
.item-score-badge { flex-shrink: 0; padding: 2px 10px; border-radius: var(--radius-round); font-size: var(--font-size-xs); font-weight: var(--font-weight-semibold); }
.score--pass { background: #e8f5e9; color: #2e7d32; }
.score--fail { background: #ffebee; color: #c62828; }

/* 判定按钮组 */
.item-verdict { display: flex; gap: var(--spacing-sm); margin-bottom: 0; }
.verdict-btn {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: var(--spacing-xs);
  padding: 10px; border: 2px solid var(--color-border); border-radius: var(--radius-md);
  background: var(--color-bg-white); font-size: var(--font-size-sm); font-weight: var(--font-weight-medium);
  cursor: pointer; transition: all var(--transition-fast); color: var(--color-text-secondary);
}
.verdict-btn:hover { border-color: var(--color-border-hover); }
.verdict-btn--pass.active { border-color: #52C41A; background: #f6ffed; color: #2e7d32; }
.verdict-btn--fail.active { border-color: #F56C6C; background: #fff2f0; color: #c62828; }

/* 不合格展开区 */
.item-fail-area { margin-top: var(--spacing-md); padding-top: var(--spacing-md); border-top: 1px dashed #fbc4c4; }
.fail-field { display: flex; flex-direction: column; gap: 4px; }
.fail-label { font-size: var(--font-size-xs); color: var(--color-text-regular); font-weight: var(--font-weight-medium); }
.required { color: var(--color-danger); }

.remark-card { margin-bottom: var(--spacing-md); }

/* 吸底提交栏 */
.sticky-footer {
  position: sticky; bottom: 0; z-index: 10;
  display: flex; align-items: center; gap: var(--spacing-lg);
  padding: var(--spacing-md) var(--spacing-lg);
  margin: 0 calc(-1 * var(--page-padding));
  background: var(--color-bg-white);
  border-top: 1px solid var(--color-border);
  box-shadow: 0 -4px 12px rgba(0,0,0,0.06);
}
.footer-stats { flex: 1; display: flex; align-items: center; gap: var(--spacing-lg); min-width: 0; }
.footer-progress { flex: 2; min-width: 120px; }
.footer-label { font-size: var(--font-size-xs); color: var(--color-text-secondary); display: block; margin-bottom: 2px; }
.footer-hint { font-size: var(--font-size-xs); color: var(--color-text-disabled); margin-top: 2px; }
.footer-score { display: flex; align-items: baseline; gap: 2px; white-space: nowrap; }
.footer-score-num { font-size: var(--font-size-xxl); font-weight: var(--font-weight-bold); color: var(--color-primary); }
.footer-score-div { font-size: var(--font-size-sm); color: var(--color-text-disabled); margin: 0 2px; }
.footer-score-max { font-size: var(--font-size-sm); color: var(--color-text-secondary); }
.footer-passfail { display: flex; flex-direction: column; gap: 2px; font-size: var(--font-size-xs); white-space: nowrap; }
.passfail { display: flex; align-items: center; gap: 4px; font-weight: var(--font-weight-medium); }
.passfail--pass { color: var(--color-success); }
.passfail--fail { color: var(--color-danger); }
.submit-btn { white-space: nowrap; font-size: var(--font-size-base); padding: 12px 28px; height: auto; }

@media (max-width: 768px) {
  .execute-container { max-width: 100%; }
  .item-verdict { flex-direction: column; }
  .sticky-footer { flex-direction: column; gap: var(--spacing-sm); margin: 0 calc(-1 * var(--spacing-md)); }
  .footer-stats { width: 100%; flex-wrap: wrap; gap: var(--spacing-sm); }
  .submit-btn { width: 100%; }
}
</style>
