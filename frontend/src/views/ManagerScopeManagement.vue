<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>Manager 账号范围管理</span>
          <el-button type="primary" @click="openDialog()">新增范围</el-button>
        </div>
      </template>

      <el-alert title="manager 账号由用户管理中创建，范围可按楼栋或班级绑定；两者同时填写时表示交叉范围。" type="info" :closable="false" class="mb" />

      <el-table :data="scopes" v-loading="loading" stripe>
        <el-table-column prop="username" label="账号" width="140" />
        <el-table-column prop="nickname" label="姓名" width="140" />
        <el-table-column prop="buildingName" label="楼栋范围" min-width="140">
          <template #default="{ row }">{{ row.buildingName || '不限楼栋' }}</template>
        </el-table-column>
        <el-table-column prop="className" label="班级范围" min-width="140">
          <template #default="{ row }">{{ row.className || '不限班级' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">停用</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="管理范围" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="Manager账号">
          <el-select v-model="form.userId" filterable style="width: 100%">
            <el-option v-for="u in managers" :key="u.id" :label="`${u.username} ${u.nickname || ''}`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="form.buildingId" clearable style="width: 100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-input v-model="form.className" placeholder="例如 计科2301，留空表示不限班级" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'
import { buildingAPI } from '@/api/building'
import managerScopeAPI from '@/api/managerScope'

const loading = ref(false)
const scopes = ref([])
const users = ref([])
const buildings = ref([])
const dialogVisible = ref(false)
const form = reactive({ id: null, userId: null, buildingId: null, className: '', status: 1 })

const managers = computed(() => users.value.filter(u => (u.role || '').toUpperCase() === 'MANAGER'))

const loadData = async () => {
  loading.value = true
  try {
    const [scopeRes, userRes, buildingRes] = await Promise.all([
      managerScopeAPI.list(),
      api.get('/users'),
      buildingAPI.list()
    ])
    scopes.value = scopeRes.data || []
    users.value = userRes.data || []
    buildings.value = buildingRes.data || []
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  Object.assign(form, { id: null, userId: null, buildingId: null, className: '', status: 1 }, row || {})
  dialogVisible.value = true
}

const save = async () => {
  if (!form.userId) {
    ElMessage.warning('请选择 manager 账号')
    return
  }
  if (form.id) await managerScopeAPI.update(form.id, form)
  else await managerScopeAPI.create(form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const remove = async (row) => {
  await ElMessageBox.confirm('确认停用该管理范围？', '提示', { type: 'warning' })
  await managerScopeAPI.delete(row.id)
  ElMessage.success('已停用')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.mb { margin-bottom: 16px; }
</style>
