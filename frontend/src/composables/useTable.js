import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * 通用表格数据管理 composable
 * 替代 25+ 页面中重复的 loading/data/pagination 模式
 *
 * @param {Object} options
 * @param {number} options.pageSize - 默认每页条数
 * @param {Function} options.fetchFn - 数据获取函数 (params) => Promise<{data, total}>
 * @returns {Object} { loading, data, total, currentPage, pageSize, loadData, refresh, resetPage }
 */
export function useTable(options = {}) {
  const { pageSize: defaultPageSize = 10, fetchFn = null } = options

  const loading = ref(false)
  const data = ref([])
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(defaultPageSize)

  const loadData = async (customFetchFn) => {
    const fn = customFetchFn || fetchFn
    if (!fn) return

    loading.value = true
    try {
      const params = { page: currentPage.value, size: pageSize.value }
      const res = await fn(params)
      data.value = res.data || []
      total.value = res.total || 0
    } catch (error) {
      ElMessage.error('加载数据失败')
    } finally {
      loading.value = false
    }
  }

  const refresh = () => loadData()
  const resetPage = () => { currentPage.value = 1; }

  return { loading, data, total, currentPage, pageSize, loadData, refresh, resetPage }
}
