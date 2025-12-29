<template>
  <el-card shadow="hover" class="article-card" @click="goDetail">
    <div class="article-meta">
      <div>
        <h3>{{ article.title }}</h3>
        <p class="summary">{{ article.summary }}</p>
      </div>
      <el-tag size="small" class="status-tag">{{ article.visibility }}</el-tag>
    </div>
    <div class="article-info">
      <span>作者：{{ article.authorName }}</span>
      <span>发布时间：{{ formatTime(article.publishedAt || article.createdAt) }}</span>
      <span v-if="article.category">分类：{{ article.category }}</span>
    </div>
    <div class="article-tags">
      <el-tag v-for="tag in article.tags" :key="tag" size="small" type="info">{{ tag }}</el-tag>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import type { Article } from '../types';
import { useRouter } from 'vue-router';
import dayjs from 'dayjs';

const props = defineProps<{ article: Article }>();
const router = useRouter();

const formatTime = (value: string) => dayjs(value).format('YYYY-MM-DD HH:mm');
const goDetail = () => router.push(`/articles/${props.article.id}`);
</script>

<style scoped>
.article-card {
  cursor: pointer;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.summary {
  color: #6b7280;
  margin-top: 8px;
}

.article-info {
  margin-top: 12px;
  display: flex;
  gap: 16px;
  color: #64748b;
  font-size: 13px;
}

.article-tags {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}
</style>
