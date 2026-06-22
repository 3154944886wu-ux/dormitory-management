import api from '../utils/api'

export const questionnaireAPI = {
  list: () => api.get('/questionnaires'),

  listWithOptions: () => api.get('/questionnaires/with-options'),

  getById: (id) => api.get(`/questionnaires/${id}`),

  create: (data) => api.post('/questionnaires', data),

  update: (id, data) => api.put(`/questionnaires/${id}`, data),

  updateStatus: (id, isActive) => api.put(`/questionnaires/${id}/status`, { isActive }),

  delete: (id) => api.delete(`/questionnaires/${id}`)
}

export default questionnaireAPI
