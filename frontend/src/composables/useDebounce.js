import { ref, watch } from 'vue'

/**
 * 防抖 composable
 * @param {*} initialValue - 初始值
 * @param {number} delay - 延迟毫秒数
 * @returns {Object} { inputValue, debouncedValue }
 */
export function useDebounce(initialValue = '', delay = 300) {
  const inputValue = ref(initialValue)
  const debouncedValue = ref(initialValue)

  let timer = null
  watch(inputValue, (val) => {
    clearTimeout(timer)
    timer = setTimeout(() => {
      debouncedValue.value = val
    }, delay)
  })

  return { inputValue, debouncedValue }
}
