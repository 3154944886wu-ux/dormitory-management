<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教师管理</span>
          <el-button type="primary" @click="openTeacherDialog()">
            <el-icon><Plus /></el-icon>
            添加教师
          </el-button>
        </div>
      </template>

      <el-alert
        title="教师使用6位工号登录，users.username 为工号，nickname 为姓名。添加时自动注册账号，初始密码与工号相同。"
        type="info"
        :closable="false"
        class="mb"
      />

      <el-table :data="teachers" v-loading="loading" stripe row-key="id">
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="scope-panel">
              <div class="scope-panel-header">
                <span>管理范围绑定</span>
              </div>
              <el-table v-if="row.scopes?.length" :data="row.scopes" size="small" border>
                <el-table-column prop="buildingName" label="楼栋" min-width="140">
                  <template #default="{ row: scope }">{{ scope.buildingName || '不限楼栋' }}</template>
                </el-table-column>
                <el-table-column prop="className" label="班级" min-width="140">
                  <template #default="{ row: scope }">{{ scope.className || '不限班级' }}</template>
                </el-table-column>
                <el-table-column label="操作" width="160">
                  <template #default="{ row: scope }">
                    <el-button link type="primary" @click="openScopeDialog(row, scope)">换绑</el-button>
                    <el-button link type="danger" @click="removeScope(row, scope)">解绑</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-else description="暂无绑定" :image-size="60" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="employeeNo" label="工号" width="100" />
        <el-table-column prop="nickname" label="姓名" width="120" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column label="绑定数" width="90">
          <template #default="{ row }">{{ row.scopes?.length || 0 }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openTeacherDialog(row)">编辑</el-button>
            <el-button link type="primary" @click="openScopeDialog(row)">添加绑定</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="deleteTeacher(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 教师信息对话框 -->
    <el-dialog v-model="teacherDialogVisible" :title="teacherForm.id ? '编辑教师' : '添加教师'" width="520px">
      <el-form :model="teacherForm" label-width="90px">
        <el-form-item label="工号" required>
          <el-input
            v-model="teacherForm.employeeNo"
            :disabled="!!teacherForm.id"
            maxlength="6"
            placeholder="6位数字，如 010001"
          />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="teacherForm.name" placeholder="教师姓名" />
        </el-form-item>
        <el-form-item v-if="!teacherForm.id">
          <el-alert title="将自动创建登录账号，并生成随机初始密码（仅创建成功时展示一次）" type="success" :closable="false" show-icon />
        </el-form-item>
        <el-form-item v-if="teacherForm.id" label="新密码">
          <el-input
            v-model="teacherForm.password"
            type="password"
            show-password
            placeholder="不修改请留空"
          />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="teacherForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="teacherForm.email" />
        </el-form-item>
        <template v-if="!teacherForm.id">
          <el-divider content-position="left">初始绑定（可选）</el-divider>
          <el-form-item label="楼栋">
            <el-select v-model="teacherForm.buildingId" clearable placeholder="不选表示不限楼栋" style="width: 100%">
              <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="班级">
            <el-select
              v-model="teacherForm.className"
              clearable
              filterable
              placeholder="不选表示不限班级"
              style="width: 100%"
              :disabled="!classNames.length"
            >
              <el-option v-for="c in classNames" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="teacherDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveTeacher">保存</el-button>
      </template>
    </el-dialog>

    <!-- 范围绑定对话框 -->
    <el-dialog v-model="scopeDialogVisible" :title="scopeForm.id ? '换绑' : '添加绑定'" width="480px">
      <el-form :model="scopeForm" label-width="90px">
        <el-form-item label="教师">
          <el-input :model-value="scopeTeacherLabel" disabled />
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="scopeForm.buildingId" clearable placeholder="不选表示不限楼栋" style="width: 100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select
            v-model="scopeForm.className"
            clearable
            filterable
            placeholder="不选表示不限班级"
            style="width: 100%"
            :disabled="!classNames.length"
          >
            <el-option v-for="c in classNames" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-alert
            title="换绑/添加时：楼栋、班级可只选其一或都选；若选择则须为系统中已有数据。两项都不想要请点「解绑」，勿留空保存。"
            type="info"
            :closable="false"
            show-icon
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scopeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveScope">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { teacherAPI } from '@/api/teacher'
import { buildingAPI } from '@/api/building'

const loading = ref(false)
const saving = ref(false)
const teachers = ref([])
const buildings = ref([])
const classNames = ref([])
const teacherDialogVisible = ref(false)
const scopeDialogVisible = ref(false)
const scopeTeacher = ref(null)

const teacherForm = reactive({
  id: null,
  employeeNo: '',
  name: '',
  password: '',
  phone: '',
  email: '',
  buildingId: null,
  className: ''
})

const scopeForm = reactive({
  id: null,
  buildingId: null,
  className: ''
})

