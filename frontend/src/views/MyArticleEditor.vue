<template>
  <section class="container">
    <div class="card-surface editor-card">
      <div class="editor-head">
        <div>
          <span class="subtle-tag">{{ $t('myArticle.title') }}</span>
          <h1 class="serif">{{ isEdit ? $t('myArticle.editArticle') : $t('myArticle.newArticle') }}</h1>
        </div>
        <div class="editor-actions">
          <el-button @click="goBack">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="saveArticle">{{ isEdit ? $t('myArticle.update') : $t('myArticle.publish') }}</el-button>
        </div>
      </div>
      <el-form :model="form" label-position="top">
        <el-form-item :label="$t('common.title')">
          <el-input v-model="form.title" :placeholder="$t('common.title')" />
        </el-form-item>
        <el-form-item :label="$t('myArticle.contentType')">
          <el-radio-group v-model="form.contentType">
            <el-radio-button label="MARKDOWN">{{ $t('myArticle.markdown') }}</el-radio-button>
            <el-radio-button label="RICH_TEXT">{{ $t('myArticle.richText') }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('common.content')">
          <div v-if="form.contentType === 'MARKDOWN'" class="editor-split">
            <el-input v-model="form.contentRaw" type="textarea" :autosize="{ minRows: 20, maxRows: 60 }" />
            <div class="preview" v-html="renderedMarkdown"></div>
          </div>
          <div v-else>
            <QuillEditor v-model:content="form.contentRaw" content-type="html" theme="snow" />
          </div>
        </el-form-item>
        <div class="form-grid">
          <el-form-item :label="$t('myArticle.visibility')">
            <el-select v-model="form.visibility" :placeholder="$t('myArticle.visibility')">
              <el-option :label="$t('myArticle.visibilityOptions.public')" value="PUBLIC" />
              <el-option :label="$t('myArticle.visibilityOptions.loginOnly')" value="LOGIN_ONLY" />
              <el-option :label="$t('myArticle.visibilityOptions.whitelist')" value="WHITELIST" />
              <el-option :label="$t('myArticle.visibilityOptions.private')" value="PRIVATE" />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('common.status')">
            <el-select v-model="form.status">
              <el-option :label="$t('myArticle.statusOptions.draft')" value="DRAFT" />
              <el-option :label="$t('myArticle.statusOptions.published')" value="PUBLISHED" />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('myArticle.rssEnabled')">
            <el-switch v-model="form.rssEnabled" />
          </el-form-item>
          <el-form-item :label="$t('myArticle.allowIndex')">
            <el-switch v-model="form.allowIndex" />
          </el-form-item>
        </div>
        <el-form-item :label="$t('myArticle.categories')">
          <el-select v-model="form.categories" multiple filterable allow-create default-first-option />
        </el-form-item>
        <el-form-item :label="$t('myArticle.tags')">
          <el-select v-model="form.tags" multiple filterable allow-create default-first-option />
        </el-form-item>
        <el-form-item :label="$t('myArticle.summary')">
          <el-input v-model="form.summary" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item :label="$t('myArticle.coverUrl')">
          <el-input v-model="form.coverUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item v-if="form.visibility === 'WHITELIST'" :label="$t('myArticle.whitelistUserIds')">
          <el-input v-model="whitelistInput" :placeholder="$t('myArticle.whitelistUserIds')" />
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
import type { ArticleEdit } from '@/api/types'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

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
})

const whitelistInput = ref('')

const renderedMarkdown = computed(() => marked.parse(form.contentRaw || ''))

const loadArticle = async () => {
  if (!isEdit.value) {
    return
  }
  const data = await api.get<ArticleEdit>(`/api/user/articles/${route.params.id}`)
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
  if (data.whitelistUserIds?.length) {
    whitelistInput.value = data.whitelistUserIds.join(',')
  }
}

const saveArticle = async () => {
  if (!form.title.trim()) {
    ElMessage.warning(t('myArticle.titleRequired'))
    return
  }
  const whitelistUserIds =
    form.visibility === 'WHITELIST'
      ? whitelistInput.value
          .split(',')
          .map((id) => parseInt(id.trim(), 10))
          .filter((id) => !Number.isNaN(id))
      : []
  const payload = {
    ...form,
    whitelistUserIds,
  }
  try {
    if (isEdit.value) {
      await api.put(`/api/user/articles/${route.params.id}`, payload)
    } else {
      await api.post('/api/user/articles', payload)
    }
    ElMessage.success(t('myArticle.saved'))
    router.push('/me/articles')
  } catch (err: any) {
    ElMessage.error(err?.message || t('common.error'))
  }
}

const goBack = () => router.back()

onMounted(loadArticle)
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
