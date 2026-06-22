<template>
  <div class="stat-card" :class="{ 'stat-card--clickable': !!$attrs.onClick }" @click="$emit('click')">
    <div class="stat-card__icon" :style="{ color: color, background: iconBg }">
      <slot name="icon">
        <el-icon :size="22"><component :is="icon" /></el-icon>
      </slot>
    </div>
    <div class="stat-card__body">
      <div class="stat-card__value" :style="{ color: valueColor || color }">{{ value }}</div>
      <div class="stat-card__label">{{ label }}</div>
    </div>
  </div>
</template>

<script setup>
/**
 * 统计卡片组件
 * 用于 Dashboard 和各统计页面的指标展示
 *
 * @props {string|number} value - 数值
 * @props {string} label        - 标签文字
 * @props {string} color        - 主题色，默认使用主色
 * @props {*} icon              - Element Plus 图标组件
 *
 * @events {void} click
 */
defineProps({
  value:      { type: [String, Number], default: 0 },
  label:      { type: String, default: '' },
  color:      { type: String, default: 'var(--color-primary)' },
  valueColor: { type: String, default: '' },
  icon:       { type: Object, default: null },
  iconBg:     { type: String, default: 'var(--color-primary-light)' }
})

defineEmits(['click'])
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
}
.stat-card--clickable {
  cursor: pointer;
}
.stat-card--clickable:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-card__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: var(--radius-lg);
  flex-shrink: 0;
}

.stat-card__value {
  font-size: 28px;
  font-weight: var(--font-weight-bold);
  line-height: 1.1;
}

.stat-card__label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-top: 2px;
}
</style>