const scopeTeacherLabel = computed(() => {
  if (!scopeTeacher.value) return ''
  const t = scopeTeacher.value
  return `${t.employeeNo || t.username}${t.nickname ? `（${t.nickname}）` : ''}`
})

const loadData = async () => {
  loading.value = true
  try {
    const [teacherRes, buildingRes, classRes] = await Promise.all([
      teacherAPI.list(),
      buildingAPI.list(),
      teacherAPI.classNames()
    ])
    teachers.value = teacherRes.data || []
    buildings.value = buildingRes.data || []
    classNames.value = classRes.data || []
  } finally {
    loading.value = false
  }
}

const resetTeacherForm = () => {
  Object.assign(teacherForm, {
    id: null,
    employeeNo: '',
    name: '',
    password: '',
    phone: '',
    email: '',
    buildingId: null,
    className: ''
  })
}

const resetScopeForm = () => {
  Object.assign(scopeForm, { id: null, buildingId: null, className: '' })
}

const openTeacherDialog = (row) => {
  resetTeacherForm()
  if (row) {
    Object.assign(teacherForm, {
      id: row.id,
      employeeNo: row.employeeNo || row.username,
      name: row.nickname || '',
      password: '',
      phone: row.phone || '',
      email: row.email || ''
    })
  }
  teacherDialogVisible.value = true
}

const openScopeDialog = (teacher, scope) => {
  scopeTeacher.value = teacher
  resetScopeForm()
  if (scope) {
    Object.assign(scopeForm, {
      id: scope.id,
      buildingId: scope.buildingId ?? null,
      className: scope.className || null
    })
  }
  scopeDialogVisible.value = true
}

const saveTeacher = async () => {
  if (!teacherForm.id) {
    if (!/^\d{6}$/.test(teacherForm.employeeNo?.trim() || '')) {
      ElMessage.warning('工号必须为6位数字')
      return
    }
    if (!teacherForm.name?.trim()) {
      ElMessage.warning('请输入姓名')
      return
    }
  }

  saving.value = true
  try {
    if (teacherForm.id) {
      await teacherAPI.update(teacherForm.id, {
        nickname: teacherForm.name,
        phone: teacherForm.phone,
        email: teacherForm.email,
        password: teacherForm.password || undefined
      })
      ElMessage.success('保存成功')
    } else {
      const scopes = []
      if (teacherForm.buildingId || teacherForm.className) {
        scopes.push({
          buildingId: teacherForm.buildingId || null,
          className: teacherForm.className || null
        })
      }
      const res = await teacherAPI.create({
        employeeNo: teacherForm.employeeNo.trim(),
        name: teacherForm.name.trim(),
        phone: teacherForm.phone,
        email: teacherForm.email,
        scopes
      })
      const initialPassword = res?.data?.initialPassword
      if (initialPassword) {
        await ElMessageBox.alert(
          `登录账号：${res.data.employeeNo || teacherForm.employeeNo}\n初始密码：${initialPassword}\n请妥善告知教师并提醒尽快修改密码。`,
          '教师账号已创建',
          { confirmButtonText: '我已记下' }
        )
      } else {
        ElMessage.success('保存成功')
      }
    }
    teacherDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const saveScope = async () => {
  if (!scopeForm.buildingId && !scopeForm.className) {
    ElMessage.warning('请至少选择楼栋或班级之一')
    return
  }
  saving.value = true
  try {
    const payload = {
      buildingId: scopeForm.buildingId || null,
      className: scopeForm.className || null
    }
    if (scopeForm.id) {
      await teacherAPI.updateScope(scopeForm.id, payload)
      ElMessage.success('换绑成功')
    } else {
      await teacherAPI.addScope(scopeTeacher.value.id, payload)
      ElMessage.success('绑定成功')
    }
    scopeDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    saving.value = false
  }
}

const removeScope = async (teacher, scope) => {
  await ElMessageBox.confirm(
    `确认解绑「${teacher.nickname || teacher.username}」的该管理范围？`,
    '提示',
    { type: 'warning' }
  )
  try {
    await teacherAPI.removeScope(scope.id)
    ElMessage.success('已解绑')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const toggleStatus = async (row) => {
  const nextStatus = row.status === 1 ? 0 : 1
  const action = nextStatus === 1 ? '启用' : '停用'
  await ElMessageBox.confirm(`确认${action}教师「${row.nickname || row.username}」？`, '提示', { type: 'warning' })
  try {
    await teacherAPI.update(row.id, { status: nextStatus })
    ElMessage.success(`已${action}`)
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const deleteTeacher = async (row) => {
  await ElMessageBox.confirm(
    `确认删除教师「${row.nickname || row.employeeNo}」？将同时删除其登录账号及全部绑定。`,
    '提示',
    { type: 'warning' }
  )
  try {
    await teacherAPI.delete(row.id)
    ElMessage.success('教师已删除')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '删除失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.mb { margin-bottom: 16px; }
.scope-panel { padding: 8px 16px 16px 48px; }
.scope-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-weight: 500;
  color: #606266;
}
</style>
