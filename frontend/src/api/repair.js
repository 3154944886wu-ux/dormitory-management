import api from '../utils/api'

export const getRepairs = (params) => api.get('/repairs', { params })
export const createRepair = (data) => api.post('/repairs', data)
export const handleRepair = (id, handler, note) => api.post(`/repairs/${id}/handle`, { handler, note })
export const completeRepair = (id, note) => api.post(`/repairs/${id}/complete`, { note })
export const closeRepair = (id, note) => api.post(`/repairs/${id}/close`, { note })
export const deleteRepair = (id) => api.delete(`/repairs/${id}`)