<template>
  <div class="auth-page">
    <div class="auth-bg-svg auth-bg-svg--left" aria-hidden="true">
      <svg viewBox="0 0 500 700" fill="none" xmlns="http://www.w3.org/2000/svg">
        <rect x="60" y="280" width="70" height="180" rx="3" stroke="#4A90E2" stroke-width="1.5" opacity="0.08"/>
        <rect x="65" y="290" width="14" height="14" rx="1" stroke="#4A90E2" stroke-width="1" opacity="0.06"/>
        <rect x="85" y="290" width="14" height="14" rx="1" stroke="#4A90E2" stroke-width="1" opacity="0.06"/>
        <rect x="105" y="290" width="14" height="14" rx="1" stroke="#4A90E2" stroke-width="1" opacity="0.06"/>
        <polygon points="95,250 60,280 130,280" stroke="#4A90E2" stroke-width="1" opacity="0.06"/>
        <rect x="150" y="250" width="60" height="210" rx="3" stroke="#4A90E2" stroke-width="1.5" opacity="0.07"/>
        <polygon points="180,225 150,250 210,250" stroke="#4A90E2" stroke-width="1" opacity="0.05"/>
        <path d="M40,460 L40,420 Q48,400 56,420 Q62,400 70,420 L70,460 Z" stroke="#4A90E2" stroke-width="1" opacity="0.07"/>
        <path d="M210,460 L210,415 Q218,395 226,415 Q232,395 240,415 L240,460 Z" stroke="#4A90E2" stroke-width="1" opacity="0.06"/>
        <rect x="20" y="520" width="60" height="8" rx="1" stroke="#4A90E2" stroke-width="1" opacity="0.07"/>
        <rect x="16" y="510" width="68" height="8" rx="1" stroke="#4A90E2" stroke-width="1" opacity="0.07"/>
        <rect x="130" y="500" width="50" height="7" rx="1" stroke="#4A90E2" stroke-width="1" opacity="0.06"/>
        <path d="M0,700 Q80,640 160,660 Q240,620 320,650 Q400,630 500,660 L500,700 L0,700 Z" stroke="#4A90E2" stroke-width="0.8" opacity="0.04"/>
      </svg>
    </div>
    <div class="auth-bg-svg auth-bg-svg--right" aria-hidden="true">
      <svg viewBox="0 0 500 700" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M120,180 L180,180 L180,220 L240,220" stroke="#4A90E2" stroke-width="1.2" opacity="0.08"/>
        <path d="M300,300 L360,300 L360,260 L420,260" stroke="#4A90E2" stroke-width="1.2" opacity="0.07"/>
        <path d="M200,400 L260,400 L260,440 L320,440" stroke="#F5A623" stroke-width="1" opacity="0.06"/>
        <circle cx="120" cy="180" r="3" fill="#4A90E2" opacity="0.08"/>
        <circle cx="240" cy="220" r="3" fill="#4A90E2" opacity="0.08"/>
        <circle cx="300" cy="300" r="3" fill="#4A90E2" opacity="0.08"/>
        <circle cx="200" cy="400" r="2.5" fill="#F5A623" opacity="0.07"/>
        <circle cx="320" cy="440" r="2.5" fill="#F5A623" opacity="0.07"/>
        <circle cx="60" cy="120" r="4" stroke="#4A90E2" stroke-width="0.8" opacity="0.06"/>
        <circle cx="440" cy="100" r="3" stroke="#4A90E2" stroke-width="0.8" opacity="0.05"/>
        <circle cx="80" cy="220" r="2" fill="#F5A623" opacity="0.05"/>
        <circle cx="460" cy="340" r="3.5" stroke="#4A90E2" stroke-width="0.8" opacity="0.05"/>
      </svg>
    </div>

    <div class="auth-card">
      <div class="auth-card-icon">
        <svg viewBox="0 0 72 72" width="60" height="60" fill="none">
          <ellipse cx="36" cy="54" rx="22" ry="5" fill="#e8f0f8"/>
          <rect x="18" y="28" width="10" height="22" rx="1.5" fill="#a8d5ba"/>
          <rect x="30" y="20" width="12" height="30" rx="1.5" fill="#6ba4e9"/>
          <rect x="44" y="26" width="10" height="24" rx="1.5" fill="#7bc4c4"/>
        </svg>
      </div>
      <h1 class="auth-card-title">智慧宿舍管理系统</h1>
      <p class="auth-card-subtitle">欢迎回来，请登录您的账号</p>

      <el-form :model="form" :rules="rules" ref="formRef" class="auth-form" @keyup.enter="handleLogin" :show-message="false">
        <el-form-item prop="username">
          <label class="auth-input-label">用户名</label>
          <el-input v-model="form.username" placeholder="请输入用户名或学号" size="large" class="auth-input">
            <template #prefix>
              <svg class="input-icon" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <label class="auth-input-label">密码</label>
          <el-input v-model="form.password" :type="showPassword ? 'text' : 'password'" placeholder="请输入密码" size="large" class="auth-input">
            <template #prefix>
              <svg class="input-icon" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
            </template>
            <template #suffix>
              <span class="pwd-toggle" @click="showPassword = !showPassword">
                <svg v-if="showPassword" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                <svg v-else viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
              </span>
            </template>
          </el-input>
        </el-form-item>

        <transition name="msg-fade">
          <div v-if="errorMsg" class="auth-error">
            <svg viewBox="0 0 20 20" width="16" height="16" fill="currentColor"><path d="M10 0C4.48 0 0 4.48 0 10s4.48 10 10 10 10-4.48 10-10S15.52 0 10 0zm1 15H9v-2h2v2zm0-4H9V5h2v6z"/></svg>
            <span>{{ errorMsg }}</span>
          </div>
        </transition>

        <el-form-item>
          <el-button size="large" class="auth-submit-btn" :loading="loading" @click="handleLogin"><span class="btn-text">登录</span></el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer">
        <span class="auth-footer-text">还没有账号？</span>
        <router-link to="/register" class="auth-footer-link">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '@/api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const showPassword = ref(false)
