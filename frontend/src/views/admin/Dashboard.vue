<template>
  <section class="dashboard-grid">
    <div class="stats-grid">
      <div class="stat-card card-surface">
        <div class="stat-title">{{ $t('admin.dashboardStats.articles') }}</div>
        <div class="stat-value">{{ stats?.articlesTotal }}</div>
        <div class="muted">{{ $t('admin.dashboardStats.published') }}: {{ stats?.articlesPublished }} | {{ $t('admin.dashboardStats.draft') }}: {{ stats?.articlesDraft }}</div>
      </div>
      <div class="stat-card card-surface">
        <div class="stat-title">{{ $t('admin.dashboardStats.comments') }}</div>
        <div class="stat-value">{{ stats?.commentsTotal }}</div>
        <div class="muted">{{ $t('admin.dashboardStats.pending') }}: {{ stats?.commentsPending }} | {{ $t('admin.dashboardStats.blocked') }}: {{ stats?.commentsBlocked }}</div>
      </div>
      <div class="stat-card card-surface">
        <div class="stat-title">{{ $t('admin.dashboardStats.users') }}</div>
        <div class="stat-value">{{ stats?.usersTotal }}</div>
        <div class="muted">{{ $t('admin.dashboardStats.albums') }}: {{ stats?.albumsTotal }} | {{ $t('admin.dashboardStats.photos') }}: {{ stats?.photosTotal }}</div>
      </div>
    </div>
    <div class="panel-grid">
      <div class="panel card-surface">
        <div class="section-title">{{ $t('admin.recentArticles') }}</div>
        <div v-for="article in stats?.recentArticles || []" :key="article.id" class="panel-row">
          <span>{{ article.title }}</span>
          <span class="muted">{{ article.status }}</span>
        </div>
      </div>
      <div class="panel card-surface">
        <div class="section-title">{{ $t('admin.recentComments') }}</div>
        <div v-for="comment in stats?.recentComments || []" :key="comment.id" class="panel-row">
          <span>{{ comment.content }}</span>
          <span class="muted">{{ comment.status }}</span>
        </div>
      </div>
      <div class="panel card-surface">
        <div class="section-title">{{ $t('admin.auditTrail') }}</div>
        <div v-for="log in stats?.recentAudits || []" :key="log.id" class="panel-row">
          <span>{{ log.action }}</span>
          <span class="muted">{{ log.createdAt?.slice(0, 16).replace('T', ' ') }}</span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import api from '@/api/client'
import type { DashboardStats } from '@/api/types'

const stats = ref<DashboardStats | null>(null)

const loadStats = async () => {
  stats.value = await api.get<DashboardStats>('/api/admin/dashboard')
}

onMounted(loadStats)
</script>

<style scoped>
.dashboard-grid {
  display: grid;
  gap: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.stat-card {
  padding: 18px;
  display: grid;
  gap: 8px;
}

.stat-title {
  font-weight: 600;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.panel-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 16px;
}

.panel {
  padding: 18px;
  display: grid;
  gap: 10px;
}

.panel-row {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  font-size: 13px;
}
</style>
