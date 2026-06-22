/* ============================================
   业务状态常量
   集中管理所有页面中散落的状态映射表
   ============================================ */

/* === 检查类型 === */
export const INSPECTION_TYPE_MAP = {
  SAFETY: '安全检查',
  HYGIENE: '卫生检查',
  COMPREHENSIVE: '综合检查'
}

export const INSPECTION_TYPE_TAG = {
  SAFETY: 'danger',
  HYGIENE: 'success',
  COMPREHENSIVE: 'warning'
}

/* === 检查计划状态 === */
export const INSPECTION_STATUS_MAP = {
  DRAFT: '草稿',
  SCHEDULED: '已安排',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成',
  CANCELLED: '已取消'
}

export const INSPECTION_STATUS_TAG = {
  DRAFT: 'info',
  SCHEDULED: 'warning',
  IN_PROGRESS: 'danger',
  COMPLETED: 'success',
  CANCELLED: 'info'
}

/* === 检查结果 === */
export const RESULT_MAP = {
  PASS: '合格',
  FAIL: '不合格'
}

export const RESULT_TAG = {
  PASS: 'success',
  FAIL: 'danger'
}

/* === 整改状态 === */
export const RECTIFICATION_STATUS_MAP = {
  NONE: '无需整改',
  PENDING: '待整改',
  COMPLETED: '已整改',
  VERIFIED: '已核实'
}

export const RECTIFICATION_STATUS_TAG = {
  NONE: 'info',
  PENDING: 'warning',
  COMPLETED: 'success',
  VERIFIED: ''
}

/* === 性别 === */
export const GENDER_MAP = {
  MALE: '男',
  FEMALE: '女'
}

/* === 楼栋类型 === */
export const BUILDING_TYPE_MAP = {
  MALE: '男生楼',
  FEMALE: '女生楼',
  MIXED: '混合楼'
}

export const BUILDING_TYPE_TAG = {
  MALE: 'primary',
  FEMALE: 'danger',
  MIXED: 'warning'
}

/* === 归寝打卡状态 === */
export const CHECK_IN_STATUS_MAP = {
  0: '正常',
  1: '晚归',
  2: '未归',
  3: '请假'
}

/* === 请假状态 === */
export const LEAVE_STATUS_MAP = {
  0: '待审批',
  1: '已批准',
  2: '已拒绝',
  3: '已撤销',
  4: '已销假'
}

/* === 报修状态 === */
export const REPAIR_STATUS_MAP = {
  0: '待处理',
  1: '处理中',
  2: '已完成',
  3: '已关闭'
}

/* === 水电费缴费状态 === */
export const UTILITY_PAY_STATUS_MAP = {
  0: '未缴费',
  1: '已缴费'
}

/**
 * 根据映射表获取显示名称
 * @param {Object} map - 映射表
 * @param {*} key - 键值
 * @param {string} fallback - 兜底文字
 */
export function getLabel(map, key, fallback = '未知') {
  return map[key] ?? fallback
}

/**
 * 根据映射表获取 Element Plus Tag 类型
 * @param {Object} map - 映射表
 * @param {*} key - 键值
 * @param {string} fallback - 兜底类型
 */
export function getTagType(map, key, fallback = 'info') {
  return map[key] ?? fallback
}
