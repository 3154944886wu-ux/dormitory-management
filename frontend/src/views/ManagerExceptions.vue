<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>归寝异常处理</span>
          <div>
            <el-button @click="loadData">刷新</el-button>
            <el-button type="success" @click="exportData">导出</el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="filters">
        <el-form-item label="日期">
          <el-date-picker v-model="filters.dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" />
        </el-form-item>
        <el-form-item label="异常类型">
          <el-select v-model="filters.exceptionType" clearable style="width: 130px">
            <el-option label="晚归" :value="1" />
            <el-option label="未归" :value="2" />
            <el-option label="缺卡" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="filters.handled" clearable style="width: 130px">
            <el-option label="未处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="exceptions" v-loading="loading" stripe>
        <el-table-column prop="studentName" label="学生" width="100" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="className" label="班级" min-width="130" />
        <el-table-column prop="buildingName" label="楼栋" width="100" />
        <el-table-column prop="roomNumber" label="房间" width="80" />
        <el-table-column prop="exceptionDate" label="日期" width="120" />
        <el-table-column prop="exceptionType" label="类型" width="90">
          <template #default="{ row }"><el-tag :type="typeMeta(row.exceptionType).type">{{ typeMeta(row.exceptionType).text }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="handled" label="状态" width="90">
          <template #default="{ row }"><el-tag :type="row.handled === 1 ? 'success' : 'warning'">{{ row.handled === 1 ? '已处理' : '未处理' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="handleResult" label="处理结果" min-width="120" />
        <el-table-column prop="handleNote" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.handled !== 1" link type="primary" @click="openHandle(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="处理异常" width="520px">
      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-select v-model="handleForm.handleResult" style="width: 100%">
            <el-option label="已安全归寝" value="safe_return" />
            <el-option label="外宿已报备" value="reported_stay_out" />
            <el-option label="联系不上待跟进" value="unreachable" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input v-model="handleForm.handleNote" type="textarea" :rows="4" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitHandle">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { handleException, searchExceptions, exportExceptions } from '@/api/checkException'

const loading = ref(false)
const saving = ref(false)
const exceptions = ref([])
const dialogVisible = ref(false)
const currentRow = ref(null)
const filters = reactive({ dateRange: [], exceptionType: '', handled: 0 })
const handleForm = reactive({ handleResult: 'safe_return', handleNote: '' })

const params = () => ({
  startDate: filters.dateRange?.[0],
  endDate: filters.dateRange?.[1],
  exceptionType: filters.exceptionType || undefined,
  handled: filters.handled === '' ? undefined : filters.handled
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await searchExceptions(params())
    exceptions.value = res.data || []
  } finally {
    loading.value = false
  }
}

const openHandle = (row) => {
  currentRow.value = row
  Object.assign(handleForm, { handleResult: 'safe_return', handleNote: '' })
  dialogVisible.value = true
}

const submitHandle = async () => {
  saving.value = true
  try {
    await handleException(currentRow.value.id, handleForm)
    ElMessage.success('处理成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const exportData = async () => {
  const blob = await exportExceptions(params())
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '归寝异常记录.csv'
  a.click()
  URL.revokeObjectURL(url)
}

const typeMeta = (type) => {
  const map = {
    1: { text: '晚归', type: 'warning' },
    2: { text: '未归', type: 'danger' },
    3: { text: '缺卡', type: 'info' }
  }
  return map[type] || { text: '未知', type: 'info' }
}

onMounted(loadData)
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
