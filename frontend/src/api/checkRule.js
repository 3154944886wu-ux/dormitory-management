import api from '../utils/api'

// 获取所有规则
export function getCheckRules() {
  return api.get('/check-rules')
}

// 获取生效的规则
export function getActiveRules() {
  return api.get('/check-rules/active')
}

// 获取默认规则
export function getDefaultRule() {
  return api.get('/check-rules/default')
}

// 根据楼栋获取规则
export function getRuleByBuilding(buildingId) {
  return api.get(`/check-rules/building/${buildingId}`)
}

// 获取规则详情
export function getCheckRule(id) {
  return api.get(`/check-rules/${id}`)
}

// 创建规则
export function createCheckRule(data) {
  return api.post('/check-rules', data)
}

// 更新规则
export function updateCheckRule(id, data) {
  return api.put(`/check-rules/${id}`, data)
}

// 删除规则
export function deleteCheckRule(id) {
  return api.delete(`/check-rules/${id}`)
}

// 设为默认规则
export function setDefaultRule(id) {
  return api.post(`/check-rules/${id}/set-default`)
}

// 切换启用/停用
export function toggleCheckRuleStatus(id, status) {
  return api.post(`/check-rules/${id}/toggle-status`, { status })
}