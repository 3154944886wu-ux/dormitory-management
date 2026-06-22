<template>
  <div class="student-announcements">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告通知</span>
        </div>
      </template>
      
      <!-- 搜索 -->
      <div class="filter-bar">
        <el-input
          v-model="searchText"
          placeholder="搜索公告标题"
          style="width: 300px"
          clearable
          @clear="loadAnnouncements"
          @keyup.enter="loadAnnouncements"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <!-- 公告列表 -->
      <div class="announcement-list" v-loading="loading">
        <div 
          v-for="item in announcements" 
          :key="item.id" 
          class="announcement-item"
          @click="showDetail(item)"
        >
          <div class="item-header">
            <el-tag v-if="item.isTop" type="danger" size="small">置顶</el-tag>
            <span class="title">{{ item.title }}</span>
          </div>
          <div class="item-content">{{ item.content }}</div>
          <div class="item-footer">
            <span class="author">发布人：{{ item.author }}</span>
            <span class="time">{{ item.createTime }}</span>
          </div>
        </div>
        
        <el-empty v-if="!loading && announcements.length === 0" description="暂无公告" />
      </div>
      
      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadAnnouncements"
          @current-change="loadAnnouncements"
        />
      </div>
    </el-card>
    
    <!-- 公告详情弹窗 -->
    <el-dialog v-model="detailVisible" title="公告详情" width="600px">
      <div class="detail-content" v-if="currentAnnouncement">
        <h3>{{ currentAnnouncement.title }}</h3>
        <div class="meta">
          <span>发布人：{{ currentAnnouncement.author }}</span>
          <span>发布时间：{{ currentAnnouncement.createTime }}</span>
        </div>
        <el-divider />
        <div class="content">{{ currentAnnouncement.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import api from '@/utils/api'

const loading = ref(false)
const searchText = ref('')
const announcements = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const detailVisible = ref(false)
const currentAnnouncement = ref(null)

const loadAnnouncements = async () => {
  loading.value = true
  try {
    const res = await api.get('/announcements', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        title: searchText.value
      }
    })
    announcements.value = res.data?.list || res.data || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载公告失败:', error)
    ElMessage.error('加载公告失败')
  } finally {
    loading.value = false
  }
}

const showDetail = (item) => {
  currentAnnouncement.value = item
  detailVisible.value = true
}

onMounted(() => {
  loadAnnouncements()
})
</script>

<style scoped>
.student-announcements {
  padding: 0;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.filter-bar {
  margin-bottom: 20px;
}

.announcement-list {
  min-height: 300px;
}

.announcement-item {
  padding: 16px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}

.announcement-item:hover {
  background-color: #f5f7fa;
}

.announcement-item:last-child {
  border-bottom: none;
}

.item-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.item-header .title {
  font-size: 16px;
  font-weight: 500;
}

.item-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-content h3 {
  margin: 0 0 10px 0;
}

.detail-content .meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #909399;
}

.detail-content .content {
  font-size: 15px;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>