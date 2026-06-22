import api from '../utils/api'

// 提交请假申请
export function submitLeaveRequest(data) {
  return api.post('/leave-requests', data)
}

// 审批请假申请
export function approveLeaveRequest(id, note) {
  return api.post(`/leave-requests/${id}/approve`, { status: 1, note: note || '' })
}

// 拒绝请假申请
export function rejectLeaveRequest(id, reason) {
  return api.post(`/leave-requests/${id}/reject`, { reason })
}

// 撤销请假申请
export function cancelLeaveRequest(id) {
  return api.post(`/leave-requests/${id}/cancel`)
}

// 销假（确认返回）
export function confirmReturn(id) {
  return api.post(`/leave-requests/${id}/confirm-return`)
}

// 获取请假申请详情
export function getLeaveRequest(id) {
  return api.get(`/leave-requests/${id}`)
}

// 获取我的请假记录
export function getMyLeaveRequests() {
  return api.get('/leave-requests/my')
}

// 按状态查询
export function getLeaveRequestsByStatus(status) {
  return api.get(`/leave-requests/status/${status}`)
}

// 分页查询所有请假申请
export function getLeaveRequests(params) {
  return api.get('/leave-requests', { params })
}

// 获取请假统计
export function getLeaveStatistics() {
  return api.get('/leave-requests/statistics')
}

// 获取待审批列表（管理员）
export function getLeaveRequestsForApproval(params) {
  return api.get('/leave-requests/pending', { params })
}