const errorMsg = ref('')
const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  errorMsg.value = ''
  try {
    await formRef.value.validate()
    loading.value = true
    const res = await authAPI.login(form)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    ElMessage.success('登录成功')
    const role = (res.data.user.role || 'student').toLowerCase()
    router.push(role === 'admin' ? '/admin/dashboard' : (role === 'manager' ? '/manager/exceptions' : '/student/home'))
  } catch (e) {
    errorMsg.value = e.message || '登录失败'
    ElMessage.error(e.message || '登录失败')
  } finally { loading.value = false }
}
</script>

<style scoped>
.auth-input { --el-input-height: 48px; }
.auth-input :deep(.el-input__wrapper) { border-radius: 8px !important; background: #fff !important; border: 1px solid #dcdfe6 !important; box-shadow: none !important; padding: 0 12px; transition: all .25s ease; }
.auth-input :deep(.el-input__wrapper:hover) { border-color: #a0cfff !important; }
.auth-input :deep(.el-input__wrapper.is-focus) { border-color: var(--color-primary) !important; box-shadow: 0 0 0 2px rgba(74,111,165,0.15) !important; }
.auth-input :deep(.el-input__inner) { font-size: 14px; color: #2c3e50; }
.auth-input :deep(.el-input__inner::placeholder) { color: #a8abb2; }
.input-icon { color: #a8abb2; margin-right: 4px; }
.pwd-toggle { display: flex; align-items: center; cursor: pointer; color: #a8abb2; transition: .25s; }
.pwd-toggle:hover { color: var(--color-primary); }
.btn-text { font-size: 16px; font-weight: 600; letter-spacing: 2px; }
.msg-fade-enter-active, .msg-fade-leave-active { transition: .3s; }
.msg-fade-enter-from, .msg-fade-leave-to { opacity: 0; transform: translateY(-6px); }
</style>
