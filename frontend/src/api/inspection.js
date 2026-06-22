import api from '../utils/api'

// ==================== 检查计划 API ====================

// 分页获取检查计划列表
export function getInspectionPlans(params) {
  return api.get('/inspection/plans', { params })
}

// 获取单个检查计划
export function getInspectionPlan(id) {
  return api.get(`/inspection/plans/${id}`)
}

// 获取待执行的检查计划
export function getPendingPlans() {
  return api.get('/inspection/plans/pending')
}

// 按状态查询检查计划
export function getPlansByStatus(status) {
  return api.get(`/inspection/plans/status/${status}`)
}

// 按类型查询检查计划
export function getPlansByType(type) {
  return api.get(`/inspection/plans/type/${type}`)
}

// 创建检查计划
export function createInspectionPlan(data) {
  return api.post('/inspection/plans', data)
}

// 更新检查计划
export function updateInspectionPlan(id, data) {
  return api.put(`/inspection/plans/${id}`, data)
}

// 更新计划状态
export function updatePlanStatus(id, status) {
  return api.put(`/inspection/plans/${id}/status`, { status })
}

// 开始执行检查计划
export function startInspectionPlan(id) {
  return api.post(`/inspection/plans/${id}/start`)
}

// 完成检查计划
export function completeInspectionPlan(id) {
  return api.post(`/inspection/plans/${id}/complete`)
}

// 取消检查计划
export function cancelInspectionPlan(id) {
  return api.post(`/inspection/plans/${id}/cancel`)
}

// 删除检查计划
export function deleteInspectionPlan(id) {
  return api.delete(`/inspection/plans/${id}`)
}

// ==================== 检查记录 API ====================

// 分页获取检查记录列表
export function getInspectionRecords(params) {
  return api.get('/inspection/records', { params })
}

// 获取单个检查记录
export function getInspectionRecord(id) {
  return api.get(`/inspection/records/${id}`)
}

// 获取待整改记录
export function getPendingRectifications() {
  return api.get('/inspection/records/pending')
}

// 按检查计划查询记录
export function getRecordsByPlan(planId) {
  return api.get(`/inspection/records/plan/${planId}`)
}

// 按房间查询记录
export function getRecordsByRoom(roomId) {
  return api.get(`/inspection/records/room/${roomId}`)
}

// 按整改状态查询
export function getRecordsByRectificationStatus(status) {
  return api.get(`/inspection/records/status/${status}`)
}

// 按检查结果查询
export function getRecordsByResult(result) {
  return api.get(`/inspection/records/result/${result}`)
}

// 多条件搜索检查记录
export function searchRecords(params) {
  return api.get('/inspection/records/search', { params })
}

// 创建检查记录（提交检查结果）
export function createInspectionRecord(data) {
  return api.post('/inspection/records', data)
}

// 更新检查记录
export function updateInspectionRecord(id, data) {
  return api.put(`/inspection/records/${id}`, data)
}

// 提交整改
export function submitRectification(id, data) {
  return api.post(`/inspection/records/${id}/rectify`, data)
}

// 审核整改
export function approveRectification(id) {
  return api.post(`/inspection/records/${id}/approve`)
}

// 删除检查记录
export function deleteInspectionRecord(id) {
  return api.delete(`/inspection/records/${id}`)
}

// ==================== 检查项模板 API ====================

// 分页获取检查项列表
export function getInspectionItems(params) {
  return api.get('/inspection/items', { params })
}

// 获取启用的检查项
export function getActiveItems() {
  return api.get('/inspection/items/active')
}

// 按类别获取检查项
export function getItemsByCategory(category) {
  return api.get(`/inspection/items/category/${category}`)
}

// 获取单个检查项
export function getInspectionItem(id) {
  return api.get(`/inspection/items/${id}`)
}

// 创建检查项
export function createInspectionItem(data) {
  return api.post('/inspection/items', data)
}

// 更新检查项
export function updateInspectionItem(id, data) {
  return api.put(`/inspection/items/${id}`, data)
}

// 删除检查项
export function deleteInspectionItem(id) {
  return api.delete(`/inspection/items/${id}`)
}
