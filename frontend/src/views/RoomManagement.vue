<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>房间管理</span>
          <div class="header-actions">
            <el-select v-model="filterBuildingId" placeholder="选择楼栋" clearable @change="onFilterChange" style="width:140px">
              <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
            </el-select>
            <el-button type="primary" @click="handleAdd" v-if="isAdmin"><el-icon><Plus /></el-icon>添加房间</el-button>
          </div>
        </div>
      </template>
      <el-table :data="rooms" v-loading="loading" stripe>
        <el-table-column prop="roomNumber" label="房间号" width="120" />
        <el-table-column label="所属楼栋" width="120"><template #default="{ row }">{{ getBuildingName(row.buildingId) }}</template></el-table-column>
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column label="已入住" width="80">
          <template #default="{ row }"><el-tag :type="liveOccupancy(row) >= row.capacity ? 'danger' : 'success'">{{ liveOccupancy(row) }} / {{ row.capacity }}</el-tag></template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }"><el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" :disabled="!isAdmin" /></template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column v-if="isAdmin" label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20,50,100]" layout="total,sizes,prev,pager,next" background @size-change="loadRooms" @current-change="loadRooms" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑房间' : '添加房间'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="所属楼栋" prop="buildingId"><el-select v-model="form.buildingId" style="width:100%"><el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" /></el-select></el-form-item>
        <el-form-item label="房间号" prop="roomNumber"><el-input v-model="form.roomNumber" placeholder="如：101" /></el-form-item>
        <el-form-item label="楼层" prop="floor"><el-input-number v-model="form.floor" :min="1" :max="50" /></el-form-item>
        <el-form-item label="容量" prop="capacity"><el-input-number v-model="form.capacity" :min="1" :max="10" /></el-form-item>
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
import { getRooms, createRoom, updateRoom, deleteRoom, updateRoomStatus } from '@/api/room'
import { buildingAPI } from '@/api/building'
import { useAuth } from '@/composables/useAuth'
import { useConfirm } from '@/composables/useConfirm'

const { isAdmin } = useAuth()
const { confirm } = useConfirm()

const loading = ref(false)
const rooms = ref([])
const buildings = ref([])
const filterBuildingId = ref(null)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({ id: null, buildingId: null, roomNumber: '', floor: 1, capacity: 4, remark: '' })

const rules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'blur' }]
}

const getBuildingName = (id) => buildings.value.find(b => b.id === id)?.name || '-'
const liveOccupancy = (row) => row?.occupancy != null ? row.occupancy : (row?.currentCount || 0)

const loadBuildings = async () => {
  try { const r = await buildingAPI.list(); buildings.value = r.data } catch {}
}

const loadRooms = async () => {
  loading.value = true
  try {
    const params = { pageNum: currentPage.value, pageSize: pageSize.value }
    if (filterBuildingId.value) params.buildingId = filterBuildingId.value
    const res = await getRooms(params)
    const data = res.data || res
    rooms.value = data.list || data.content || data.rows || data
    total.value = data.total || data.totalElements || data.count || 0
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const onFilterChange = () => { currentPage.value = 1; loadRooms() }

const handleAdd = () => { isEdit.value = false; Object.assign(form, { id: null, buildingId: filterBuildingId.value || null, roomNumber: '', floor: 1, capacity: 4, remark: '' }); dialogVisible.value = true }
const handleEdit = (row) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }

const handleSubmit = async () => {
  if (!(await formRef.value.validate().catch(() => false))) return
  try {
    isEdit.value ? await updateRoom(form.id, form) : await createRoom(form)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false; loadRooms()
  } catch (e) { ElMessage.error(e.response?.data?.message || '操作失败') }
}

const handleDelete = async (row) => {
  if (!(await confirm('删除房间', `确定要删除 ${row.roomNumber} 吗？`))) return
  try { await deleteRoom(row.id); ElMessage.success('删除成功'); loadRooms() } catch { ElMessage.error('删除失败') }
}

const handleStatusChange = async (row) => {
  try { await updateRoomStatus(row.id, row.status); ElMessage.success('状态更新成功') } catch { ElMessage.error('状态更新失败'); row.status = row.status === 1 ? 0 : 1 }
}

onMounted(() => { loadBuildings(); loadRooms() })
</script>
