<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <el-input v-model="filters.keyword" placeholder="Keyword" clearable />
      <el-select v-model="filters.status" placeholder="Status" clearable>
        <el-option label="Draft" value="DRAFT" />
        <el-option label="Published" value="PUBLISHED" />
        <el-option label="Archived" value="ARCHIVED" />
      </el-select>
      <el-select v-model="filters.contentType" placeholder="Content type" clearable>
        <el-option label="Markdown" value="MARKDOWN" />
        <el-option label="Rich Text" value="RICH_TEXT" />
      </el-select>
      <el-select v-model="filters.visibility" placeholder="Visibility" clearable>
        <el-option label="Public" value="PUBLIC" />
        <el-option label="Login only" value="LOGIN_ONLY" />
        <el-option label="Whitelist" value="WHITELIST" />
        <el-option label="Private" value="PRIVATE" />
        <el-option label="Admin only" value="ADMIN_ONLY" />
      </el-select>
      <el-select v-model="filters.authorId" placeholder="Author" clearable>
        <el-option v-for="user in users" :key="user.id" :label="user.username" :value="user.id" />
      </el-select>
      <el-button type="primary" @click="loadArticles">Filter</el-button>
      <el-button @click="newArticle">New</el-button>
    </div>

    <el-table :data="articles" stripe>
      <el-table-column prop="title" label="Title" min-width="220" />
      <el-table-column prop="status" label="Status" width="120" />
      <el-table-column prop="visibility" label="Visibility" width="140" />
      <el-table-column prop="contentType" label="Type" width="120" />
      <el-table-column prop="createdAt" label="Created" width="170" />
      <el-table-column label="Actions" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="editArticle(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="deleteArticle(row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api/client'
import type { ArticleSummary, User } from '@/api/types'

const router = useRouter()
const articles = ref<ArticleSummary[]>([])
const users = ref<User[]>([])

const filters = reactive({
  keyword: '',
  status: '',
  contentType: '',
  visibility: '',
  authorId: null as number | null,
})

const loadArticles = async () => {
  articles.value = await api.get<ArticleSummary[]>('/api/admin/articles', {
    params: {
      keyword: filters.keyword || undefined,
      status: filters.status || undefined,
      contentType: filters.contentType || undefined,
      visibility: filters.visibility || undefined,
      authorId: filters.authorId || undefined,
      page: 1,
      size: 50,
    },
  })
}

const loadUsers = async () => {
  users.value = await api.get<User[]>('/api/admin/users')
}

const newArticle = () => router.push('/admin/articles/new')

const editArticle = (row: ArticleSummary) => router.push(`/admin/articles/${row.id}`)

const deleteArticle = async (row: ArticleSummary) => {
  await ElMessageBox.confirm('Delete this article?', 'Confirm', { type: 'warning' })
  try {
    await api.delete(`/api/admin/articles/${row.id}`)
    ElMessage.success('Deleted')
    await loadArticles()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

onMounted(async () => {
  await loadUsers()
  await loadArticles()
})
</script>

<style scoped>
.admin-section {
  display: grid;
  gap: 16px;
}

.toolbar {
  padding: 16px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
}
</style>
