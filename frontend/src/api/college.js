import api from '../utils/api'

export const collegeAPI = {
  list: () => api.get('/colleges'),

  create: (data) => api.post('/colleges', data),

  update: (id, data) => api.put(`/colleges/${id}`, data),

  delete: (id) => api.delete(`/colleges/${id}`)
}

export default collegeAPI
