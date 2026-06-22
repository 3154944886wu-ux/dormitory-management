import api from '../utils/api'

export const buildingAPI = {
  // 获取楼栋列表
  list: () => api.get('/buildings'),
  
  // 获取单个楼栋
  getById: (id) => api.get(`/buildings/${id}`),
  
  // 创建楼栋
  create: (data) => api.post('/buildings', data),
  
  // 更新楼栋
  update: (id, data) => api.put(`/buildings/${id}`, data),
  
  // 删除楼栋
  delete: (id) => api.delete(`/buildings/${id}`),
  
  // 更新状态
  updateStatus: (id, status) => api.put(`/buildings/${id}/status`, { status })
}

export default buildingAPI