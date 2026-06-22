import api from '../utils/api'

export const majorAPI = {
  list: (collegeId) => api.get('/majors', { params: collegeId ? { collegeId } : {} }),

  create: (data) => api.post('/majors', data),

  update: (id, data) => api.put(`/majors/${id}`, data),

  delete: (id) => api.delete(`/majors/${id}`)
}

export default majorAPI
