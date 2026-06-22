<template>
  <div ref="chartRef" class="echarts-wrapper" :style="{ height: height }" />
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

/**
 * ECharts 包装组件
 * 自动处理 resize 和 dispose，props 传 option
 *
 * @props {Object}  option   - ECharts 配置项
 * @props {string}  height   - 高度，默认 '320px'
 * @props {boolean} loading  - 加载状态（显示 ECharts 内置 loading 动画）
 */
const props = defineProps({
  option:  { type: Object, default: () => ({}) },
  height:  { type: String, default: '320px' },
  loading: { type: Boolean, default: false }
})

const chartRef = ref(null)
let chartInstance = null
let resizeObserver = null

const initChart = () => {
  if (!chartRef.value) return
  if (chartInstance) chartInstance.dispose()

  chartInstance = echarts.init(chartRef.value)
  chartInstance.setOption(props.option)

  // 自动 resize
  resizeObserver = new ResizeObserver(() => {
    chartInstance?.resize()
  })
  resizeObserver.observe(chartRef.value)
}

onMounted(() => {
  nextTick(initChart)
})

onUnmounted(() => {
  resizeObserver?.disconnect()
  chartInstance?.dispose()
})

// watch option changes, merge update (not replace)
watch(() => props.option, (newOpt) => {
  if (chartInstance) {
    chartInstance.setOption(newOpt, { notMerge: true })
  }
}, { deep: true })

// watch loading state
watch(() => props.loading, (val) => {
  if (!chartInstance) return
  if (val) chartInstance.showLoading()
  else chartInstance.hideLoading()
})
</script>

<style scoped>
.echarts-wrapper {
  width: 100%;
  min-height: 200px;
}
</style>
