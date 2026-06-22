<template>
  <div class="announcement-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告通知管理</span>
          <el-button type="primary" @click="handleAdd" v-if="isAdmin">
            <el-icon><Plus /></el-icon>
            发布公告
          </el-button>
        </div>
      </template>
      
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable @change="loadAnnouncements" style="width: 120px; margin-right: 10px;">
          <el-option label="草稿" :value="0" />
          <el-option label="已发布" :value="1" />
          <el-option label="已下线" :value="2" />
        </el-select>
        <el-select v-model="filterType" placeholder="类型筛选" clearable @change="loadAnnouncements" style="width: 120px;">
          <el-option label="普通公告" :value="0" />
          <el-option label="重要通知" :value="1" />
          <el-option label="紧急通知" :value="2" />
        </el-select>
      </div>
      
      <el-table :data="announcements" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <span>{{ row.title }}</span>
            <el-tag v-if="row.isTop === 1" type="danger" size="small" style="margin-left: 8px;">置顶</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="160">
          <template #default="{ row }">
            {{ row.publishTime ? formatDate(row.publishTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="70" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button v-if="isAdmin && row.status === 0" type="success" link @click="handlePublish(row)">发布</el-button>
            <el-button v-if="isAdmin && row.status === 1" type="warning" link @click="handleOffline(row)">下线</el-button>
            <el-button v-if="isAdmin && row.status === 1" type="info" link @click="handleTop(row)">
              {{ row.isTop === 1 ? '取消置顶' : '置顶' }}
            </el-button>
            <el-button v-if="isAdmin" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="isAdmin" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑公告' : '发布公告'"
      width="700px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="0">普通公告</el-radio>
            <el-radio :value="1">重要通知</el-radio>
            <el-radio :value="2">紧急通知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">保存为草稿</el-radio>
            <el-radio :value="1">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="公告详情"
      width="600px"
    >
      <div class="announcement-detail" v-if="currentAnnouncement">
        <h2>{{ currentAnnouncement.title }}</h2>
        <div class="meta">
          <el-tag :type="getTypeTag(currentAnnouncement.type)">
            {{ getTypeLabel(currentAnnouncement.type) }}
          </el-tag>
          <span class="publisher">{{ currentAnnouncement.publisherName }}</span>
          <span class="time">{{ formatDate(currentAnnouncement.publishTime) }}</span>
          <span class="views">浏览: {{ currentAnnouncement.viewCount }}</span>
        </div>
        <el-divider />
        <div class="content">{{ currentAnnouncement.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import announcementAPI from '@/api/announcement'

const loading = ref(false)
const announcements = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const currentAnnouncement = ref(null)
const filterStatus = ref(null)
const filterType = ref(null)

const form = reactive({
  id: null,
  title: '',
  content: '',
  type: 0,
  status: 0,
  isTop: 0
})

const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }]
}

const isAdmin = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.role?.toUpperCase() === 'ADMIN'
})

const getTypeTag = (type) => {
  const tags = { 0: 'info', 1: 'warning', 2: 'danger' }
  return tags[type] || 'info'
}

const getTypeLabel = (type) => {
  const labels = { 0: '普通公告', 1: '重要通知', 2: '紧急通知' }
  return labels[type] || '未知'
}

const getStatusTag = (status) => {
  const tags = { 0: 'info', 1: 'success', 2: 'warning' }
  return tags[status] || 'info'
}

const getStatusLabel = (status) => {
  const labels = { 0: '草稿', 1: '已发布', 2: '已下线' }
  return labels[status] || '未知'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const loadAnnouncements = async () => {
  loading.value = true
  try {
    const res = await announcementAPI.getAll({ status: filterStatus.value, type: filterType.value })
    announcements.value = res.data
  } catch (error) {
    ElMessage.error('加载公告列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    title: '',
    content: '',
    type: 0,
    status: 0,
    isTop: 0
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    title: row.title,
    content: row.content,
    type: row.type,
    status: row.status,
    isTop: row.isTop
  })
  dialogVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await announcementAPI.getById(row.id)
    currentAnnouncement.value = res.data
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载公告详情失败')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (isEdit.value) {
      await announcementAPI.update(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await announcementAPI.create(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadAnnouncements()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除公告"${row.title}"吗？`, '提示', {
      type: 'warning'
    })
    await announcementAPI.delete(row.id)
    ElMessage.success('删除成功')
    loadAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handlePublish = async (row) => {
  try {
    await announcementAPI.publish(row.id)
    ElMessage.success('发布成功')
    loadAnnouncements()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

const handleOffline = async (row) => {
  try {
    await ElMessageBox.confirm('确定要下线该公告吗？', '提示', {
      type: 'warning'
    })
    await announcementAPI.offline(row.id)
    ElMessage.success('已下线')
    loadAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleTop = async (row) => {
  try {
    await announcementAPI.toggleTop(row.id)
    ElMessage.success(row.isTop === 1 ? '已取消置顶' : '已置顶')
    loadAnnouncements()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadAnnouncements()
})
</script>

<style scoped>
.announcement-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-bar {
  margin-bottom: 16px;
}

.announcement-detail h2 {
  margin: 0 0 16px 0;
  font-size: 20px;
}

.announcement-detail .meta {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #909399;
  font-size: 14px;
}

.announcement-detail .content {
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>