import { computed } from 'vue'
import { useRouter } from 'vue-router'

/**
 * 认证状态管理 composable
 * 集中管理 localStorage 中的 token 和 user 信息
 *
 * @returns {Object} { token, user, userRole, isAdmin, isStudent, isLoggedIn, logout }
 */
export function useAuth() {
  const router = useRouter()

  const token = computed(() => localStorage.getItem('token') || '')

  const user = computed(() => {
    try {
      return JSON.parse(localStorage.getItem('user') || '{}')
    } catch {
      return {}
    }
  })

  const userRole = computed(() => (user.value.role || 'student').toLowerCase())
  const isAdmin = computed(() => userRole.value === 'admin')
  const isManager = computed(() => userRole.value === 'manager')
  const isStudent = computed(() => userRole.value === 'student')
  const isLoggedIn = computed(() => !!token.value)

  /** 退出登录 */
  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
  }

  return { token, user, userRole, isAdmin, isManager, isStudent, isLoggedIn, logout }
}
