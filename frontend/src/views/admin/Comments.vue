<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <el-input v-model="filters.articleId" placeholder="Article ID" />
      <el-select v-model="filters.status" placeholder="Status" clearable>
        <el-option label="Published" value="PUBLISHED" />
        <el-option label="Pending" value="PENDING" />
        <el-option label="Blocked" value="BLOCKED" />
      </el-select>
      <el-button type="primary" @click="loadComments">Filter</el-button>
    </div>
    <el-table :data="comments" stripe>
      <el-table-column prop="content" label="Content" min-width="220" />
      <el-table-column prop="articleId" label="Article" width="120" />
      <el-table-column prop="status" label="Status" width="120" />
      <el-table-column prop="createdAt" label="Created" width="170" />
      <el-table-column label="Actions" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="updateStatus(row, 'PUBLISHED')">Approve</el-button>
          <el-button size="small" @click="updateStatus(row, 'BLOCKED')">Block</el-button>
          <el-button size="small" type="danger" @click="deleteComment(row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api/client'
import type { Comment } from '@/api/types'

const comments = ref<Comment[]>([])
const filters = reactive({
  articleId: '',
  status: '',
})

const loadComments = async () => {
  comments.value = await api.get<Comment[]>('/api/admin/comments', {
    params: {
      articleId: filters.articleId || undefined,
      status: filters.status || undefined,
      page: 1,
      size: 50,
    },
  })
}

const updateStatus = async (comment: Comment, status: string) => {
  try {
    await api.patch(`/api/admin/comments/${comment.id}/status`, { status })
    ElMessage.success('Updated')
    await loadComments()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Update failed')
  }
}

const deleteComment = async (comment: Comment) => {
  await ElMessageBox.confirm('Delete this comment?', 'Confirm', { type: 'warning' })
  try {
    await api.delete(`/api/admin/comments/${comment.id}`)
    ElMessage.success('Deleted')
    await loadComments()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

onMounted(loadComments)
</script>

<style scoped>
.admin-section {
  display: grid;
  gap: 16px;
}

.toolbar {
  padding: 16px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
