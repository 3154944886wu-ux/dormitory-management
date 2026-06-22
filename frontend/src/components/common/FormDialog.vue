<template>
  <el-dialog
    :model-value="modelValue"
    :title="title"
    :width="width"
    :close-on-click-modal="false"
    @update:model-value="$emit('update:modelValue', $event)"
    @closed="$emit('closed')"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-width="labelWidth"
      @submit.prevent
    >
      <slot />
    </el-form>

    <template #footer>
      <slot name="footer">
        <el-button @click="$emit('update:modelValue', false)">取消</el-button>
        <el-button type="primary" :loading="loading" @click="$emit('confirm')">确定</el-button>
      </slot>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'

/**
 * 表单对话框组件
 * 封装 el-dialog + el-form，支持新增/编辑复用
 *
 * @props {boolean} modelValue - 对话框可见性（v-model）
 * @props {string} title       - 对话框标题
 * @props {string} width       - 宽度，默认 '520px'
 * @props {Object} formData    - 表单数据对象
 * @props {Object} rules       - el-form 校验规则
 * @props {string} labelWidth  - 标签宽度，默认 '100px'
 * @props {boolean} loading    - 提交加载状态
 *
 * @events {void} update:modelValue - 可见性变更
 * @events {void} confirm           - 点击确定按钮
 * @events {void} closed            - 对话框关闭动画结束
 */
const props = defineProps({
  modelValue: { type: Boolean, default: false },
  title:      { type: String, default: '对话框' },
  width:      { type: String, default: '520px' },
  formData:   { type: Object, default: () => ({}) },
  rules:      { type: Object, default: () => ({}) },
  labelWidth: { type: String, default: '100px' },
  loading:    { type: Boolean, default: false }
})

defineEmits(['update:modelValue', 'confirm', 'closed'])

const formRef = ref(null)

/** 暴露 validate 方法供父组件调用 */
const validate = () => formRef.value?.validate()
defineExpose({ validate })
</script>
