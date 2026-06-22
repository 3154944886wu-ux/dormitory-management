<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公寓楼栋管理</span>
          <el-button type="primary" @click="handleAdd" v-if="isAdmin"><el-icon><Plus /></el-icon>添加楼栋</el-button>
        </div>
      </template>
      <el-table :data="buildings" v-loading="loading" stripe>
        <el-table-column prop="name" label="楼栋名称" width="120" />
        <el-table-column prop="floors" label="楼层数" width="100" />
        <el-table-column prop="roomsPerFloor" label="每层房间数" width="120" />
        <el-table-column label="总房间数" width="100">
          <template #default="{ row }">{{ row.floors * row.roomsPerFloor }}</template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTagType(BUILDING_TYPE_TAG, row.genderType)">{{ getLabel(BUILDING_TYPE_MAP, row.genderType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="manager" label="宿管" width="100" />
        <el-table-column prop="managerPhone" label="宿管电话" width="130" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" :disabled="!isAdmin" />
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column v-if="isAdmin" label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑楼栋' : '添加楼栋'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="楼栋名称" prop="name"><el-input v-model="form.name" placeholder="如：1号楼" /></el-form-item>
        <el-form-item label="楼层数" prop="floors"><el-input-number v-model="form.floors" :min="1" :max="50" /></el-form-item>
        <el-form-item label="每层房间数" prop="roomsPerFloor"><el-input-number v-model="form.roomsPerFloor" :min="1" :max="100" /></el-form-item>
        <el-form-item label="楼栋类型" prop="genderType">
          <el-select v-model="form.genderType"><el-option label="男生楼" value="MALE" /><el-option label="女生楼" value="FEMALE" /><el-option label="混合楼" value="MIXED" /></el-select>
        </el-form-item>
        <el-form-item label="宿管姓名" prop="manager"><el-input v-model="form.manager" /></el-form-item>
        <el-form-item label="宿管电话" prop="managerPhone"><el-input v-model="form.managerPhone" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item>
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
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { buildingAPI } from '@/api/building'
import { useAuth } from '@/composables/useAuth'
import { useConfirm } from '@/composables/useConfirm'
import { BUILDING_TYPE_MAP, BUILDING_TYPE_TAG, getLabel, getTagType } from '@/constants/status'

const { isAdmin } = useAuth()
const { confirm } = useConfirm()

const loading = ref(false)
const buildings = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({ id: null, name: '', floors: 6, roomsPerFloor: 20, genderType: 'MALE', manager: '', managerPhone: '', remark: '' })

const rules = {
  name: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }],
  floors: [{ required: true, message: '请输入楼层数', trigger: 'blur' }],
  roomsPerFloor: [{ required: true, message: '请输入每层房间数', trigger: 'blur' }],
  genderType: [{ required: true, message: '请选择楼栋类型', trigger: 'change' }]
}

const loadBuildings = async () => {
  loading.value = true
  try { const res = await buildingAPI.list(); buildings.value = res.data } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; Object.assign(form, { id: null, name: '', floors: 6, roomsPerFloor: 20, genderType: 'MALE', manager: '', managerPhone: '', remark: '' }); dialogVisible.value = true }
const handleEdit = (row) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }

const handleSubmit = async () => {
  if (!(await formRef.value.validate().catch(() => false))) return
  try {
    isEdit.value ? await buildingAPI.update(form.id, form) : await buildingAPI.create(form)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false; loadBuildings()
  } catch (e) { ElMessage.error(e.response?.data?.message || '操作失败') }
}

const handleDelete = async (row) => {
  if (!(await confirm('删除楼栋', `确定要删除 ${row.name} 吗？`))) return
  try { await buildingAPI.delete(row.id); ElMessage.success('删除成功'); loadBuildings() } catch { ElMessage.error('删除失败') }
}

const handleStatusChange = async (row) => {
  try { await buildingAPI.updateStatus(row.id, row.status); ElMessage.success('状态更新成功') } catch { ElMessage.error('状态更新失败'); row.status = row.status === 1 ? 0 : 1 }
}

onMounted(() => loadBuildings())
</script>
