import api from '../utils/api'

export const getUtilityFees = (params) => api.get('/utility-fees', { params })
export const createUtilityFee = (data) => api.post('/utility-fees', data)
export const updateUtilityFee = (id, data) => api.put(`/utility-fees/${id}`, data)
export const payUtilityFee = (id) => api.post(`/utility-fees/${id}/pay`)
export const deleteUtilityFee = (id) => api.delete(`/utility-fees/${id}`)