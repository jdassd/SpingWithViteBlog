<template>
  <div class="series-detail-page">
    <div v-loading="loading">
      <el-empty v-if="!loading && !series" :description="$t('series.notFound')" />
      
      <template v-if="series">
        <div class="series-header">
          <div class="series-cover" v-if="series.coverUrl">
            <img :src="series.coverUrl" :alt="series.title" />
          </div>
          <div class="series-info">
            <h1 class="series-title">{{ series.title }}</h1>
            <el-tag :type="series.status === 'COMPLETED' ? 'success' : 'info'" size="large">
              {{ series.status === 'COMPLETED' ? $t('series.completed') : $t('series.ongoing') }}
            </el-tag>
            <p class="series-desc" v-if="series.description">{{ series.description }}</p>
            <div class="series-stats">
              <span>{{ series.articleCount || articles.length }} {{ $t('series.articles') }}</span>
            </div>
          </div>
        </div>
        
        <div class="articles-section">
          <h2>{{ $t('series.articleList') }}</h2>
          <el-empty v-if="articles.length === 0" :description="$t('series.noArticles')" />
          
          <div class="article-list">
            <router-link
              v-for="(article, index) in articles"
              :key="article.id"
              :to="`/articles/${article.id}`"
              class="article-item"
            >
              <span class="article-order">{{ index + 1 }}</span>
              <div class="article-info">
                <div class="article-title">{{ article.title }}</div>
                <div class="article-summary" v-if="article.summary">{{ article.summary }}</div>
              </div>
              <div class="article-date" v-if="article.publishedAt">
                {{ formatDate(article.publishedAt) }}
              </div>
            </router-link>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '@/api/client'
import type { Series } from '@/api/types'

interface ArticleInSeries {
  id: number
  title: string
  summary?: string
  coverUrl?: string
  seriesOrder: number
  publishedAt?: string
}

const route = useRoute()
const loading = ref(false)
const series = ref<Series | null>(null)
const articles = ref<ArticleInSeries[]>([])

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString()
}

const loadSeriesDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const res = await api.get<{ success: boolean; data: Series & { articles: ArticleInSeries[] } }>(`/api/series/${id}`)
    if (res.success && res.data) {
      series.value = res.data
      articles.value = res.data.articles || []
    }
  } catch (err) {
    console.error('Failed to load series:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSeriesDetail()
})
</script>

<style scoped>
.series-detail-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.series-header {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.series-cover {
  width: 200px;
  height: 150px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.series-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.series-info {
  flex: 1;
}

.series-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 12px 0;
}

.series-desc {
  color: #666;
  line-height: 1.6;
  margin: 12px 0;
}

.series-stats {
  color: #999;
  font-size: 14px;
}

.articles-section h2 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.article-list {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.article-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  text-decoration: none;
  transition: background 0.2s;
}

.article-item:last-child {
  border-bottom: none;
}

.article-item:hover {
  background: #fafafa;
}

.article-order {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #667eea;
  color: #fff;
  border-radius: 50%;
  font-weight: 600;
  font-size: 14px;
  margin-right: 16px;
  flex-shrink: 0;
}

.article-info {
  flex: 1;
  min-width: 0;
}

.article-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.article-summary {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-date {
  color: #999;
  font-size: 13px;
  margin-left: 16px;
}

@media (max-width: 640px) {
  .series-header {
    flex-direction: column;
  }
  
  .series-cover {
    width: 100%;
    height: 200px;
  }
}
</style>
