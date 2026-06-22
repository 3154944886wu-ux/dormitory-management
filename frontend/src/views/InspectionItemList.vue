<template>
  <div class="page-container">
    <div class="page-header">
      <h2>检查项目管理</h2>
      <el-button type="primary" @click="showAdd"><el-icon><Plus /></el-icon>新增检查项</el-button>
    </div>

    <el-card class="table-card" shadow="never">
      <SkeletonLoader v-if="loading && items.length === 0" type="table" :count="5" />
      <template v-else>
        <el-table :data="items" v-loading="loading">
          <el-table-column prop="id" label="ID" width="64" align="center" />
          <el-table-column prop="name" label="检查项名称" min-width="140" />
          <el-table-column label="类别" width="88" align="center">
            <template #default="{ row }">
              <span v-if="row.category === 'SAFETY'" class="tag tag--danger">安全</span>
              <span v-else class="tag tag--success">卫生</span>
            </template>
          </el-table-column>
          <el-table-column prop="standard" label="检查标准" min-width="200" show-overflow-tooltip />
          <el-table-column label="分值" width="72" align="center">
            <template #default="{ row }"><span class="stat-number">{{ row.maxScore }}</span></template>
          </el-table-column>
          <el-table-column prop="sortOrder" label="排序" width="64" align="center" />
          <el-table-column label="状态" width="76" align="center">
            <template #default="{ row }">
              <span v-if="row.status === 1" class="tag tag--info">启用</span>
              <span v-else class="tag tag--muted">禁用</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="128" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="showEdit(row)">编辑</el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <EmptyState v-if="!loading && items.length === 0" description="暂无检查项目" action-text="创建第一个检查项" @action="showAdd" />
        <div class="table-footer">
          <span class="table-footer__total">共 <strong>{{ total }}</strong> 条记录</span>
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="sizes, prev, pager, next"
            background
            size="default"
            @size-change="loadItems"
            @current-change="loadItems"
          />
        </div>
      </template>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑检查项' : '新增检查项'" width="500px">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" placeholder="如：地面清洁" /></el-form-item>
        <el-form-item label="类别" prop="category">
          <el-select v-model="form.category" style="width:100%"><el-option label="安全检查" value="SAFETY" /><el-option label="卫生检查" value="HYGIENE" /></el-select>
        </el-form-item>
        <el-form-item label="标准" prop="standard"><el-input v-model="form.standard" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="分值"><el-input-number v-model="form.maxScore" :min="0" :max="100" :precision="1" style="width:100%" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getInspectionItems, createInspectionItem, updateInspectionItem, deleteInspectionItem } from '@/api/inspection'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import { useConfirm } from '@/composables/useConfirm'
import SkeletonLoader from '@/components/common/SkeletonLoader.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const { loading, data: items, total, currentPage, pageSize, loadData: loadItems } = useTable({ fetchFn: getInspectionItems, pageSize: 20 })
const { dialogVisible, isEdit, form, formRef, submitting } = useForm({
  id: null, name: '', category: '', standard: '', maxScore: 10, sortOrder: 0, status: 1
})
const { confirmDanger } = useConfirm()

const rules = {
  name: [{ required: true, message: '请输入检查项名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择类别', trigger: 'change' }]
}

const showAdd = () => {
  isEdit.value = false; Object.assign(form, { id: null, name: '', category: '', standard: '', maxScore: 10, sortOrder: 0, status: 1 })
  dialogVisible.value = true
}
const showEdit = (row) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (isEdit.value) { await updateInspectionItem(form.id, { ...form }); ElMessage.success('更新成功') }
      else { await createInspectionItem({ ...form }); ElMessage.success('创建成功') }
      dialogVisible.value = false; loadItems()
    } catch (e) { ElMessage.error(e.response?.data?.message || '操作失败') }
    finally { submitting.value = false }
  })
}

const handleDelete = async (row) => {
  if (!(await confirmDanger('删除检查项', `确认删除「${row.name}」？`))) return
  try { await deleteInspectionItem(row.id); ElMessage.success('已删除'); loadItems() } catch (e) { ElMessage.error('删除失败') }
}

onMounted(() => loadItems())
</script>

<style scoped>
/* 自定义 inline tag — 纯色背景 + 深色文字，无边框 */
.tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  border-radius: var(--radius-round);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  line-height: 1.6;
  white-space: nowrap;
}
.tag--success { background: #e8f5e9; color: #2e7d32; }
.tag--danger  { background: #ffebee; color: #c62828; }
.tag--info    { background: #e3f2fd; color: #1565c0; }
.tag--muted   { background: #f5f5f5; color: #9e9e9e; }

/* 表格卡片 */
.table-card {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
}

/* 表头 */
:deep(.el-table__header-wrapper th.el-table__cell) {
  background-color: #fafafa;
  color: var(--color-text-primary);
  font-weight: var(--font-weight-semibold);
  border-bottom: 1px solid var(--color-border);
}

/* 表格行 */
:deep(.el-table__body-wrapper tr.el-table__row) {
  transition: background-color var(--transition-fast);
}
:deep(.el-table__body-wrapper tr.el-table__row:hover) {
  background-color: var(--color-bg-hover);
}

/* 表格底栏：总数 + 分页 */
.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-border-light);
}
.table-footer__total {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}
.table-footer__total strong {
  color: var(--color-text-primary);
  font-weight: var(--font-weight-semibold);
}
</style>
