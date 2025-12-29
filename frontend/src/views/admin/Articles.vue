<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <el-input v-model="filters.keyword" :placeholder="$t('admin.labels.keyword')" clearable />
      <el-select v-model="filters.status" :placeholder="$t('admin.columns.status')" clearable>
        <el-option :label="$t('admin.statusOptions.draft')" value="DRAFT" />
        <el-option :label="$t('admin.statusOptions.published')" value="PUBLISHED" />
        <el-option :label="$t('admin.statusOptions.archived')" value="ARCHIVED" />
      </el-select>
      <el-select v-model="filters.contentType" :placeholder="$t('admin.labels.contentType')" clearable>
        <el-option :label="$t('admin.labels.markdown')" value="MARKDOWN" />
        <el-option :label="$t('admin.labels.richText')" value="RICH_TEXT" />
      </el-select>
      <el-select v-model="filters.visibility" :placeholder="$t('admin.columns.visibility')" clearable>
        <el-option :label="$t('admin.visibilityOptions.public')" value="PUBLIC" />
        <el-option :label="$t('admin.visibilityOptions.loginOnly')" value="LOGIN_ONLY" />
        <el-option :label="$t('admin.visibilityOptions.whitelist')" value="WHITELIST" />
        <el-option :label="$t('admin.visibilityOptions.private')" value="PRIVATE" />
        <el-option :label="$t('admin.visibilityOptions.adminOnly')" value="ADMIN_ONLY" />
      </el-select>
      <el-select v-model="filters.authorId" :placeholder="$t('admin.columns.author')" clearable>
        <el-option v-for="user in users" :key="user.id" :label="user.username" :value="user.id" />
      </el-select>
      <el-button type="primary" @click="loadArticles">{{ $t('admin.filter') }}</el-button>
      <el-button @click="newArticle">{{ $t('admin.new') }}</el-button>
    </div>

    <el-table :data="articles" stripe>
      <el-table-column prop="title" :label="$t('admin.columns.title')" min-width="220" />
      <el-table-column prop="status" :label="$t('admin.columns.status')" width="120" />
      <el-table-column prop="visibility" :label="$t('admin.columns.visibility')" width="140" />
      <el-table-column prop="contentType" :label="$t('admin.columns.type')" width="120" />
      <el-table-column prop="createdAt" :label="$t('admin.columns.created')" width="170" />
      <el-table-column :label="$t('admin.columns.actions')" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="editArticle(row)">{{ $t('admin.actions.edit') }}</el-button>
          <el-button size="small" type="danger" @click="deleteArticle(row)">{{ $t('admin.actions.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import api from '@/api/client'
import type { ArticleSummary, User } from '@/api/types'

const { t } = useI18n()
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
  await ElMessageBox.confirm(t('admin.confirms.deleteArticle'), t('admin.confirms.confirm'), { type: 'warning' })
  try {
    await api.delete(`/api/admin/articles/${row.id}`)
    ElMessage.success(t('admin.messages.deleted'))
    await loadArticles()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.deleteFailed'))
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

@media (max-width: 640px) {
  .toolbar {
    grid-template-columns: 1fr;
    padding: 12px;
  }
}
</style>
