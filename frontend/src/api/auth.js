import api from '../utils/api'

export const authAPI = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  getUserInfo: () => api.get('/auth/me'),
  updateProfile: (data) => api.put('/auth/profile', data),
  changePassword: (oldPasswordOrData, newPassword) => {
    const payload = oldPasswordOrData && typeof oldPasswordOrData === 'object'
      ? oldPasswordOrData
      : { oldPassword: oldPasswordOrData, newPassword }
    return api.put('/auth/password', payload)
  }
}

export default api
