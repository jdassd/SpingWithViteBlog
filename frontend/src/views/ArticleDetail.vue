<template>
  <section class="container">
    <div v-if="loading" class="card-surface detail-card">
      <el-skeleton :rows="6" animated />
    </div>
    <div v-else-if="error" class="card-surface detail-card">
      <div class="section-title">{{ error }}</div>
      <el-button class="mt" @click="goBack">Back</el-button>
    </div>
    <div v-else class="detail-grid">
      <article class="card-surface detail-card">
        <div class="detail-meta">
          <span class="subtle-tag">{{ article?.contentType }}</span>
          <span class="muted">{{ formatDate(article?.publishedAt || article?.createdAt) }}</span>
        </div>
        <h1 class="detail-title serif">{{ article?.title }}</h1>
        <div class="detail-tags">
          <span v-for="tag in article?.tags || []" :key="tag" class="tag-pill">{{ tag }}</span>
        </div>
        <ContentRenderer :rendered-content="article?.renderedContent" />
      </article>
      <aside class="card-surface sidebar-card">
        <div class="section-title">Article Info</div>
        <div class="info-row">
          <span class="muted">Visibility</span>
          <span>{{ article?.visibility }}</span>
        </div>
        <div class="info-row">
          <span class="muted">Status</span>
          <span>{{ article?.status }}</span>
        </div>
        <div class="info-row">
          <span class="muted">Categories</span>
          <span>{{ (article?.categories || []).join(', ') || '-' }}</span>
        </div>
      </aside>
    </div>
    <section class="card-surface comment-card">
      <div class="section-title">Comments</div>
      <div class="comment-form">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="3"
          placeholder="Share your thoughts"
        />
        <div v-if="!auth.isAuthenticated && allowGuest">
          <el-input v-model="guestName" placeholder="Guest name" />
          <el-input v-model="guestEmail" placeholder="Guest email" />
        </div>
        <div class="comment-actions">
          <span v-if="replyTo" class="muted">Replying to #{{ replyTo.id }}</span>
          <div class="comment-buttons">
            <el-button v-if="replyTo" @click="cancelReply">Cancel Reply</el-button>
            <el-button type="primary" @click="submitComment">Submit</el-button>
          </div>
        </div>
      </div>
      <div class="comment-list">
        <CommentThread
          v-for="comment in comments"
          :key="comment.id"
          :comment="comment"
          :current-user-id="auth.user?.id"
          @reply="startReply"
          @delete="deleteComment"
        />
      </div>
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import api from '@/api/client'
import type { ArticleDetail, Comment } from '@/api/types'
import { useAuthStore } from '@/stores/auth'
import { useSiteStore } from '@/stores/site'
import ContentRenderer from '@/components/ContentRenderer.vue'
import CommentThread from '@/components/CommentThread.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const site = useSiteStore()

const loading = ref(true)
const error = ref('')
const article = ref<ArticleDetail | null>(null)
const comments = ref<Comment[]>([])

const commentContent = ref('')
const guestName = ref('')
const guestEmail = ref('')
const replyTo = ref<Comment | null>(null)
const allowGuest = ref(false)

const formatDate = (value?: string) => (value ? dayjs(value).format('YYYY-MM-DD') : '')

const loadArticle = async () => {
  loading.value = true
  error.value = ''
  try {
    article.value = await api.get<ArticleDetail>(`/api/public/articles/${route.params.id}`)
    await loadComments()
  } catch (err: any) {
    error.value = err?.message || 'Failed to load article'
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  comments.value = await api.get<Comment[]>(`/api/public/articles/${route.params.id}/comments`)
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('Comment content required')
    return
  }
  if (!auth.isAuthenticated && !allowGuest.value) {
    ElMessage.warning('Please log in to comment')
    return
  }
  const payload = {
    content: commentContent.value,
    parentId: replyTo.value?.id || null,
    guestName: auth.isAuthenticated ? undefined : guestName.value,
    guestEmail: auth.isAuthenticated ? undefined : guestEmail.value,
  }
  try {
    await api.post(`/api/public/articles/${route.params.id}/comments`, payload)
    commentContent.value = ''
    guestName.value = ''
    guestEmail.value = ''
    replyTo.value = null
    ElMessage.success('Comment submitted')
    await loadComments()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Failed to submit comment')
  }
}

const startReply = (comment: Comment) => {
  replyTo.value = comment
}

const cancelReply = () => {
  replyTo.value = null
}

const deleteComment = async (comment: Comment) => {
  try {
    await api.delete(`/api/public/comments/${comment.id}`)
    ElMessage.success('Comment deleted')
    await loadComments()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

const goBack = () => router.back()

onMounted(async () => {
  await site.loadPublic()
  allowGuest.value = site.settings.allow_guest_comment === 'true'
  await loadArticle()
})
</script>

<style scoped>
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 20px;
}

.detail-card {
  padding: 24px;
}

.detail-meta {
  display: flex;
  gap: 12px;
  align-items: center;
  font-size: 12px;
}

.detail-title {
  font-size: 34px;
  margin: 12px 0;
}

.detail-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.sidebar-card {
  padding: 18px;
  display: grid;
  gap: 12px;
  height: fit-content;
}

.info-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.comment-card {
  margin-top: 24px;
  padding: 20px;
  display: grid;
  gap: 16px;
}

.comment-form {
  display: grid;
  gap: 10px;
}

.comment-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-buttons {
  display: flex;
  gap: 8px;
}

.comment-list {
  display: grid;
  gap: 12px;
}

.mt {
  margin-top: 12px;
}

@media (max-width: 900px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .detail-title {
    font-size: 26px;
  }

  .detail-card {
    padding: 18px;
  }

  .comment-card {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .detail-title {
    font-size: 22px;
  }

  .detail-meta {
    flex-wrap: wrap;
  }

  .detail-tags {
    flex-wrap: wrap;
  }

  .comment-actions {
    flex-direction: column;
    gap: 8px;
    align-items: stretch;
  }

  .comment-buttons {
    justify-content: flex-end;
  }
}
</style>
