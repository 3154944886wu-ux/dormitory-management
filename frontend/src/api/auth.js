import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器 - 添加Token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
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
    // 接受 2xx 范围的状态码 (200-299)
    if (res.code < 200 || res.code >= 300) {
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    // 登录接口的 401 不跳转，让页面显示错误信息
    if (error.response?.status === 401 && !error.config.url.includes('/auth/login')) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    const message = error.response?.data?.message
    if (message) {
      return Promise.reject(new Error(message))
    }
    return Promise.reject(error)
  }
)

// Auth API
export const authAPI = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  getUserInfo: () => api.get('/auth/me'),
  updateProfile: (data) => api.put('/auth/profile', data),
  changePassword: (data) => api.put('/auth/password', data)
}

export default api