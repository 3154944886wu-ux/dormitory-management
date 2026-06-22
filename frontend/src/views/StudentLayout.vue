<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="sidebar-logo">
        <el-icon :size="28"><School /></el-icon>
        <span>宿舍管理系统</span>
      </div>
      <div class="sidebar-role-badge student">学生</div>

      <el-menu
        :default-active="activeMenu"
        router
        background-color="#1D2438"
        text-color="#bfcbd9"
        active-text-color="#67C23A"
      >
        <el-menu-item index="/student/home"><el-icon><HomeFilled /></el-icon><span>我的首页</span></el-menu-item>
        <el-menu-item index="/student/check-in"><el-icon><Clock /></el-icon><span>归寝打卡</span></el-menu-item>
        <el-menu-item index="/student/my-records"><el-icon><List /></el-icon><span>我的打卡记录</span></el-menu-item>
        <el-menu-item index="/student/leave-request"><el-icon><Document /></el-icon><span>请假申请</span></el-menu-item>
        <el-menu-item index="/student/my-leaves"><el-icon><Tickets /></el-icon><span>我的请假记录</span></el-menu-item>
        <el-menu-item index="/student/my-room"><el-icon><House /></el-icon><span>我的宿舍</span></el-menu-item>
        <el-menu-item index="/student/repairs"><el-icon><Tools /></el-icon><span>报修申请</span></el-menu-item>
        <el-menu-item index="/student/announcements"><el-icon><Bell /></el-icon><span>公告通知</span></el-menu-item>
        <el-menu-item index="/student/fees"><el-icon><Coin /></el-icon><span>水电费用</span></el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <div class="main-container">
      <header class="top-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/student/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 通知铃铛 -->
          <el-popover placement="bottom" :width="320" trigger="click" @show="markAllRead">
            <template #reference>
              <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
                <el-button :icon="Bell" circle />
              </el-badge>
            </template>
            <div class="notification-list">
              <div v-if="notifications.length === 0" class="notification-empty">暂无通知</div>
              <div v-for="n in notifications.slice(0, 10)" :key="n.id" class="notification-item">
                <div class="notify-type">
                  <el-tag size="small" :type="notifyTagType(n.type)">{{ notifyLabel(n.type) }}</el-tag>
                </div>
                <div class="notify-content">{{ n.content }}</div>
                <div class="notify-time">{{ formatTime(n.createTime) }}</div>
              </div>
            </div>
          </el-popover>

          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile"><el-icon><User /></el-icon>个人信息</el-dropdown-item>
                <el-dropdown-item command="password"><el-icon><Lock /></el-icon>修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="main-content">
        <router-view />
      </main>
    </div>

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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '@/api/auth'
import { dormSelectionAPI } from '@/api/dormSelection'
import { getRecordsByRoom } from '@/api/inspection'
import { studentAPI } from '@/api/student'
import { useAuth } from '@/composables/useAuth'
import { useConfirm } from '@/composables/useConfirm'
import {
  School, HomeFilled, Clock, List, Document, Tickets, House, Tools, Bell, Coin,
  UserFilled, ArrowDown, User, Lock, SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const { logout: doLogout } = useAuth()
const { confirm: confirmDialog } = useConfirm()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '首页')
const username = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.name || user.username || '同学'
})

const notifications = ref([])
const unreadCount = ref(0)

const notifyTagType = (type) => {
  const t = { auto_confirm: 'success', relocation_approved: 'warning', relocation_rejected: 'danger', relocation_executed: 'primary', inspection_pending: 'danger' }
  return t[type] || 'info'
}
const notifyLabel = (type) => {
  const l = { auto_confirm: '自动确认', relocation_approved: '申请通过', relocation_rejected: '申请驳回', relocation_executed: '调换完成', inspection_pending: '待整改' }
  return l[type] || type
}
const formatTime = (t) => {
  if (!t) return '-'
  return new Date(t).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const loadNotifications = async () => {
  try {
    const res = await dormSelectionAPI.myNotifications()
    notifications.value = res.data || []
    unreadCount.value = notifications.value.length
  } catch (e) { /* ignore */ }
  // 加载检查整改提醒
  try {
    const roomRes = await studentAPI.getMyRoom()
    if (roomRes.data?.roomId) {
      const inspectionRes = await getRecordsByRoom(roomRes.data.roomId)
      const records = inspectionRes.data || []
      records.filter(r => r.rectificationStatus === 'PENDING').forEach(r => {
        notifications.value.unshift({
          id: 'inspect_' + r.id, type: 'inspection_pending',
          content: `房间 ${r.roomNumber} 需要整改：${r.remark || '检查不合格'}（评分 ${r.overallScore}）`,
          createTime: r.inspectionTime
        })
      })
      unreadCount.value += records.filter(r => r.rectificationStatus === 'PENDING').length
    }
  } catch (e) { /* ignore */ }
}
const markAllRead = () => { unreadCount.value = 0 }

const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        callback(value !== passwordForm.newPassword ? new Error('两次输入的密码不一致') : undefined)
      }, trigger: 'blur' }
  ]
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    const ok = await confirmDialog('退出登录', '确定要退出登录吗？')
    if (ok) { doLogout(); ElMessage.success('已退出登录') }
  } else if (command === 'profile') {
    router.push('/student/profile')
  } else if (command === 'password') {
    passwordDialogVisible.value = true
  }
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await authAPI.changePassword(passwordForm.oldPassword, passwordForm.newPassword)
        ElMessage.success('密码修改成功')
        passwordDialogVisible.value = false
        Object.assign(passwordForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '密码修改失败')
      }
    }
  })
}

onMounted(() => { loadNotifications() })
</script>

<style scoped>
.header-left  { display: flex; align-items: center; }
.header-right { display: flex; align-items: center; }
</style>
