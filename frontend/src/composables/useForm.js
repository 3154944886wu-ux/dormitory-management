import { ref, reactive } from 'vue'

/**
 * 表单对话框管理 composable
 * 替代 20+ 页面中重复的 add/edit dialog 模式
 *
 * @param {Object} defaultForm - 表单初始值对象
 * @returns {Object} { dialogVisible, isEdit, form, formRef, submitting, showAdd, showEdit, closeDialog }
 */
export function useForm(defaultForm = {}) {
  const dialogVisible = ref(false)
  const isEdit = ref(false)
  const formRef = ref(null)
  const submitting = ref(false)
  const form = reactive({ ...defaultForm })

  /** 显示新增对话框 */
  const showAdd = (resetFn) => {
    isEdit.value = false
    Object.assign(form, defaultForm)
    if (resetFn) resetFn(form)
    dialogVisible.value = true
  }

  /** 显示编辑对话框 */
  const showEdit = (row, fillFn) => {
    isEdit.value = true
    Object.assign(form, defaultForm)
    if (fillFn) {
      fillFn(form, row)
    } else {
      Object.assign(form, row)
    }
    dialogVisible.value = true
  }

  /** 关闭对话框 */
  const closeDialog = () => {
    dialogVisible.value = false
    submitting.value = false
  }

  return { dialogVisible, isEdit, form, formRef, submitting, showAdd, showEdit, closeDialog }
}
