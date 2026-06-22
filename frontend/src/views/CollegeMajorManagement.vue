<template>
  <div class="college-major-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学院管理</span>
          <el-button type="primary" @click="handleAddCollege" v-if="isAdmin">
            <el-icon><Plus /></el-icon>
            新增学院
          </el-button>
        </div>
      </template>
      <el-table :data="colleges" v-loading="collegeLoading" stripe>
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="name" label="学院名称" min-width="300" />
        <el-table-column label="操作" width="150" fixed="right" v-if="isAdmin">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEditCollege(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDeleteCollege(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>专业管理</span>
          <el-button type="primary" @click="handleAddMajor" v-if="isAdmin">
            <el-icon><Plus /></el-icon>
            新增专业
          </el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-select v-model="filterCollegeId" placeholder="筛选学院" clearable @change="loadMajors" style="width: 220px;">
          <el-option v-for="c in colleges" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </div>

      <el-table :data="majors" v-loading="majorLoading" stripe style="margin-top: 10px;">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="name" label="专业名称" min-width="250" />
        <el-table-column prop="collegeName" label="所属学院" width="200" />
        <el-table-column label="操作" width="150" fixed="right" v-if="isAdmin">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEditMajor(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDeleteMajor(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="collegeDialogVisible" :title="isEditCollege ? '编辑学院' : '新增学院'" width="450px" @close="resetCollegeForm">
      <el-form :model="collegeForm" :rules="collegeRules" ref="collegeFormRef" label-width="100px">
        <el-form-item label="学院名称" prop="name">
          <el-input v-model="collegeForm.name" placeholder="如：计算机学院" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="collegeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitCollege">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="majorDialogVisible" :title="isEditMajor ? '编辑专业' : '新增专业'" width="450px" @close="resetMajorForm">
      <el-form :model="majorForm" :rules="majorRules" ref="majorFormRef" label-width="100px">
        <el-form-item label="专业名称" prop="name">
          <el-input v-model="majorForm.name" placeholder="如：计算机科学与技术" />
        </el-form-item>
        <el-form-item label="所属学院" prop="collegeId">
          <el-select v-model="majorForm.collegeId" placeholder="请选择学院" style="width: 100%;">
            <el-option v-for="c in colleges" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="majorDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitMajor">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { collegeAPI } from '@/api/college'
import { majorAPI } from '@/api/major'

const collegeLoading = ref(false)
const majorLoading = ref(false)
const colleges = ref([])
const majors = ref([])
const filterCollegeId = ref(null)

const collegeDialogVisible = ref(false)
const isEditCollege = ref(false)
const collegeFormRef = ref(null)
const collegeForm = reactive({ id: null, name: '' })
const collegeRules = {
  name: [{ required: true, message: '请输入学院名称', trigger: 'blur' }]
}

const majorDialogVisible = ref(false)
const isEditMajor = ref(false)
const majorFormRef = ref(null)
const majorForm = reactive({ id: null, name: '', collegeId: null })
const majorRules = {
  name: [{ required: true, message: '请输入专业名称', trigger: 'blur' }],
  collegeId: [{ required: true, message: '请选择所属学院', trigger: 'change' }]
}

const isAdmin = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.role?.toUpperCase() === 'ADMIN'
})

const loadColleges = async () => {
  collegeLoading.value = true
  try {
    const res = await collegeAPI.list()
    colleges.value = res.data || []
  } catch (error) {
    ElMessage.error('加载学院列表失败')
  } finally {
    collegeLoading.value = false
  }
}

const loadMajors = async () => {
  majorLoading.value = true
  try {
    const res = await majorAPI.list(filterCollegeId.value)
    majors.value = res.data || []
  } catch (error) {
    ElMessage.error('加载专业列表失败')
  } finally {
    majorLoading.value = false
  }
}

const resetCollegeForm = () => {
  Object.assign(collegeForm, { id: null, name: '' })
}

const resetMajorForm = () => {
  Object.assign(majorForm, { id: null, name: '', collegeId: null })
}

const handleAddCollege = () => {
  isEditCollege.value = false
  resetCollegeForm()
  collegeDialogVisible.value = true
}

const handleEditCollege = (row) => {
  isEditCollege.value = true
  Object.assign(collegeForm, { id: row.id, name: row.name })
  collegeDialogVisible.value = true
}

const handleSubmitCollege = async () => {
  const valid = await collegeFormRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEditCollege.value) {
      await collegeAPI.update(collegeForm.id, { name: collegeForm.name })
      ElMessage.success('更新成功')
    } else {
      await collegeAPI.create({ name: collegeForm.name })
      ElMessage.success('创建成功')
    }
    collegeDialogVisible.value = false
    await loadColleges()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleDeleteCollege = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除学院"${row.name}"吗？`, '提示', { type: 'warning' })
    await collegeAPI.delete(row.id)
    ElMessage.success('删除成功')
    await loadColleges()
    await loadMajors()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

const handleAddMajor = () => {
  isEditMajor.value = false
  resetMajorForm()
  majorDialogVisible.value = true
}

const handleEditMajor = (row) => {
  isEditMajor.value = true
  Object.assign(majorForm, { id: row.id, name: row.name, collegeId: row.collegeId })
  majorDialogVisible.value = true
}

const handleSubmitMajor = async () => {
  const valid = await majorFormRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEditMajor.value) {
      await majorAPI.update(majorForm.id, { name: majorForm.name, collegeId: majorForm.collegeId })
      ElMessage.success('更新成功')
    } else {
      await majorAPI.create({ name: majorForm.name, collegeId: majorForm.collegeId })
      ElMessage.success('创建成功')
    }
    majorDialogVisible.value = false
    await loadMajors()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleDeleteMajor = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除专业"${row.name}"吗？`, '提示', { type: 'warning' })
    await majorAPI.delete(row.id)
    ElMessage.success('删除成功')
    await loadMajors()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadColleges()
  loadMajors()
})
</script>

<style scoped>
.college-major-container {
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
