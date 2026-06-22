import api from '../utils/api'

export const getRepairs = (params) => api.get('/repairs', { params })
export const createRepair = (data) => api.post('/repairs', data)
export const updateRepairStatus = (id, status) => api.put(`/repairs/${id}/status`, { status })