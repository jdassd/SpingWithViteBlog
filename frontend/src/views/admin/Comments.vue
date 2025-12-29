<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <el-input v-model="filters.articleId" :placeholder="$t('admin.labels.articleId')" />
      <el-select v-model="filters.status" :placeholder="$t('admin.columns.status')" clearable>
        <el-option :label="$t('admin.statusOptions.published')" value="PUBLISHED" />
        <el-option :label="$t('admin.statusOptions.pending')" value="PENDING" />
        <el-option :label="$t('admin.statusOptions.blocked')" value="BLOCKED" />
      </el-select>
      <el-button type="primary" @click="loadComments">{{ $t('admin.filter') }}</el-button>
    </div>
    <el-table :data="comments" stripe>
      <el-table-column prop="content" :label="$t('admin.columns.content')" min-width="220" />
      <el-table-column prop="articleId" :label="$t('admin.columns.article')" width="120" />
      <el-table-column prop="status" :label="$t('admin.columns.status')" width="120" />
      <el-table-column prop="createdAt" :label="$t('admin.columns.created')" width="170" />
      <el-table-column :label="$t('admin.columns.actions')" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="updateStatus(row, 'PUBLISHED')">{{ $t('admin.actions.approve') }}</el-button>
          <el-button size="small" @click="updateStatus(row, 'BLOCKED')">{{ $t('admin.actions.block') }}</el-button>
          <el-button size="small" type="danger" @click="deleteComment(row)">{{ $t('admin.actions.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import api from '@/api/client'
import type { Comment } from '@/api/types'

const { t } = useI18n()
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
    ElMessage.success(t('admin.messages.updated'))
    await loadComments()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.updateFailed'))
  }
}

const deleteComment = async (comment: Comment) => {
  await ElMessageBox.confirm(t('admin.confirms.deleteComment'), t('admin.confirms.confirm'), { type: 'warning' })
  try {
    await api.delete(`/api/admin/comments/${comment.id}`)
    ElMessage.success(t('admin.messages.deleted'))
    await loadComments()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.deleteFailed'))
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
