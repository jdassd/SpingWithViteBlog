<template>
  <article class="article-card card-surface">
    <div class="article-meta">
      <span class="subtle-tag">{{ article.contentType }}</span>
      <span class="muted">{{ formatDate(article.publishedAt || article.createdAt) }}</span>
    </div>
    <h3 class="article-title serif">
      <RouterLink :to="`/articles/${article.id}`">{{ article.title }}</RouterLink>
    </h3>
    <p class="article-summary">{{ article.summary }}</p>
    <div class="article-tags">
      <span v-for="tag in article.tags || []" :key="tag" class="tag-pill">{{ tag }}</span>
    </div>
  </article>
</template>

<script setup lang="ts">
import type { ArticleSummary } from '@/api/types'
import dayjs from 'dayjs'

defineProps<{ article: ArticleSummary }>()

const formatDate = (value?: string) => (value ? dayjs(value).format('YYYY-MM-DD') : '')
</script>

<style scoped>
.article-card {
  padding: 20px;
  display: grid;
  gap: 14px;
}

.article-meta {
  display: flex;
  gap: 12px;
  align-items: center;
  font-size: 12px;
}

.article-title {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: -0.01em;
  line-height: 1.3;
}

.article-summary {
  color: var(--ink-muted);
  line-height: 1.6;
  font-size: 14px;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-pill {
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 12px;
  background: rgba(15, 118, 110, 0.12);
  color: var(--accent);
}
</style>
