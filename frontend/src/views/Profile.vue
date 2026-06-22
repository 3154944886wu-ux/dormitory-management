<template>
  <div class="profile">
    <div class="page-header">
      <h2>个人信息</h2>
    </div>

    <el-card>
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        
        <el-form-item label="姓名" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        
        <el-divider>修改密码</el-divider>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="form.newPassword" 
            type="password" 
            placeholder="不修改密码请留空"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存</el-button>
          <el-button @click="loadProfile">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户管理（管理员可见） -->
    <div v-if="(user?.role || '').toUpperCase() === 'ADMIN'" class="user-management">
      <div class="section-header">
        <h3>用户管理</h3>
        <el-button type="primary" size="small" @click="showAddDialog">
          添加用户
        </el-button>
      </div>

      <el-card>
        <el-table :data="users" stripe>
          <el-table-column prop="username" label="用户名" width="150" />
          <el-table-column prop="nickname" label="姓名" width="120" />
          <el-table-column prop="phone" label="电话" width="130" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column label="角色" width="100">
            <template #default="{ row }">
              <el-tag :type="roleTag(row.role)">
                {{ roleText(row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="showEditDialog(row)">
                编辑
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="handleDeleteUser(row)"
                :disabled="row.id === user.id"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 用户编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="500px"
    >
      <el-form :model="userForm" label-width="100px" :rules="userRules" ref="userFormRef">
        <el-form-item label="用户名" prop="username" v-if="!isEdit">
          <el-input v-model="userForm.username" />
        </el-form-item>
        <el-form-item label="姓名" prop="nickname">
          <el-input v-model="userForm.nickname" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="userForm.password" 
            type="password"
            :placeholder="isEdit ? '不修改请留空' : '请输入密码'"
            show-password
          />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" style="width: 100%">
            <el-option label="学生" value="STUDENT" />
            <el-option label="教师" value="MANAGER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveUser">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'

const user = ref(null)
const users = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const userFormRef = ref(null)

const form = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  newPassword: '',
  confirmPassword: ''
})

const userForm = reactive({
  id: null,
  username: '',
  nickname: '',
  password: '',
  phone: '',
  email: '',
  role: 'STUDENT',
  status: 1
})

const validatePass = (rule, value, callback) => {
  if (form.newPassword && value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  nickname: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  newPassword: [
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [{ validator: validatePass, trigger: 'blur' }]
}

const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const roleText = (role) => {
  const map = { ADMIN: '管理员', MANAGER: '教师', STUDENT: '学生' }
  return map[(role || '').toUpperCase()] || role || '-'
}

const roleTag = (role) => {
  const map = { ADMIN: 'danger', MANAGER: 'warning', STUDENT: 'success' }
  return map[(role || '').toUpperCase()] || 'info'
}

// 加载用户信息
const loadProfile = async () => {
  try {
    const res = await api.get('/users/me')
    Object.assign(form, res.data || {})
    form.newPassword = ''
    form.confirmPassword = ''
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

// 加载用户列表
const loadUsers = async () => {
  if ((user.value?.role || '').toUpperCase() !== 'ADMIN') return
  
  try {
    const res = await api.get('/users')
    users.value = res.data || []
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  }
}

// 保存个人信息
const handleSave = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      const data = {
        nickname: form.nickname,
        phone: form.phone,
        email: form.email
      }
      
      if (form.newPassword) {
        data.password = form.newPassword
      }
      
      await api.put('/users/me', data)
      ElMessage.success('保存成功')
      
      // 更新本地存储
      const storedUser = JSON.parse(localStorage.getItem('user'))
      storedUser.nickname = form.nickname
      localStorage.setItem('user', JSON.stringify(storedUser))
      
      form.newPassword = ''
      form.confirmPassword = ''
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '保存失败')
    }
  })
}

// 显示添加用户对话框
const showAddDialog = () => {
  isEdit.value = false
  Object.assign(userForm, {
    id: null,
    username: '',
    nickname: '',
    password: '',
    phone: '',
    email: '',
    role: 'STUDENT',
    status: 1
  })
  dialogVisible.value = true
}

// 显示编辑用户对话框
const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(userForm, row)
  userForm.password = ''
  dialogVisible.value = true
}

// 保存用户
const handleSaveUser = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      if (isEdit.value) {
        await api.put(`/users/${userForm.id}`, userForm)
        ElMessage.success('更新成功')
      } else {
        await api.post('/users', userForm)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadUsers()
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  })
}

// 删除用户
const handleDeleteUser = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该用户？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await api.delete(`/users/${row.id}`)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  const userData = localStorage.getItem('user')
  if (userData) {
    user.value = JSON.parse(userData)
  }
  loadProfile()
  loadUsers()
})
</script>

<style scoped>
.profile {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.user-management {
  margin-top: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  margin: 0;
}
</style>