<template>
  <div class="student-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
          <el-tabs v-model="activeTab" style="flex: 1; margin-left: 20px;">
            <el-tab-pane label="学生列表" name="students" />
            <el-tab-pane label="调换申请" name="relocations" />
          </el-tabs>
          <div class="header-actions" v-show="activeTab === 'students'">
            <el-input
              v-model="searchName"
              placeholder="搜索学生姓名"
              clearable
              @keyup.enter="handleSearch"
              style="width: 200px; margin-right: 10px;"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
            <el-button type="primary" @click="handleAdd" v-if="isAdmin">
              <el-icon><Plus /></el-icon>
              添加学生
            </el-button>
          </div>
        </div>
      </template>
      
      <div v-show="activeTab === 'students'">
      <el-table :data="students" v-loading="loading" stripe>
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.gender === '男' ? 'primary' : 'danger'">
              {{ row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="department" label="院系" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column label="住宿信息" width="150">
          <template #default="{ row }">
            <div v-if="row.roomId">
              {{ getBuildingName(row.roomId) }} - {{ getRoomNumber(row.roomId) }}
            </div>
            <el-tag v-else type="info" size="small">未分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在住' : '已退宿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="primary"
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="row.status === 1 && row.roomId"
              type="warning"
              link
              @click="handleRelocate(row)"
            >
              调宿
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="warning"
              link
              @click="handleCheckOut(row)"
            >
              退宿
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-if="isAdmin">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end;"
      />
      </div>

      <!-- 调换申请标签页 -->
      <div v-show="activeTab === 'relocations'">
        <div style="margin-bottom: 12px;">
          <el-radio-group v-model="relocationStatusFilter" @change="loadRelocations">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="pending">待审核</el-radio-button>
            <el-radio-button label="approved">已通过</el-radio-button>
            <el-radio-button label="rejected">已拒绝</el-radio-button>
            <el-radio-button label="executed">已执行</el-radio-button>
          </el-radio-group>
        </div>

        <el-table :data="relocationApps" v-loading="relocationLoading" stripe>
          <el-table-column prop="studentName" label="学生姓名" width="100" />
          <el-table-column prop="studentNo" label="学号" width="120" />
          <el-table-column label="当前房间" width="120">
            <template #default="{ row }">
              {{ row.currentBuildingName }} {{ row.currentRoomNumber }}
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="申请理由" min-width="180" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="relocationStatusTag(row.status)">
                {{ relocationStatusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reviewComment" label="审核意见" width="150" show-overflow-tooltip />
          <el-table-column label="新房间" width="120">
            <template #default="{ row }">
              <span v-if="row.newRoomNumber">{{ row.newBuildingName }} {{ row.newRoomNumber }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="申请时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button
                v-if="row.status === 'pending'"
                type="success"
                link
                @click="handleApprove(row)"
              >
                通过
              </el-button>
              <el-button
                v-if="row.status === 'pending'"
                type="danger"
                link
                @click="handleReject(row)"
              >
                拒绝
              </el-button>
              <el-button
                v-if="row.status === 'approved'"
                type="primary"
                link
                @click="handleExecuteRelocation(row)"
              >
                执行调换
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑学生' : '添加学生'"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择" style="width: 100%;">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="院系" prop="department">
              <el-input v-model="form.department" placeholder="请输入院系" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input v-model="form.className" placeholder="请输入班级" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="分配房间" prop="roomId">
          <el-cascader
            v-model="form.roomId"
            :options="roomOptions"
            :props="{ value: 'id', label: 'name', checkStrictly: false }"
            placeholder="请选择楼栋和房间"
            clearable
            style="width: 100%;"
            @change="onRoomChange"
          />
        </el-form-item>
        <el-form-item label="分配床位" prop="bedNumber" v-if="selectedRoomId">
          <el-select
            v-model="form.bedNumber"
            placeholder="请选择床位"
            clearable
            style="width: 100%;"
          >
            <el-option
              v-for="b in availableBeds"
              :key="b.id"
              :label="b.bedNumber + ' (' + (b.bedType === 'window' ? '靠窗' : '靠走廊') + ')'"
              :value="b.bedNumber"
              :disabled="b.isOccupied === 1"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="家庭地址" prop="address">
          <el-input v-model="form.address" type="textarea" rows="2" placeholder="请输入家庭地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 调宿对话框 -->
    <el-dialog
      v-model="relocateVisible"
      title="调宿"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="当前学生">
          <el-tag type="info">{{ relocateStudent?.name }} ({{ relocateStudent?.studentNo }})</el-tag>
        </el-form-item>
        <el-form-item label="当前房间">
          <el-tag type="info">
            {{ getBuildingName(relocateStudent?.roomId) }} - {{ getRoomNumber(relocateStudent?.roomId) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="目标楼栋" required>
          <el-select
            v-model="relocateForm.buildingId"
            placeholder="请选择楼栋"
            style="width: 100%;"
            @change="handleRoomBuildingChange"
          >
            <el-option
              v-for="b in buildings"
              :key="b.id"
              :label="b.name"
              :value="b.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="目标房间" required>
          <el-select
            v-model="relocateForm.roomId"
            placeholder="请选择房间"
            style="width: 100%;"
            :disabled="!relocateForm.buildingId"
            @change="handleRoomChange"
          >
            <el-option
              v-for="r in targetRooms"
              :key="r.id"
              :label="r.roomNumber + ' (' + r.currentCount + '/' + r.capacity + ')'"
              :value="r.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="目标床位" required>
          <el-select
            v-model="relocateForm.bedId"
            placeholder="请选择床位"
            style="width: 100%;"
            :disabled="!relocateForm.roomId"
          >
            <el-option
              v-for="b in targetBeds"
              :key="b.id"
              :label="b.bedNumber"
              :value="b.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="relocateVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRelocateSubmit" :loading="relocateLoading">确定调宿</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getStudents, createStudent, updateStudent, checkOutStudent, deleteStudent, relocateStudent as relocateStudentAPI } from '@/api/student'
import { getRooms, getRoomsByBuilding } from '@/api/room'
import { buildingAPI } from '@/api/building'
import { getAvailableBeds } from '@/api/bed'
import { relocationAPI } from '@/api/relocation'

const loading = ref(false)
const students = ref([])
const buildings = ref([])
const rooms = ref([])
const roomOptions = ref([])
const searchName = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  studentNo: '',
  name: '',
  gender: '男',
  phone: '',
  department: '',
  className: '',
  roomId: null,
  bedNumber: null,
  idCard: '',
  address: ''
})

const availableBeds = ref([])
const selectedRoomId = computed(() => {
  if (Array.isArray(form.roomId)) {
    return form.roomId[form.roomId.length - 1]
  }
  return form.roomId
})

const onRoomChange = async (val) => {
  form.bedNumber = null
  availableBeds.value = []
  if (!val) return
  const roomId = Array.isArray(val) ? val[val.length - 1] : val
  if (!roomId) return
  try {
    const res = await getAvailableBeds(roomId)
    availableBeds.value = res.data || []
  } catch (error) {
    console.error('加载床位列表失败')
  }
}

const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

// 调宿相关状态
const relocateVisible = ref(false)
const relocateStudent = ref(null)
const relocateLoading = ref(false)
const relocateForm = reactive({
  buildingId: null,
  roomId: null,
  bedId: null,
  _appId: null
})
const targetRooms = ref([])
const targetBeds = ref([])

// 监听楼栋选择变化，加载对应房间
const loadTargetRooms = async (buildingId) => {
  if (!buildingId) {
    targetRooms.value = []
    return
  }
  try {
    // 只加载有空床位的房间（currentCount < capacity）
    const res = await getRoomsByBuilding(buildingId)
    const allRooms = res.data || []
    targetRooms.value = allRooms.filter(r => r.currentCount < r.capacity && r.isActive === 1)
  } catch (error) {
    console.error('加载房间列表失败')
    targetRooms.value = []
  }
}

// 监听房间选择变化，加载对应可用床位
const loadTargetBeds = async (roomId) => {
  if (!roomId) {
    targetBeds.value = []
    return
  }
  try {
    const res = await getAvailableBeds(roomId)
    targetBeds.value = res.data || []
  } catch (error) {
    console.error('加载床位列表失败')
    targetBeds.value = []
  }
}

const handleRelocate = (row) => {
  relocateStudent.value = row
  relocateForm.buildingId = null
  relocateForm.roomId = null
  relocateForm.bedId = null
  relocateForm._appId = null
  targetRooms.value = []
  targetBeds.value = []
  relocateVisible.value = true
}

const handleRoomBuildingChange = (buildingId) => {
  relocateForm.roomId = null
  relocateForm.bedId = null
  targetBeds.value = []
  loadTargetRooms(buildingId)
}

const handleRoomChange = (roomId) => {
  relocateForm.bedId = null
  loadTargetBeds(roomId)
}

const handleRelocateSubmit = async () => {
  if (!relocateForm.roomId || !relocateForm.bedId) {
    ElMessage.warning('请选择目标房间和床位')
    return
  }
  relocateLoading.value = true
  try {
    if (relocateForm._appId) {
      await relocationAPI.execute(relocateForm._appId, {
        roomId: relocateForm.roomId,
        bedId: relocateForm.bedId
      })
      ElMessage.success('调换执行成功')
      relocateForm._appId = null
      loadRelocations()
    } else {
      await relocateStudentAPI(relocateStudent.value.id, {
        roomId: relocateForm.roomId,
        bedId: relocateForm.bedId
      })
      ElMessage.success('调宿成功')
    }
    relocateVisible.value = false
    loadStudents()
    loadRooms()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    relocateLoading.value = false
  }
}

// 调换申请相关
const activeTab = ref('students')
const relocationApps = ref([])
const relocationLoading = ref(false)
const relocationStatusFilter = ref('')

const relocationStatusTag = (s) => {
  return { pending: 'warning', approved: 'success', rejected: 'danger', executed: 'info' }[s] || 'info'
}
const relocationStatusLabel = (s) => {
  return { pending: '待审核', approved: '已通过', rejected: '已拒绝', executed: '已执行' }[s] || s
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

const loadRelocations = async () => {
  relocationLoading.value = true
  try {
    const params = relocationStatusFilter.value ? { status: relocationStatusFilter.value } : {}
    const res = await relocationAPI.listApplications(params)
    relocationApps.value = res.data || []
  } catch (error) {
    console.error('加载调换申请失败：', error)
    relocationApps.value = []
  } finally {
    relocationLoading.value = false
  }
}

const handleApprove = async (row) => {
  try {
    const { value: comment } = await ElMessageBox.prompt('请输入审批意见（可选）', '审批通过', {
      confirmButtonText: '通过',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '审批意见...'
    })
    await relocationAPI.approve(row.id, comment || '')
    ElMessage.success('已审批通过')
    loadRelocations()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const handleReject = async (row) => {
  try {
    const { value: comment } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝申请', {
      confirmButtonText: '拒绝',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '拒绝原因...'
    })
    if (!comment || !comment.trim()) {
      ElMessage.warning('请填写拒绝原因')
      return
    }
    await relocationAPI.reject(row.id, comment)
    ElMessage.success('已拒绝')
    loadRelocations()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const handleExecuteRelocation = (row) => {
  // 复用现有的调宿对话框，预填学生信息
  relocateStudent.value = {
    id: row.studentId,
    name: row.studentName,
    studentNo: row.studentNo,
    roomId: row.currentRoomId
  }
  relocateForm.buildingId = null
  relocateForm.roomId = null
  relocateForm.bedId = null
  targetRooms.value = []
  targetBeds.value = []
  // 存储当前申请ID，用于执行时调用
  relocateForm._appId = row.id
  relocateVisible.value = true
}

const isAdmin= computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.role?.toUpperCase() === 'ADMIN'
})

const getBuildingName = (roomId) => {
  const room = rooms.value.find(r => r.id === roomId)
  if (!room) return '-'
  const building = buildings.value.find(b => b.id === room.buildingId)
  return building ? building.name : '-'
}

const getRoomNumber = (roomId) => {
  const room = rooms.value.find(r => r.id === roomId)
  return room ? room.roomNumber : '-'
}

const loadBuildings = async () => {
  try {
    const res = await buildingAPI.list()
    buildings.value = res.data
  } catch (error) {
    console.error('加载楼栋列表失败')
  }
}

const loadRooms = async () => {
  try {
    const res = await getRooms({ pageSize: 1000 })
    rooms.value = res.data.content || res.data
    
    // 构建级联选择器的树形数据
    const buildingMap = {}
    buildings.value.forEach(b => {
      buildingMap[b.id] = {
        id: b.id,
        name: b.name,
        children: []
      }
    })
    
    rooms.value.forEach(r => {
      if (buildingMap[r.buildingId]) {
        // 只有未满的房间才可选
        if (r.currentCount < r.capacity && r.status === 1) {
          buildingMap[r.buildingId].children.push({
            id: r.id,
            name: r.roomNumber
          })
        }
      }
    })
    
    roomOptions.value = Object.values(buildingMap)
  } catch (error) {
    console.error('加载房间列表失败')
  }
}

const loadStudents = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      page: currentPage.value,
      size: pageSize.value
    }
    if (searchName.value.trim()) {
      params.name = searchName.value.trim()
    }
    console.log('发送的学生分页参数：', params)
    const res = await getStudents(params)
    console.log('后端返回学生结果：', res)
    const data = res.data || res
    students.value = data.list || data.content || data.rows || data
    total.value = data.total || data.totalElements || data.count || 0
  } catch (error) {
    ElMessage.error('加载学生列表失败')
    console.error('加载学生列表错误：', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  if (!searchName.value.trim()) {
    currentPage.value = 1
    loadStudents()
    return
  }
  // 搜索时重置到第 1 页，loadStudents 会带上 name 参数
  currentPage.value = 1
  loadStudents()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    studentNo: '',
    name: '',
    gender: '男',
    phone: '',
    department: '',
    className: '',
    roomId: null,
    bedNumber: null,
    idCard: '',
    address: ''
  })
  availableBeds.value = []
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: null, studentNo: '', name: '', gender: '男',
    phone: '', department: '', className: '',
    roomId: null, bedNumber: null, idCard: '', address: ''
  })
  availableBeds.value = []
  Object.assign(form, row)
  // 编辑时加载该房间的床位列表
  if (row.roomId) {
    onRoomChange(row.roomId)
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    const data = { ...form }
    // 处理级联选择器的roomId（可能是数组）
    if (Array.isArray(data.roomId)) {
      data.roomId = data.roomId[data.roomId.length - 1]
    }
    // 没选床位时不发送 bedNumber
    if (!data.bedNumber) {
      delete data.bedNumber
    }
    
    if (isEdit.value) {
      await updateStudent(form.id, data)
      ElMessage.success('更新成功')
    } else {
      await createStudent(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadStudents()
    loadRooms() // 刷新房间状态
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleCheckOut = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要为学生 ${row.name} 办理退宿吗？`, '退宿确认', {
      type: 'warning'
    })
    await checkOutStudent(row.id)
    ElMessage.success('退宿成功')
    loadStudents()
    loadRooms()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '退宿失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除学生 ${row.name} 吗？`, '删除确认', {
      type: 'warning'
    })
    await deleteStudent(row.id)
    ElMessage.success('删除成功')
    loadStudents()
    loadRooms()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 使用 watch 响应 v-model 的分页变化
watch(currentPage, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    console.log('学生页码变化：', oldVal, '->', newVal)
    loadStudents()
  }
})

watch(pageSize, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    console.log('学生每页条数变化：', oldVal, '->', newVal)
    currentPage.value = 1
    loadStudents()
  }
})

watch(activeTab, (tab) => {
  if (tab === 'relocations') {
    loadRelocations()
  }
})

onMounted(async () => {
  await loadBuildings()
  await loadRooms()
  loadStudents()
})
</script>

<style scoped>
.student-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}
</style>