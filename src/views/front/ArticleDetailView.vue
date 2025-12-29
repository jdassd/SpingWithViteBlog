<template>
  <div class="page-container">
    <div v-if="!article" class="layout-main">
      <el-result icon="warning" title="文章不存在" sub-title="文章不存在或已被删除" />
    </div>
    <div v-else class="layout-main">
      <div class="article-header">
        <h1>{{ article.title }}</h1>
        <div class="article-meta">
          <span>作者：{{ article.authorName }}</span>
          <span>发布时间：{{ formatTime(article.publishedAt || article.createdAt) }}</span>
          <el-tag size="small">{{ article.visibility }}</el-tag>
        </div>
      </div>
      <div class="article-content" v-html="article.contentHtml"></div>
      <CommentThread :article-id="article.id" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import dayjs from 'dayjs';
import CommentThread from '../../components/CommentThread.vue';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const route = useRoute();
const router = useRouter();
const site = useSiteStore();
const auth = useAuthStore();

const article = computed(() => site.articles.find((item) => item.id === route.params.id));

onMounted(() => {
  if (!article.value) return;
  const canView = site.visibleArticles(auth.currentUser).some((item) => item.id === article.value?.id);
  if (!canView) {
    router.push(auth.isAuthed ? '/403' : '/401');
  }
});

const formatTime = (value: string) => dayjs(value).format('YYYY-MM-DD HH:mm');
</script>

<style scoped>
.article-header {
  margin-bottom: 24px;
}

.article-meta {
  display: flex;
  gap: 16px;
  color: #64748b;
  font-size: 14px;
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3) {
  margin-top: 24px;
}

.article-content :deep(pre) {
  background: #0f172a;
  color: #e2e8f0;
  padding: 16px;
  border-radius: 12px;
  overflow: auto;
}
</style>
