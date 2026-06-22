<template>
  <div class="data-table-wrapper">
    <el-table
      ref="tableRef"
      :data="data"
      :stripe="stripe"
      :border="border"
      v-loading="loading"
      :max-height="maxHeight"
      :row-key="rowKey"
      @row-click="(row) => $emit('row-click', row)"
      @selection-change="(rows) => $emit('selection-change', rows)"
    >
      <slot />
    </el-table>

    <div v-if="showPagination" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPageModel"
        v-model:page-size="pageSizeModel"
        :total="total"
        :page-sizes="pageSizes"
        :layout="layout"
        background
        @size-change="$emit('size-change', $event)"
        @current-change="$emit('page-change', $event)"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

/**
 * 数据表格组件
 * 封装 el-table + el-pagination，减少 25+ 页面的重复代码
 *
 * @props {Array}    data          - 表格数据
 * @props {boolean}  loading       - 加载状态
 * @props {number}   total         - 总条数
 * @props {number}   currentPage   - 当前页（v-model）
 * @props {number}   pageSize      - 每页条数（v-model）
 * @props {boolean}  showPagination- 是否显示分页，默认 true
 * @props {number[]} pageSizes     - 每页条数选项
 * @props {boolean}  stripe        - 斑马纹，默认 true
 * @props {boolean}  border        - 边框
 * @props {string}   maxHeight     - 最大高度
 * @props {string}   rowKey        - 行 key
 * @props {string}   layout        - 分页布局
 *
 * @events {void} update:currentPage, update:pageSize, row-click, selection-change,
 *               size-change, page-change
 */
const props = defineProps({
  data:           { type: Array,    default: () => [] },
  loading:        { type: Boolean,  default: false },
  total:          { type: Number,   default: 0 },
  currentPage:    { type: Number,   default: 1 },
  pageSize:       { type: Number,   default: 10 },
  showPagination: { type: Boolean,  default: true },
  pageSizes:      { type: Array,    default: () => [10, 20, 50] },
  stripe:         { type: Boolean,  default: true },
  border:         { type: Boolean,  default: false },
  maxHeight:      { type: [String, Number], default: undefined },
  rowKey:         { type: String,   default: 'id' },
  layout:         { type: String,   default: 'total, sizes, prev, pager, next' }
})

const emit = defineEmits([
  'update:currentPage', 'update:pageSize',
  'row-click', 'selection-change',
  'size-change', 'page-change'
])

const currentPageModel = computed({
  get: () => props.currentPage,
  set: val => emit('update:currentPage', val)
})

const pageSizeModel = computed({
  get: () => props.pageSize,
  set: val => emit('update:pageSize', val)
})
</script>

<style scoped>
.data-table-wrapper {
  width: 100%;
}
</style>
