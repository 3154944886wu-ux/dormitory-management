import api from '../utils/api'

// 分页查询所有异常记录
export function getExceptions(params) {
  return api.get('/check-exceptions', { params })
}

// 获取异常记录详情
export function getException(id) {
  return api.get(`/check-exceptions/${id}`)
}

// 按日期查询
export function getExceptionsByDate(date) {
  return api.get(`/check-exceptions/date/${date}`)
}

// 按学生查询
export function getExceptionsByStudent(studentId) {
  return api.get(`/check-exceptions/student/${studentId}`)
}

// 按处理状态查询
export function getExceptionsByHandled(handled) {
  return api.get(`/check-exceptions/handled/${handled}`)
}

// 多条件搜索
export function searchExceptions(params) {
  return api.get('/check-exceptions/search', { params })
}

// 处理异常记录
export function handleException(id, data) {
  return api.post(`/check-exceptions/${id}/handle`, data)
}

// 导出异常记录
export function exportExceptions(params) {
  return api.get('/check-exceptions/export', { params, responseType: 'blob' })
}

// 获取异常统计
export function getExceptionStatistics(date) {
  return api.get('/check-exceptions/statistics', { params: { date } })
}

// 获取日期范围内的异常数量
export function countExceptions(startDate, endDate) {
  return api.get('/check-exceptions/count', { params: { startDate, endDate } })
}

// 异常趋势统计
export function getExceptionTrend(params) {
  return api.get('/check-exceptions/trend', { params })
}