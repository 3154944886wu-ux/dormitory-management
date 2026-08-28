<template>
  <div class="not-found">
    <el-result icon="warning" title="页面不存在" sub-title="你访问的地址无效，或该功能尚未开放。">
      <template #extra>
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="$router.back()">返回上一页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
const goHome = () => {
  let role = 'student'
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    role = (user.role || 'student').toLowerCase()
  } catch {
    role = 'student'
  }
  const token = localStorage.getItem('token')
  if (!token) {
    window.location.href = '/login'
    return
  }
  if (role === 'admin') {
    window.location.href = '/admin/dashboard'
  } else if (role === 'manager') {
    window.location.href = '/manager/exceptions'
  } else {
    window.location.href = '/student/home'
  }
}
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}
</style>
