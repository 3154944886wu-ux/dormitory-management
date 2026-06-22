<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <el-icon :size="28"><School /></el-icon>
        <span>宿舍管理系统</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>首页仪表盘</span>
        </el-menu-item>
        
        <el-sub-menu index="building">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>楼栋房间</span>
          </template>
          <el-menu-item index="/buildings">
            <el-icon><OfficeBuilding /></el-icon>
            <span>楼栋管理</span>
          </el-menu-item>
          <el-menu-item index="/rooms">
            <el-icon><House /></el-icon>
            <span>房间管理</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/students">
          <el-icon><User /></el-icon>
          <span>学生管理</span>
        </el-menu-item>
        
        <el-menu-item index="/visitors">
          <el-icon><Avatar /></el-icon>
          <span>访客登记</span>
        </el-menu-item>
        
        <el-menu-item index="/repairs">
          <el-icon><Tools /></el-icon>
          <span>报修管理</span>
        </el-menu-item>
        
        <el-menu-item index="/utility-fees">
          <el-icon><Coin /></el-icon>
          <span>水电费管理</span>
        </el-menu-item>
        
        <el-menu-item index="/announcements">
          <el-icon><Bell /></el-icon>
          <span>公告通知</span>
        </el-menu-item>
        
        <el-sub-menu index="check">
          <template #title>
            <el-icon><Clock /></el-icon>
            <span>归寝检查</span>
          </template>
          <el-menu-item index="/check-in">
            <el-icon><Clock /></el-icon>
            <span>打卡签到</span>
          </el-menu-item>
          <el-menu-item index="/leave-request">
            <el-icon><Document /></el-icon>
            <span>请假申请</span>
          </el-menu-item>
          <el-menu-item index="/leave-approval" v-if="isAdmin">
            <el-icon><Stamp /></el-icon>
            <span>请假审批</span>
          </el-menu-item>
          <el-menu-item index="/check-rules" v-if="isAdmin">
            <el-icon><Setting /></el-icon>
            <span>打卡规则</span>
          </el-menu-item>
          <el-menu-item index="/check-exceptions" v-if="isAdmin">
            <el-icon><Warning /></el-icon>
            <span>异常记录</span>
          </el-menu-item>
          <el-menu-item index="/check-in-records" v-if="isAdmin">
            <el-icon><List /></el-icon>
            <span>打卡记录</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>
                  修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
    
    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { authAPI } from '@/api/auth'
import {
  School,
  DataLine,
  OfficeBuilding,
  House,
  User,
  Avatar,
  Tools,
  Coin,
  Bell,
  UserFilled,
  ArrowDown,
  Lock,
  SwitchButton,
  Clock,
  Document,
  Stamp,
  Setting,
  Warning,
  List
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '首页')
const username = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.name || user.username || '用户'
})

const isAdmin = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.role === 'admin' || user.role === 'dorm_manager'
})

const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleCommand = (command) => {
  console.log('handleCommand called:', command)
  switch (command) {
    case 'profile':
      console.log('Navigating to /profile')
      router.push('/profile')
      break
    case 'password':
      console.log('Opening password dialog')
      passwordDialogVisible.value = true
      break
    case 'logout':
      console.log('Logout triggered')
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        router.push('/login')
        ElMessage.success('已退出登录')
      }).catch(() => {})
      break
  }
}

const handleChangePassword = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    await authAPI.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    passwordDialogVisible.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '密码修改失败')
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  background-color: #304156;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #3a4a5e;
}

.el-menu {
  border-right: none;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.username {
  font-size: 14px;
}

.main {
  background: #f5f7fa;
  overflow-y: auto;
}

:deep(.el-menu-item.is-active) {
  background-color: #263445 !important;
}

:deep(.el-sub-menu .el-menu-item) {
  padding-left: 50px !important;
}
</style>