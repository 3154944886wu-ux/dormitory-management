import axios from 'axios'
import { isTokenExpired } from './jwt'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

let redirectingToLogin = false

const redirectToLogin = () => {
  if (redirectingToLogin) {
    return
  }
  redirectingToLogin = true
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  window.location.href = '/login'
}

// 请求拦截器 - 添加Token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      if (isTokenExpired(token)) {
        redirectToLogin()
        return Promise.reject(new Error('登录已过期'))
      }
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器 - 统一错误处理
api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code && res.code !== 200 && res.code !== 201) {
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    const status = error.response?.status
    // 仅 401（未认证）时跳转登录；403 可能是业务权限问题，仅 console 警告
    if (status === 401) {
      const isLoginRequest = error.config?.url?.includes('/auth/login')
      const isLoginPage = window.location.href.includes('/login')
      if (!isLoginRequest && !isLoginPage) {
        redirectToLogin()
      }
    }
    if (status === 403) {
      console.warn('[API 403]', error.config?.url, error.response?.data)
      const forbiddenMessage = error.response?.data?.message
        || '权限不足，请使用管理员或宿管/辅导员账号，或重新登录后再试'
      return Promise.reject(new Error(forbiddenMessage))
    }
    const message = error.response?.data?.message
    if (message) {
      return Promise.reject(new Error(message))
    }
    return Promise.reject(error)
  }
)

export default api
