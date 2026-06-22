<template>
  <div class="skeleton-loader" :class="`skeleton-${type}`">
    <!-- 表格骨架 -->
    <template v-if="type === 'table'">
      <div class="skeleton-table-header">
        <div v-for="i in 5" :key="i" class="skeleton-cell skeleton-animate" />
      </div>
      <div v-for="row in count" :key="row" class="skeleton-table-row">
        <div v-for="col in 5" :key="col" class="skeleton-cell skeleton-animate" />
      </div>
    </template>

    <!-- 卡片骨架 -->
    <template v-if="type === 'card'">
      <div v-for="c in count" :key="c" class="skeleton-card">
        <div class="skeleton-card-icon skeleton-animate" />
        <div class="skeleton-card-lines">
          <div class="skeleton-line skeleton-animate" style="width: 60%" />
          <div class="skeleton-line skeleton-animate" style="width: 80%" />
        </div>
      </div>
    </template>

    <!-- 表单骨架 -->
    <template v-if="type === 'form'">
      <div v-for="f in count" :key="f" class="skeleton-form-row">
        <div class="skeleton-label skeleton-animate" style="width: 80px" />
        <div class="skeleton-input skeleton-animate" />
      </div>
    </template>

    <!-- 列表骨架 -->
    <template v-if="type === 'list'">
      <div v-for="l in count" :key="l" class="skeleton-list-item">
        <div class="skeleton-avatar skeleton-animate" />
        <div class="skeleton-list-lines">
          <div class="skeleton-line skeleton-animate" style="width: 40%" />
          <div class="skeleton-line skeleton-animate" style="width: 70%" />
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
/**
 * 骨架屏加载组件
 * 替代 v-loading 圆圈的视觉效果，支持 4 种布局
 *
 * @props {'table'|'card'|'form'|'list'} type  - 骨架类型
 * @props {number} count - 骨架行数，默认 5
 */
defineProps({
  type:  { type: String, default: 'table', validator: v => ['table','card','form','list'].includes(v) },
  count: { type: Number, default: 5 }
})
</script>

<style scoped>
.skeleton-loader { --sk-color: #e8e8e8; padding: var(--spacing-md) 0; }

/* 通用闪烁动画 */
.skeleton-animate {
  background: linear-gradient(90deg, var(--sk-color) 25%, #f0f0f0 50%, var(--sk-color) 75%);
  background-size: 200% 100%;
  animation: skeleton-shimmer 1.5s ease-in-out infinite;
  border-radius: var(--radius-sm);
}
@keyframes skeleton-shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* 表格 */
.skeleton-table-header,
.skeleton-table-row { display: flex; gap: var(--spacing-sm); margin-bottom: var(--spacing-sm); }
.skeleton-table-header .skeleton-cell { height: 40px; flex: 1; }
.skeleton-table-row .skeleton-cell { height: 32px; flex: 1; }

/* 卡片 */
.skeleton-card { display: flex; gap: var(--spacing-md); padding: var(--spacing-md); margin-bottom: var(--spacing-sm); border: 1px solid var(--color-border); border-radius: var(--radius-lg); }
.skeleton-card-icon { width: 48px; height: 48px; border-radius: var(--radius-lg); flex-shrink: 0; }
.skeleton-card-lines { flex: 1; display: flex; flex-direction: column; gap: var(--spacing-xs); }

/* 表单 */
.skeleton-form-row { display: flex; align-items: center; gap: var(--spacing-md); margin-bottom: var(--spacing-md); }
.skeleton-label { height: 16px; }
.skeleton-input { flex: 1; height: 36px; border-radius: var(--radius-md); }

/* 列表 */
.skeleton-list-item { display: flex; align-items: center; gap: var(--spacing-md); padding: var(--spacing-sm) 0; }
.skeleton-avatar { width: 40px; height: 40px; border-radius: var(--radius-round); flex-shrink: 0; }
.skeleton-list-lines { flex: 1; display: flex; flex-direction: column; gap: var(--spacing-xs); }

/* 通用线条 */
.skeleton-line { height: 14px; }
</style>
