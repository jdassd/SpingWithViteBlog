<template>
  <div class="ranking-page">
    <h1 class="page-title">{{ $t('ranking.title') }}</h1>
    
    <div class="ranking-list" v-loading="loading">
      <el-empty v-if="!loading && items.length === 0" :description="$t('ranking.noData')" />
      
      <div v-for="(item, index) in items" :key="item.articleId" class="ranking-item">
        <div class="rank-number" :class="{ 'top-3': index < 3 }">
          {{ index + 1 }}
        </div>
        <div class="item-cover" v-if="item.coverUrl">
          <img :src="item.coverUrl" :alt="item.title" />
        </div>
        <div class="item-info">
          <router-link :to="`/articles/${item.articleId}`" class="item-title">
            {{ item.title }}
          </router-link>
          <div class="item-stats">
            <span><el-icon><View /></el-icon> {{ item.viewCount }}</span>
            <span><el-icon><Star /></el-icon> {{ item.likeCount }}</span>
            <span><el-icon><ChatDotRound /></el-icon> {{ item.commentCount }}</span>
            <span><el-icon><Collection /></el-icon> {{ item.favoriteCount }}</span>
          </div>
        </div>
        <div class="item-score">
          <span class="score-label">{{ $t('ranking.score') }}</span>
          <span class="score-value">{{ item.score }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api/client'
import { View, Star, ChatDotRound, Collection } from '@element-plus/icons-vue'
import type { RankingItem } from '@/api/types'

const loading = ref(false)
const items = ref<RankingItem[]>([])

const loadRanking = async () => {
  loading.value = true
  try {
    const res = await api.get<RankingItem[]>('/api/ranking?limit=20')
    items.value = res || []
  } catch (err) {
    console.error('Failed to load ranking:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRanking()
})
</script>

<style scoped>
.ranking-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #333;
}

.ranking-list {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-item:hover {
  background: #fafafa;
}

.rank-number {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  color: #999;
  background: #f5f5f5;
  border-radius: 50%;
  margin-right: 16px;
}

.rank-number.top-3 {
  background: linear-gradient(135deg, #ffd700, #ffb347);
  color: #fff;
}

.item-cover {
  width: 60px;
  height: 45px;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 16px;
  flex-shrink: 0;
}

.item-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  text-decoration: none;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-title:hover {
  color: #409eff;
}

.item-stats {
  display: flex;
  gap: 16px;
  margin-top: 6px;
  font-size: 13px;
  color: #999;
}

.item-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.item-score {
  text-align: center;
  margin-left: 16px;
}

.score-label {
  display: block;
  font-size: 12px;
  color: #999;
}

.score-value {
  font-size: 20px;
  font-weight: 700;
  color: #ff6b6b;
}
</style>
