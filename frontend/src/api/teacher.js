import api from '../utils/api'

export const teacherAPI = {
  list: () => api.get('/teachers'),
  getById: (id) => api.get(`/teachers/${id}`),
  create: (data) => api.post('/teachers', data),
  update: (id, data) => api.put(`/teachers/${id}`, data),
  delete: (id) => api.delete(`/teachers/${id}`),
  classNames: () => api.get('/teachers/class-names'),
  addScope: (userId, data) => api.post(`/teachers/${userId}/scopes`, data),
  updateScope: (scopeId, data) => api.put(`/teachers/scopes/${scopeId}`, data),
  removeScope: (scopeId) => api.delete(`/teachers/scopes/${scopeId}`)
}

export default teacherAPI
