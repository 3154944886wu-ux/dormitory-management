import api from '../utils/api'

// 学生打卡
export function checkIn(data) {
  return api.post('/checkin', data)
}

// 获取我的打卡记录
export function getMyRecords(params) {
  return api.get('/checkin/my', { params })
}

// 获取今日打卡情况
export function getTodayStatus() {
  return api.get('/checkin/today')
}

// 分页查询打卡记录
export function getRecords(params) {
  return api.get('/checkin/records', { params })
}

// 获取打卡统计（支持 startDate/endDate 范围）
export function getStatistics(params) {
  return api.get('/checkin/statistics', { params })
}

// 归寝趋势统计
export function getCheckInTrend(params) {
  return api.get('/checkin/trend', { params }).catch(() => getStatistics(params))
}

// 导出打卡记录
export function exportRecords(params) {
  return api.get('/checkin/export', { params, responseType: 'blob' })
}