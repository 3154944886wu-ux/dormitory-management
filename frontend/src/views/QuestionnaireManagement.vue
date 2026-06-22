<template>
  <div class="questionnaire-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>问卷题目管理</span>
          <el-button type="primary" @click="handleAdd" v-if="isAdmin">
            <el-icon><Plus /></el-icon>
            新增题目
          </el-button>
        </div>
      </template>

      <el-table :data="questions" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="questionText" label="题目内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="questionType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.questionType === 'bed' ? 'warning' : 'primary'" size="small">
              {{ row.questionType === 'bed' ? '床位类' : '匹配类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="权重" width="80" align="center" />
        <el-table-column label="选项数" width="80" align="center">
          <template #default="{ row }">
            {{ row.options ? row.options.length : 0 }}
          </template>
        </el-table-column>
        <el-table-column label="启用" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.isActive"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
              :disabled="!isAdmin"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" v-if="isAdmin">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑题目' : '新增题目'"
      width="650px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="题目内容" prop="questionText">
          <el-input v-model="form.questionText" placeholder="如：你的作息习惯？" />
        </el-form-item>
        <el-form-item label="题目类型" prop="questionType">
          <el-select v-model="form.questionType" style="width: 100%;">
            <el-option label="匹配类" value="match" />
            <el-option label="床位类" value="bed" />
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="权重" prop="weight">
              <el-input-number v-model="form.weight" :min="1" :max="100" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否必填" prop="isRequired">
              <el-switch v-model="form.isRequired" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否启用" prop="isActive">
              <el-switch v-model="form.isActive" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="options-section">
        <div class="options-header">
          <span class="options-title">选项列表</span>
          <el-button type="primary" size="small" @click="addOptionRow">
            <el-icon><Plus /></el-icon>
            添加选项
          </el-button>
        </div>
        <el-table :data="form.options" border size="small" style="width: 100%;">
          <el-table-column label="序号" type="index" width="60" align="center" />
          <el-table-column label="选项文本" prop="optionText">
            <template #default="{ row, $index }">
              <el-input v-model="row.optionText" placeholder="如：早睡早起" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="匹配值" prop="optionValue" width="120" align="center">
            <template #default="{ row, $index }">
              <el-input-number v-model="row.optionValue" :min="0" :max="100" size="small" controls-position="right" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70" align="center">
            <template #default="{ $index }">
              <el-button type="danger" link size="small" @click="removeOptionRow($index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="form.options.length === 0" class="empty-options">暂无选项，请点击"添加选项"</div>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { questionnaireAPI } from '@/api/questionnaire'

const loading = ref(false)
const questions = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  questionText: '',
  questionType: 'match',
  weight: 1,
  isRequired: 1,
  isActive: 1,
  options: []
})

const rules = {
  questionText: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  questionType: [{ required: true, message: '请选择题目类型', trigger: 'change' }]
}

const isAdmin = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.role?.toUpperCase() === 'ADMIN'
})

const loadQuestions = async () => {
  loading.value = true
  try {
    const res = await questionnaireAPI.listWithOptions()
    questions.value = res.data || []
  } catch (error) {
    ElMessage.error('加载问卷列表失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    questionText: '',
    questionType: 'match',
    weight: 1,
    isRequired: 1,
    isActive: 1,
    options: []
  })
}

const addOptionRow = () => {
  form.options.push({ optionText: '', optionValue: 0 })
}

const removeOptionRow = (index) => {
  form.options.splice(index, 1)
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  resetForm()
  Object.assign(form, {
    id: row.id,
    questionText: row.questionText,
    questionType: row.questionType,
    weight: row.weight,
    isRequired: row.isRequired,
    isActive: row.isActive,
    options: row.options ? row.options.map(o => ({ ...o })) : []
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (form.options.length === 0) {
    ElMessage.warning('请至少添加一个选项')
    return
  }

  const data = {
    questionText: form.questionText,
    questionType: form.questionType,
    weight: form.weight,
    isRequired: form.isRequired,
    isActive: form.isActive,
    options: form.options
  }

  try {
    if (isEdit.value) {
      await questionnaireAPI.update(form.id, data)
      ElMessage.success('更新成功')
    } else {
      await questionnaireAPI.create(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadQuestions()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除题目"${row.questionText}"吗？`, '提示', { type: 'warning' })
    await questionnaireAPI.delete(row.id)
    ElMessage.success('删除成功')
    loadQuestions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

const handleStatusChange = async (row) => {
  try {
    await questionnaireAPI.updateStatus(row.id, row.isActive)
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
    row.isActive = row.isActive === 1 ? 0 : 1
  }
}

onMounted(() => {
  loadQuestions()
})
</script>

<style scoped>
.questionnaire-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.options-section {
  margin-top: 10px;
}

.options-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.options-title {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.empty-options {
  text-align: center;
  color: #c0c4cc;
  padding: 20px 0;
  font-size: 13px;
}
</style>
