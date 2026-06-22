import api from '../utils/api'

export const dashboardAPI = {
  // 获取统计数据
  getOverview() {
    return api.get('/dashboard/overview')
  },
  
  // 获取入住统计
  getAccommodationStats() {
    return api.get('/dashboard/accommodation')
  },
  
  // 获取报修统计
  getRepairStats() {
    return api.get('/dashboard/repair')
  },
  
  // 获取水电费统计
  getUtilityStats() {
    return api.get('/dashboard/utility')
  }
}

export default dashboardAPI