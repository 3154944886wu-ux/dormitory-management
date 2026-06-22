import api from '../utils/api'

export const managerScopeAPI = {
  list: () => api.get('/manager-scopes'),
  create: (data) => api.post('/manager-scopes', data),
  update: (id, data) => api.put(`/manager-scopes/${id}`, data),
  delete: (id) => api.delete(`/manager-scopes/${id}`)
}

export default managerScopeAPI
