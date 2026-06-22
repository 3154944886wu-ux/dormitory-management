import api from '../utils/api'

export const relocationAPI = {
  // 学生提交申请
  apply: (data) => api.post('/relocation/apply', data),

  // 学生查看自己的申请
  myApplications: () => api.get('/relocation/my-applications'),

  // 管理员查看所有申请
  listApplications: (params) => api.get('/relocation/applications', { params }),

  // 管理员查看单个申请
  getApplication: (id) => api.get(`/relocation/applications/${id}`),

  // 管理员审批通过
  approve: (id, comment) => api.post(`/relocation/applications/${id}/approve`, { comment }),

  // 管理员拒绝
  reject: (id, comment) => api.post(`/relocation/applications/${id}/reject`, { comment }),

  // 管理员执行调换
  execute: (id, data) => api.post(`/relocation/applications/${id}/execute`, data)
}
