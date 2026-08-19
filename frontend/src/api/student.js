import api from '../utils/api'

// 获取学生列表
export const getStudents = (params = {}) => {
  return api.get('/students', { params })
}

// 获取学生详情
export const getStudentById = (id) => {
  return api.get(`/students/${id}`)
}

// 根据学号查询
export const getStudentByNo = (studentNo) => {
  return api.get(`/students/no/${studentNo}`)
}

// 创建学生
export const createStudent = (student) => {
  return api.post('/students', student)
}

// 更新学生
export const updateStudent = (id, student) => {
  return api.put(`/students/${id}`, student)
}

// 退宿
export const checkOutStudent = (id) => {
  return api.post(`/students/${id}/checkout`)
}

// 删除学生
export const deleteStudent = (id) => {
  return api.delete(`/students/${id}`)
}

// 调宿
export const relocateStudent = (id, data) => {
  return api.post(`/students/${id}/relocate`, data)
}

// 学生端 API 对象（供 StudentHome 等页面使用）
export const studentAPI = {
  // 获取个人信息（通过学号）
  getProfile: () => {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const studentNo = user.username
    if (!studentNo) return Promise.reject('未登录')
    return api.get(`/students/no/${studentNo}`)
  },

  // 今日打卡状态
  getTodayCheckIn: () => {
    return api.get('/checkin/today')
  },

  // 月度统计（学生端：获取自己的记录来统计）
  getMonthStats: () => {
    return api.get('/checkin/my')
  },

  // 我的房间
  getMyRoom: () => {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const studentNo = user.username
    if (!studentNo) return Promise.reject('未登录')
    return api.get(`/students/no/${studentNo}`)
  },

  // 公告列表
  getAnnouncements: (limit = 5) => {
    return api.get('/announcements/published', { params: { pageSize: limit } })
  },

  // 我的打卡记录（客户端分页/筛选）
  getCheckRecords: async (params = {}) => {
    const res = await api.get('/checkin/my')
    let records = Array.isArray(res.data) ? res.data : (res.data?.records || [])

    if (params.startDate) {
      records = records.filter(r => r.checkDate >= params.startDate)
    }
    if (params.endDate) {
      records = records.filter(r => r.checkDate <= params.endDate)
    }

    const total = records.length
    const page = params.page || 1
    const size = params.size || 10
    const start = (page - 1) * size

    return {
      data: {
        records: records.slice(start, start + size),
        total
      }
    }
  },

  // 我的请假记录（客户端分页）
  getLeaveRecords: async (params = {}) => {
    const res = await api.get('/leave-requests/my')
    const records = Array.isArray(res.data) ? res.data : []

    const total = records.length
    const page = params.page || 1
    const size = params.size || 10
    const start = (page - 1) * size

    return {
      data: {
        records: records.slice(start, start + size),
        total
      }
    }
  },

  // 我的报修记录
  getRepairs: async (params = {}) => {
    const res = await api.get('/repairs', { params })
    const records = Array.isArray(res.data) ? res.data : (res.data?.records || [])

    const total = records.length
    const page = params.page || 1
    const size = params.size || 10
    const start = (page - 1) * size

    return {
      data: {
        records: records.slice(start, start + size),
        total
      }
    }
  },

  // 提交报修
  createRepair: (data) => api.post('/repairs', data)
}

export default {
  getStudents,
  getStudentById,
  getStudentByNo,
  createStudent,
  updateStudent,
  checkOutStudent,
  deleteStudent,
  relocateStudent,
  studentAPI
}