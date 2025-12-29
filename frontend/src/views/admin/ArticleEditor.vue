<template>
  <section class="container">
    <div class="card-surface editor-card">
      <div class="editor-head">
        <div>
          <span class="subtle-tag">{{ $t('admin.editor.adminEditor') }}</span>
          <h1 class="serif">{{ isEdit ? $t('admin.editor.editArticle') : $t('admin.editor.newArticle') }}</h1>
        </div>
        <div class="editor-actions">
          <el-button @click="goBack">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="saveArticle">{{ isEdit ? $t('admin.actions.update') : $t('admin.actions.publish') }}</el-button>
        </div>
      </div>
      <el-form :model="form" label-position="top">
        <el-form-item :label="$t('admin.columns.title')">
          <el-input v-model="form.title" :placeholder="$t('admin.editor.enterTitle')" />
        </el-form-item>
        <el-form-item :label="$t('admin.labels.contentType')">
          <el-radio-group v-model="form.contentType">
            <el-radio-button label="MARKDOWN">{{ $t('admin.labels.markdown') }}</el-radio-button>
            <el-radio-button label="RICH_TEXT">{{ $t('admin.labels.richText') }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('admin.editor.content')">
          <div v-if="form.contentType === 'MARKDOWN'" class="editor-split">
            <el-input v-model="form.contentRaw" type="textarea" :autosize="{ minRows: 20, maxRows: 60 }" />
            <div class="preview" v-html="renderedMarkdown"></div>
          </div>
          <div v-else>
            <QuillEditor v-model:content="form.contentRaw" content-type="html" theme="snow" />
          </div>
        </el-form-item>
        <div class="form-grid">
          <el-form-item :label="$t('admin.labels.visibility')">
            <el-select v-model="form.visibility" :placeholder="$t('admin.labels.visibility')">
              <el-option v-for="option in visibilityOptions" :key="option" :label="getVisibilityLabel(option)" :value="option" />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('admin.columns.status')">
            <el-select v-model="form.status">
              <el-option :label="$t('admin.statusOptions.draft')" value="DRAFT" />
              <el-option :label="$t('admin.statusOptions.published')" value="PUBLISHED" />
              <el-option :label="$t('admin.statusOptions.archived')" value="ARCHIVED" />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('admin.labels.rssEnabled')">
            <el-switch v-model="form.rssEnabled" />
          </el-form-item>
          <el-form-item :label="$t('admin.labels.allowIndex')">
            <el-switch v-model="form.allowIndex" />
          </el-form-item>
        </div>
        <el-form-item :label="$t('admin.labels.categories')">
          <el-select v-model="form.categories" multiple filterable allow-create default-first-option />
        </el-form-item>
        <el-form-item :label="$t('admin.labels.tags')">
          <el-select v-model="form.tags" multiple filterable allow-create default-first-option />
        </el-form-item>
        <el-form-item :label="$t('admin.labels.summary')">
          <el-input v-model="form.summary" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item :label="$t('admin.labels.coverUrl')">
          <el-input v-model="form.coverUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item v-if="form.visibility === 'WHITELIST'" :label="$t('admin.labels.whitelistUsers')">
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
import { useI18n } from 'vue-i18n'
import { marked } from 'marked'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import api from '@/api/client'
import type { ArticleEdit, User } from '@/api/types'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const users = ref<User[]>([])
const visibilityOptions = ['PUBLIC', 'LOGIN_ONLY', 'WHITELIST', 'PRIVATE', 'ADMIN_ONLY']

const getVisibilityLabel = (option: string) => {
  const map: Record<string, string> = {
    PUBLIC: t('admin.visibilityOptions.public'),
    LOGIN_ONLY: t('admin.visibilityOptions.loginOnly'),
    WHITELIST: t('admin.visibilityOptions.whitelist'),
    PRIVATE: t('admin.visibilityOptions.private'),
    ADMIN_ONLY: t('admin.visibilityOptions.adminOnly'),
  }
  return map[option] || option
}

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
    ElMessage.warning(t('admin.messages.titleRequired'))
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
    ElMessage.success(t('admin.messages.saved'))
    router.push('/admin/articles')
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.saveFailed'))
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
  min-height: 460px;
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
