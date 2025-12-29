<template>
  <section class="container">
    <div class="card-surface editor-card">
      <div class="editor-head">
        <div>
          <span class="subtle-tag">Page</span>
          <h1 class="serif">{{ isEdit ? 'Edit Page' : 'New Page' }}</h1>
        </div>
        <div class="editor-actions">
          <el-button @click="goBack">Cancel</el-button>
          <el-button type="primary" @click="savePage">{{ isEdit ? 'Update' : 'Create' }}</el-button>
        </div>
      </div>
      <el-form :model="form" label-position="top">
        <el-form-item label="Title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="Slug">
          <el-input v-model="form.slug" />
        </el-form-item>
        <el-form-item label="External URL">
          <el-input v-model="form.externalUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item v-if="!form.externalUrl" label="Content Type">
          <el-radio-group v-model="form.contentType">
            <el-radio-button label="MARKDOWN">Markdown</el-radio-button>
            <el-radio-button label="RICH_TEXT">Rich Text</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="!form.externalUrl" label="Content">
          <div v-if="form.contentType === 'MARKDOWN'" class="editor-split">
            <el-input v-model="form.contentRaw" type="textarea" :autosize="{ minRows: 20, maxRows: 60 }" />
            <div class="preview" v-html="renderedMarkdown"></div>
          </div>
          <div v-else>
            <QuillEditor v-model:content="form.contentRaw" content-type="html" theme="snow" />
          </div>
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="Visibility">
            <el-select v-model="form.visibility">
              <el-option label="Public" value="PUBLIC" />
              <el-option label="Login only" value="LOGIN_ONLY" />
              <el-option label="Whitelist" value="WHITELIST" />
              <el-option label="Private" value="PRIVATE" />
              <el-option label="Admin only" value="ADMIN_ONLY" />
            </el-select>
          </el-form-item>
          <el-form-item label="Show in Navigation">
            <el-switch v-model="form.isNav" />
          </el-form-item>
          <el-form-item label="Sort Order">
            <el-input-number v-model="form.sortOrder" :min="0" />
          </el-form-item>
        </div>
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
import type { Page, User } from '@/api/types'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const users = ref<User[]>([])

const form = reactive({
  title: '',
  slug: '',
  contentType: 'MARKDOWN',
  contentRaw: '',
  visibility: 'PUBLIC',
  isNav: false,
  sortOrder: 0,
  externalUrl: '',
  whitelistUserIds: [] as number[],
})

const renderedMarkdown = computed(() => marked.parse(form.contentRaw || ''))

const loadUsers = async () => {
  users.value = await api.get<User[]>('/api/admin/users')
}

const loadPage = async () => {
  if (!isEdit.value) {
    return
  }
  const data = await api.get<Page>(`/api/admin/pages/${route.params.id}`)
  form.title = data.title
  form.slug = data.slug
  form.contentType = data.contentType || 'MARKDOWN'
  form.contentRaw = data.contentRaw || ''
  form.visibility = data.visibility
  form.isNav = !!data.isNav
  form.sortOrder = data.sortOrder || 0
  form.externalUrl = data.externalUrl || ''
  form.whitelistUserIds = data.whitelistUserIds || []
}

const savePage = async () => {
  if (!form.title.trim() || !form.slug.trim()) {
    ElMessage.warning('Title and slug required')
    return
  }
  const payload = { ...form }
  try {
    if (isEdit.value) {
      await api.put(`/api/admin/pages/${route.params.id}`, payload)
    } else {
      await api.post('/api/admin/pages', payload)
    }
    ElMessage.success('Saved')
    router.push('/admin/pages')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const goBack = () => router.back()

onMounted(async () => {
  await loadUsers()
  await loadPage()
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
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

@media (max-width: 900px) {
  .editor-split {
    grid-template-columns: 1fr;
  }
}
</style>
