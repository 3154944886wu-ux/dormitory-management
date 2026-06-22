import api from '../utils/api'

export const dormBatchAPI = {
  list: (params) => api.get('/batches', { params }),

  getById: (id) => api.get(`/batches/${id}`),

  create: (data) => api.post('/batches', data),

  update: (id, data) => api.put(`/batches/${id}`, data),

  start: (id) => api.put(`/batches/${id}/start`),

  cutoff: (id) => api.put(`/batches/${id}/cutoff`),

  finish: (id) => api.put(`/batches/${id}/finish`),

  reset: (id) => api.put(`/batches/${id}/reset`),

  delete: (id) => api.delete(`/batches/${id}`),

  archive: (id) => api.put(`/batches/${id}/archive`)
}

export default dormBatchAPI
