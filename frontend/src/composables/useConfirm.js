import { ElMessageBox } from 'element-plus'

/**
 * 确认对话框 composable
 * 统一的 ElMessageBox.confirm 封装，中文按钮文字
 *
 * @returns {Object} { confirm, confirmDanger }
 */
export function useConfirm() {
  /** 普通确认 */
  const confirm = (title, message, options = {}) => {
    return ElMessageBox.confirm(message || '确认执行此操作？', title || '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      ...options
    }).then(() => true).catch(() => false)
  }

  /** 危险操作确认（红色按钮） */
  const confirmDanger = (title, message) => {
    return confirm(title, message, {
      confirmButtonText: '确认删除',
      type: 'error'
    })
  }

  return { confirm, confirmDanger }
}
