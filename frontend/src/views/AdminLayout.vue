<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="sidebar-logo">
        <el-icon :size="28"><School /></el-icon>
        <span>宿舍管理系统</span>
      </div>
      <div class="sidebar-role-badge admin">管理员</div>

      <el-menu
        :default-active="activeMenu"
        router
        background-color="#1D2438"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>工作台</span>
        </el-menu-item>

        <el-sub-menu index="building">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>楼栋房间</span>
          </template>
          <el-menu-item index="/admin/buildings">
            <el-icon><OfficeBuilding /></el-icon>
            <span>楼栋管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/rooms">
            <el-icon><House /></el-icon>
            <span>房间管理</span>
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/admin/students"><el-icon><User /></el-icon><span>学生管理</span></el-menu-item>
        <el-menu-item index="/admin/visitors"><el-icon><Avatar /></el-icon><span>访客登记</span></el-menu-item>
        <el-menu-item index="/admin/repairs"><el-icon><Tools /></el-icon><span>报修管理</span></el-menu-item>
        <el-menu-item index="/admin/utility-fees"><el-icon><Coin /></el-icon><span>水电费管理</span></el-menu-item>

        <el-sub-menu index="check">
          <template #title><el-icon><Clock /></el-icon><span>归寝检查</span></template>
          <el-menu-item index="/admin/check-rules"><el-icon><Setting /></el-icon><span>打卡规则</span></el-menu-item>
          <el-menu-item index="/admin/check-records"><el-icon><List /></el-icon><span>打卡记录</span></el-menu-item>
          <el-menu-item index="/admin/check-exceptions"><el-icon><Warning /></el-icon><span>异常记录</span></el-menu-item>
          <el-menu-item index="/admin/check-statistics"><el-icon><TrendCharts /></el-icon><span>归寝统计</span></el-menu-item>
          <el-menu-item index="/admin/leave-approval"><el-icon><Stamp /></el-icon><span>请假审批</span></el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="inspection">
          <template #title><el-icon><Checked /></el-icon><span>安全卫生检查</span></template>
          <el-menu-item index="/admin/inspection/plans"><el-icon><Calendar /></el-icon><span>检查计划</span></el-menu-item>
          <el-menu-item index="/admin/inspection/execute"><el-icon><EditPen /></el-icon><span>执行检查</span></el-menu-item>
          <el-menu-item index="/admin/inspection/records"><el-icon><List /></el-icon><span>检查记录</span></el-menu-item>
          <el-menu-item index="/admin/inspection/items"><el-icon><Setting /></el-icon><span>检查项目</span></el-menu-item>
          <el-menu-item index="/admin/inspection/statistics"><el-icon><TrendCharts /></el-icon><span>统计分析</span></el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="dorm-selection">
          <template #title><el-icon><Monitor /></el-icon><span>智能选宿</span></template>
          <el-menu-item index="/admin/college-major"><el-icon><School /></el-icon><span>学院专业管理</span></el-menu-item>
          <el-menu-item index="/admin/questionnaires"><el-icon><Document /></el-icon><span>问卷管理</span></el-menu-item>
          <el-menu-item index="/admin/dorm-batches"><el-icon><Calendar /></el-icon><span>批次管理</span></el-menu-item>
          <el-menu-item index="/admin/batch-rooms"><el-icon><Connection /></el-icon><span>房源划拨</span></el-menu-item>
          <el-menu-item index="/admin/allocation-result"><el-icon><Grid /></el-icon><span>分配结果</span></el-menu-item>
          <el-menu-item index="/admin/allocation-report"><el-icon><Tickets /></el-icon><span>分配报表</span></el-menu-item>
          <el-menu-item index="/admin/allocation-statistics"><el-icon><TrendCharts /></el-icon><span>统计分析</span></el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/admin/announcements"><el-icon><Bell /></el-icon><span>公告管理</span></el-menu-item>
        <el-menu-item index="/admin/teachers"><el-icon><UserFilled /></el-icon><span>教师管理</span></el-menu-item>
        <el-menu-item index="/admin/manager-scopes"><el-icon><SetUp /></el-icon><span>管理范围</span></el-menu-item>
        <el-menu-item index="/admin/audit-logs"><el-icon><Tickets /></el-icon><span>审计日志</span></el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <div class="main-container">
      <header class="top-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
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
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '@/api/auth'
import { useAuth } from '@/composables/useAuth'
import { useConfirm } from '@/composables/useConfirm'
import {
  School, DataLine, OfficeBuilding, House, User, Avatar, Tools, Coin, Bell,
  UserFilled, ArrowDown, Lock, SwitchButton, Checked, Clock, Document,
  EditPen, Stamp, Setting, Warning, List, Monitor, Calendar, Connection,
  Grid, Tickets, TrendCharts, SetUp
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const { userRole, logout: doLogout } = useAuth()
const { confirm: confirmDialog } = useConfirm()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '首页')
const username = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.name || user.username || '管理员'
})

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
    router.push('/admin/profile')
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
</script>

<style scoped>
.header-left  { display: flex; align-items: center; }
.header-right { display: flex; align-items: center; }
</style>
