<template>
  <section class="container">
    <div class="list-hero card-surface">
      <div>
        <span class="subtle-tag">My Articles</span>
        <h1 class="serif">Your drafts and posts</h1>
      </div>
      <div class="actions">
        <el-button type="primary" @click="newArticle">New Article</el-button>
      </div>
    </div>
    <el-table :data="articles" stripe>
      <el-table-column prop="title" label="Title" min-width="220" />
      <el-table-column prop="status" label="Status" width="120" />
      <el-table-column prop="visibility" label="Visibility" width="140" />
      <el-table-column prop="createdAt" label="Created" width="160" />
      <el-table-column label="Actions" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="editArticle(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="deleteArticle(row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api/client'
import type { ArticleSummary } from '@/api/types'

const router = useRouter()
const articles = ref<ArticleSummary[]>([])

const loadArticles = async () => {
  articles.value = await api.get<ArticleSummary[]>('/api/user/articles', {
    params: { page: 1, size: 50 },
  })
}

const newArticle = () => router.push('/me/articles/new')

const editArticle = (row: ArticleSummary) => router.push(`/me/articles/${row.id}`)

const deleteArticle = async (row: ArticleSummary) => {
  await ElMessageBox.confirm('Delete this article?', 'Confirm', { type: 'warning' })
  try {
    await api.delete(`/api/user/articles/${row.id}`)
    ElMessage.success('Deleted')
    await loadArticles()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

onMounted(loadArticles)
</script>

<style scoped>
.list-hero {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
</style>
