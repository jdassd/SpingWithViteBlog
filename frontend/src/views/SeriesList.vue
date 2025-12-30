<template>
  <div class="series-list-page">
    <h1 class="page-title">{{ $t('series.title') }}</h1>
    
    <div v-loading="loading">
      <el-empty v-if="!loading && seriesList.length === 0" :description="$t('series.noSeries')" />
      
      <div class="series-grid">
        <router-link
          v-for="series in seriesList"
          :key="series.id"
          :to="`/series/${series.id}`"
          class="series-card"
        >
          <div class="series-cover" v-if="series.coverUrl">
            <img :src="series.coverUrl" :alt="series.title" />
          </div>
          <div class="series-cover placeholder" v-else>
            <el-icon size="40"><Folder /></el-icon>
          </div>
          <div class="series-info">
            <div class="series-title">{{ series.title }}</div>
            <div class="series-desc" v-if="series.description">{{ series.description }}</div>
            <div class="series-meta">
              <el-tag size="small" :type="series.status === 'COMPLETED' ? 'success' : 'info'">
                {{ series.status === 'COMPLETED' ? $t('series.completed') : $t('series.ongoing') }}
              </el-tag>
              <span class="article-count">{{ series.articleCount || 0 }} {{ $t('series.articles') }}</span>
            </div>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/api/client'
import { Folder } from '@element-plus/icons-vue'
import type { Series } from '@/api/types'

const loading = ref(false)
const seriesList = ref<Series[]>([])

const loadSeries = async () => {
  loading.value = true
  try {
    const res = await api.get<Series[]>('/api/series')
    seriesList.value = res || []
  } catch (err) {
    console.error('Failed to load series:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSeries()
})
</script>

<style scoped>
.series-list-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #333;
}

.series-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.series-card {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  text-decoration: none;
  transition: all 0.2s;
}

.series-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.series-cover {
  width: 100%;
  height: 160px;
  overflow: hidden;
}

.series-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.series-cover.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: rgba(255, 255, 255, 0.8);
}

.series-info {
  padding: 16px;
}

.series-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.series-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.series-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.article-count {
  font-size: 13px;
  color: #999;
}
</style>
