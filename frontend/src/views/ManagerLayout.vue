<template>
  <div class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="sidebar-logo">
        <el-icon :size="28"><School /></el-icon>
        <span>宿舍管理系统</span>
      </div>
      <div class="sidebar-role-badge manager">宿管/辅导员</div>

      <el-menu
        :default-active="activeMenu"
        router
        background-color="#1D2438"
        text-color="#bfcbd9"
        active-text-color="#E6A23C"
      >
        <el-menu-item index="/manager/exceptions"><el-icon><Warning /></el-icon><span>异常处理</span></el-menu-item>
        <el-menu-item index="/manager/check-records"><el-icon><List /></el-icon><span>归寝记录</span></el-menu-item>
        <el-menu-item index="/manager/statistics"><el-icon><TrendCharts /></el-icon><span>统计分析</span></el-menu-item>
        <el-menu-item index="/manager/profile"><el-icon><User /></el-icon><span>个人信息</span></el-menu-item>
      </el-menu>
    </el-aside>

    <div class="main-container">
      <header class="top-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/manager/exceptions' }">首页</el-breadcrumb-item>
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
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { School, Warning, List, TrendCharts, User, UserFilled, ArrowDown, SwitchButton } from '@element-plus/icons-vue'
import { useAuth } from '@/composables/useAuth'

const route = useRoute()
const router = useRouter()
const { logout } = useAuth()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '异常处理')
const username = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.nickname || user.username || 'manager'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    logout()
    ElMessage.success('已退出登录')
  } else if (command === 'profile') {
    router.push('/manager/profile')
  }
}
</script>
