<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>归寝规则与电子围栏</span>
          <el-button type="primary" @click="openDialog()">新增规则</el-button>
        </div>
      </template>

      <el-table :data="rules" v-loading="loading" stripe>
        <el-table-column prop="name" label="规则名称" min-width="140" />
        <el-table-column prop="buildingName" label="适用楼栋" min-width="120">
          <template #default="{ row }">{{ row.buildingName || '全局默认' }}</template>
        </el-table-column>
        <el-table-column label="时间规则" min-width="230">
          <template #default="{ row }">
            {{ row.checkStartTime }} - {{ row.checkEndTime }}，未归 {{ row.absentDeadline || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="电子围栏" min-width="220">
          <template #default="{ row }">
            <span v-if="row.allowedLatitude && row.allowedLongitude">
              {{ row.allowedLatitude }}, {{ row.allowedLongitude }} / {{ row.allowedRadius || 500 }}米
            </span>
            <span v-else>未配置</span>
          </template>
        </el-table-column>
        <el-table-column prop="maxLocationAccuracy" label="精度阈值" width="100" />
        <el-table-column prop="isDefault" label="默认" width="80">
          <template #default="{ row }"><el-tag v-if="row.isDefault === 1">默认</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              inline-prompt
              active-text="启"
              inactive-text="停"
              :loading="row._toggling"
              @change="(val) => toggleStatus(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="success" @click="setDefault(row)">设默认</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑规则' : '新增规则'" width="640px">
      <el-form :model="form" label-width="130px">
        <el-form-item label="规则名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="适用楼栋">
          <el-select v-model="form.buildingId" clearable placeholder="留空表示全局规则" style="width: 100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="归寝开始"><el-time-picker v-model="form.checkStartTime" v-bind="timePickerProps" /></el-form-item>
        <el-form-item label="归寝结束"><el-time-picker v-model="form.checkEndTime" v-bind="timePickerProps" /></el-form-item>
        <el-form-item label="未归截止"><el-time-picker v-model="form.absentDeadline" v-bind="timePickerProps" /></el-form-item>
        <el-form-item>
          <el-alert
            title="时间须满足：归寝开始 < 归寝结束 < 未归截止（超过结束至未归前记为晚归；超过未归自动记未归）"
            type="info"
            :closable="false"
            show-icon
          />
        </el-form-item>
        <el-form-item label="适用星期">
          <div class="weekday-row">
            <button
              v-for="day in weekDays"
              :key="day.value"
              type="button"
              class="weekday-chip"
              :class="{ active: selectedDays.includes(day.value) }"
              @click="toggleApplyDay(day.value)"
            >
              {{ day.label }}
            </button>
          </div>
        </el-form-item>
        <el-form-item label="围栏中心点">
          <div class="geo-field">
            <div class="geo-rows">
              <div class="geo-row">
                <span class="geo-label">纬度</span>
                <el-input-number v-model="form.allowedLatitude" :precision="7" :min="-90" :max="90" controls-position="right" class="geo-input" />
              </div>
              <div class="geo-row">
                <span class="geo-label">经度</span>
                <el-input-number v-model="form.allowedLongitude" :precision="7" :min="-180" :max="180" controls-position="right" class="geo-input" />
              </div>
            </div>
            <el-button type="primary" plain :loading="locating" @click="fillCurrentLocation">定位当前</el-button>
          </div>
        </el-form-item>
        <el-form-item label="半径(米)"><el-input-number v-model="form.allowedRadius" :min="50" :max="5000" /></el-form-item>
        <el-form-item label="必须定位"><el-switch v-model="form.requireLocation" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="最大精度误差"><el-input-number v-model="form.maxLocationAccuracy" :min="20" :max="1000" /></el-form-item>
        <el-form-item label="异常阈值"><el-input-number v-model="form.exceptionThreshold" :min="1" :max="30" /></el-form-item>
        <el-form-item label="是否默认"><el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCheckRules, createCheckRule, updateCheckRule, deleteCheckRule, setDefaultRule, toggleCheckRuleStatus } from '@/api/checkRule'
import { buildingAPI } from '@/api/building'

const loading = ref(false)
const saving = ref(false)
const locating = ref(false)
const dialogVisible = ref(false)
const rules = ref([])
const buildings = ref([])

const timePickerProps = {
  valueFormat: 'HH:mm:ss',
  format: 'HH:mm:ss'
}

const weekDays = [
  { value: 1, label: '周一' },
  { value: 2, label: '周二' },
  { value: 3, label: '周三' },
  { value: 4, label: '周四' },
  { value: 5, label: '周五' },
  { value: 6, label: '周六' },
  { value: 7, label: '周日' }
]

const selectedDays = ref([1, 2, 3, 4, 5])

const form = reactive(defaultForm())

function defaultForm() {
  return {
    id: null,
    name: '',
    buildingId: null,
    checkStartTime: '22:00:00',
    checkEndTime: '23:00:00',
    absentDeadline: '00:00:00',
    applyDays: '1,2,3,4,5',
    allowLateCount: 3,
    isDefault: 0,
    status: 1,
    remark: '',
    allowedLatitude: null,
    allowedLongitude: null,
    allowedRadius: 500,
    requireLocation: 1,
    maxLocationAccuracy: 200,
    exceptionThreshold: 3
  }
}

function parseApplyDays(value) {
  if (!value) return [1, 2, 3, 4, 5]
  return value
    .split(',')
    .map((item) => Number(item.trim()))
    .filter((item) => item >= 1 && item <= 7)
}

function syncApplyDays() {
  form.applyDays = [...selectedDays.value].sort((a, b) => a - b).join(',')
}

function toggleApplyDay(day) {
  const index = selectedDays.value.indexOf(day)
  if (index >= 0) {
    if (selectedDays.value.length <= 1) {
      ElMessage.warning('至少选择一天')
      return
    }
    selectedDays.value.splice(index, 1)
  } else {
    selectedDays.value.push(day)
  }
  syncApplyDays()
}

const loadData = async () => {
  loading.value = true
  try {
    const [ruleRes, buildingRes] = await Promise.all([getCheckRules(), buildingAPI.list()])
    rules.value = ruleRes.data || []
    buildings.value = buildingRes.data || []
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  Object.assign(form, defaultForm(), row || {})
  selectedDays.value = parseApplyDays(form.applyDays)
  dialogVisible.value = true
}

const fillCurrentLocation = () => {
  if (!navigator.geolocation) {
    ElMessage.error('当前浏览器不支持定位功能')
    return
  }
  locating.value = true
  navigator.geolocation.getCurrentPosition(
    (position) => {
      form.allowedLatitude = Number(position.coords.latitude.toFixed(7))
      form.allowedLongitude = Number(position.coords.longitude.toFixed(7))
      locating.value = false
      ElMessage.success('已填入当前位置')
    },
    (error) => {
      locating.value = false
      const messages = {
        1: '定位权限被拒绝，请在浏览器设置中允许定位',
        2: '无法获取当前位置，请检查定位服务是否开启',
        3: '获取定位超时，请重试'
      }
      ElMessage.error(messages[error.code] || error.message || '定位失败')
    },
    { enableHighAccuracy: true, timeout: 12000, maximumAge: 0 }
  )
}

function toWindowMinutes(timeStr, anchorStr) {
  const toMinutes = (str) => {
    const [h, m] = str.split(':').map(Number)
    return h * 60 + m
  }
  const minutes = toMinutes(timeStr)
  const anchor = toMinutes(anchorStr)
  if (minutes <= anchor && timeStr !== anchorStr) {
    return minutes + 24 * 60
  }
  return minutes
}

function validateTimeOrder() {
  const { checkStartTime: start, checkEndTime: end, absentDeadline: absent } = form
  if (!start || !end || !absent) {
    ElMessage.warning('请完整填写归寝开始、结束和未归截止时间')
    return false
  }
  const startMin = toWindowMinutes(start, start)
  const endMin = toWindowMinutes(end, start)
  const absentMin = toWindowMinutes(absent, start)
  if (!(startMin < endMin && endMin < absentMin)) {
    ElMessage.warning('时间规则必须满足：归寝开始 < 归寝结束 < 未归截止')
    return false
  }
  return true
}

function buildSavePayload() {
  return {
    name: form.name.trim(),
    buildingId: form.buildingId || null,
    checkStartTime: form.checkStartTime,
    checkEndTime: form.checkEndTime,
    absentDeadline: form.absentDeadline,
    applyDays: form.applyDays,
    allowLateCount: form.allowLateCount,
    isDefault: form.isDefault,
    status: form.status,
    remark: form.remark || '',
    allowedLatitude: form.allowedLatitude,
    allowedLongitude: form.allowedLongitude,
    allowedRadius: form.allowedRadius,
    requireLocation: form.requireLocation,
    maxLocationAccuracy: form.maxLocationAccuracy,
    exceptionThreshold: form.exceptionThreshold
  }
}

const save = async () => {
  if (!form.name?.trim()) {
    ElMessage.warning('请输入规则名称')
    return
  }
  if (!selectedDays.value.length) {
    ElMessage.warning('请至少选择一天适用星期')
    return
  }
  if (!validateTimeOrder()) return
  syncApplyDays()
  saving.value = true
  try {
    const payload = buildSavePayload()
    if (form.id) await updateCheckRule(form.id, payload)
    else await createCheckRule(payload)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const setDefault = async (row) => {
  await setDefaultRule(row.id)
  ElMessage.success('已设为默认')
  loadData()
}

const toggleStatus = async (row, enabled) => {
  row._toggling = true
  try {
    const status = enabled ? 1 : 0
    await toggleCheckRuleStatus(row.id, status)
    row.status = status
    ElMessage.success(status === 1 ? '规则已启用' : '规则已停用')
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    row._toggling = false
  }
}

const remove = async (row) => {
  await ElMessageBox.confirm(`确认删除规则「${row.name}」？`, '提示', { type: 'warning' })
  await deleteCheckRule(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.weekday-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.weekday-chip {
  min-width: 44px;
  padding: 6px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background: #fff;
  color: #606266;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.weekday-chip:hover {
  color: var(--el-color-primary);
  border-color: #a0cfff;
}
.weekday-chip.active {
  color: #fff;
  background: var(--el-color-primary);
  border-color: var(--el-color-primary);
}
.geo-field {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  width: 100%;
}
.geo-rows {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.geo-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.geo-label {
  width: 36px;
  color: #606266;
  font-size: 13px;
  text-align: right;
  flex-shrink: 0;
}
.geo-input {
  width: 220px;
}
</style>
