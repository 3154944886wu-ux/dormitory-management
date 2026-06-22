import api from '../utils/api'

export const announcementAPI = {
  // 获取公告列表
  getAll(params = {}) {
    return api.get('/announcements', { params })
  },
  
  // 获取已发布公告列表
  getPublished() {
    return api.get('/announcements/published')
  },
  
  // 获取公告详情
  getById(id) {
    return api.get(`/announcements/${id}`)
  },
  
  // 创建公告
  create(data) {
    return api.post('/announcements', data)
  },
  
  // 更新公告
  update(id, data) {
    return api.put(`/announcements/${id}`, data)
  },
  
  // 删除公告
  delete(id) {
    return api.delete(`/announcements/${id}`)
  },
  
  // 发布公告
  publish(id) {
    return api.put(`/announcements/${id}/publish`)
  },
  
  // 下线公告
  offline(id) {
    return api.put(`/announcements/${id}/offline`)
  },
  
  // 置顶/取消置顶
  toggleTop(id) {
    return api.put(`/announcements/${id}/top`)
  }
}

export default announcementAPI