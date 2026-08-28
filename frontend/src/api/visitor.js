import api from '../utils/api'

export const visitorAPI = {
  // 获取访客列表
  getAll(params = {}) {
    return api.get('/visitors', { params })
  },
  
  // 获取访客详情
  getById(id) {
    return api.get(`/visitors/${id}`)
  },
  
  // 创建访客记录
  create(data) {
    return api.post('/visitors', data)
  },
  
  // 更新访客记录
  update(id, data) {
    return api.put(`/visitors/${id}`, data)
  },
  
  // 删除访客记录
  delete(id) {
    return api.delete(`/visitors/${id}`)
  },
  
  // 访客离开
  leave(id) {
    return api.post(`/visitors/${id}/leave`)
  }
}

export default visitorAPI