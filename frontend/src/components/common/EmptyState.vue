<template>
  <div class="empty-state">
    <el-empty
      :image-size="imageSize"
      :description="description"
    >
      <template v-if="actionText" #default>
        <el-button type="primary" @click="$emit('action')">
          {{ actionText }}
        </el-button>
      </template>
    </el-empty>
  </div>
</template>

<script setup>
/**
 * 空状态组件
 * 统一所有页面的空数据展示，支持引导性 CTA 按钮
 *
 * @props {string}  description - 引导文案
 * @props {number}  imageSize   - 插图尺寸，默认 120
 * @props {string}  actionText  - CTA 按钮文字，不传则不显示按钮
 *
 * @events {void} action - CTA 按钮点击
 */
defineProps({
  description: { type: String, default: '暂无数据' },
  imageSize:   { type: Number, default: 120 },
  actionText:  { type: String, default: '' }
})

defineEmits(['action'])
</script>

<style scoped>
.empty-state {
  padding: var(--spacing-xxl) 0;
  display: flex;
  justify-content: center;
}
</style>
