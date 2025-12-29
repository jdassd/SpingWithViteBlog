<template>
  <section class="container">
    <div class="card-surface editor-card">
      <div class="editor-head">
        <div>
          <span class="subtle-tag">Admin Editor</span>
          <h1 class="serif">{{ isEdit ? 'Edit Article' : 'New Article' }}</h1>
        </div>
        <div class="editor-actions">
          <el-button @click="goBack">Cancel</el-button>
          <el-button type="primary" @click="saveArticle">{{ isEdit ? 'Update' : 'Publish' }}</el-button>
        </div>
      </div>
      <el-form :model="form" label-position="top">
        <el-form-item label="Title">
          <el-input v-model="form.title" placeholder="Enter title" />
        </el-form-item>
        <el-form-item label="Content Type">
          <el-radio-group v-model="form.contentType">
            <el-radio-button label="MARKDOWN">Markdown</el-radio-button>
            <el-radio-button label="RICH_TEXT">Rich Text</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Content">
          <div v-if="form.contentType === 'MARKDOWN'" class="editor-split">
            <el-input v-model="form.contentRaw" type="textarea" :rows="12" />
            <div class="preview" v-html="renderedMarkdown"></div>
          </div>
          <div v-else>
            <QuillEditor v-model:content="form.contentRaw" content-type="html" theme="snow" />
          </div>
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="Visibility">
            <el-select v-model="form.visibility" placeholder="Select visibility">
              <el-option v-for="option in visibilityOptions" :key="option" :label="option" :value="option" />
            </el-select>
          </el-form-item>
          <el-form-item label="Status">
            <el-select v-model="form.status">
              <el-option label="Draft" value="DRAFT" />
              <el-option label="Published" value="PUBLISHED" />
              <el-option label="Archived" value="ARCHIVED" />
            </el-select>
          </el-form-item>
          <el-form-item label="RSS Enabled">
            <el-switch v-model="form.rssEnabled" />
          </el-form-item>
          <el-form-item label="Allow Index">
            <el-switch v-model="form.allowIndex" />
          </el-form-item>
        </div>
        <el-form-item label="Categories">
          <el-select v-model="form.categories" multiple filterable allow-create default-first-option />
        </el-form-item>
        <el-form-item label="Tags">
          <el-select v-model="form.tags" multiple filterable allow-create default-first-option />
        </el-form-item>
        <el-form-item label="Summary">
          <el-input v-model="form.summary" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="Cover URL">
          <el-input v-model="form.coverUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item v-if="form.visibility === 'WHITELIST'" label="Whitelist users">
          <el-select v-model="form.whitelistUserIds" multiple filterable>
            <el-option v-for="user in users" :key="user.id" :label="user.username" :value="user.id" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import api from '@/api/client'
import type { ArticleEdit, User } from '@/api/types'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const users = ref<User[]>([])
const visibilityOptions = ['PUBLIC', 'LOGIN_ONLY', 'WHITELIST', 'PRIVATE', 'ADMIN_ONLY']

const form = reactive({
  title: '',
  contentType: 'MARKDOWN',
  contentRaw: '',
  visibility: 'PUBLIC',
  status: 'DRAFT',
  rssEnabled: true,
  allowIndex: true,
  categories: [] as string[],
  tags: [] as string[],
  summary: '',
  coverUrl: '',
  whitelistUserIds: [] as number[],
})

const renderedMarkdown = computed(() => marked.parse(form.contentRaw || ''))

const loadUsers = async () => {
  users.value = await api.get<User[]>('/api/admin/users')
}

const loadArticle = async () => {
  if (!isEdit.value) {
    return
  }
  const data = await api.get<ArticleEdit>(`/api/admin/articles/${route.params.id}`)
  form.title = data.title
  form.contentType = data.contentType
  form.contentRaw = data.contentRaw
  form.visibility = data.visibility
  form.status = data.status
  form.rssEnabled = data.rssEnabled ?? true
  form.allowIndex = data.allowIndex ?? true
  form.categories = data.categories || []
  form.tags = data.tags || []
  form.summary = data.summary || ''
  form.coverUrl = data.coverUrl || ''
  form.whitelistUserIds = data.whitelistUserIds || []
}

const saveArticle = async () => {
  if (!form.title.trim()) {
    ElMessage.warning('Title required')
    return
  }
  const payload = {
    ...form,
  }
  try {
    if (isEdit.value) {
      await api.put(`/api/admin/articles/${route.params.id}`, payload)
    } else {
      await api.post('/api/admin/articles', payload)
    }
    ElMessage.success('Saved')
    router.push('/admin/articles')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const goBack = () => router.back()

onMounted(async () => {
  await loadUsers()
  await loadArticle()
})
</script>

<style scoped>
.editor-card {
  padding: 24px;
  display: grid;
  gap: 16px;
}

.editor-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.editor-actions {
  display: flex;
  gap: 8px;
}

.editor-split {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.preview {
  background: var(--surface-muted);
  border-radius: 12px;
  padding: 16px;
  overflow: auto;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

@media (max-width: 900px) {
  .editor-split {
    grid-template-columns: 1fr;
  }
}
</style>
