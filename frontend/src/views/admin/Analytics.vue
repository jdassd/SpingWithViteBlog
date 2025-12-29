<template>
  <div class="admin-analytics">
    <h1>{{ $t('menu.analytics') }}</h1>
    
    <!-- Overview Cards -->
    <div class="stats-cards" v-loading="loading">
      <div class="stat-card">
        <div class="stat-title">{{ $t('analytics.todayPv') }}</div>
        <div class="stat-value">{{ overview.todayPv }}</div>
        <div class="stat-compare">
          {{ $t('analytics.yesterday') }}: {{ overview.yesterdayPv }}
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-title">{{ $t('analytics.todayUv') }}</div>
        <div class="stat-value">{{ overview.todayUv }}</div>
        <div class="stat-compare">
          {{ $t('analytics.yesterday') }}: {{ overview.yesterdayUv }}
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-title">{{ $t('analytics.weekPv') }}</div>
        <div class="stat-value">{{ overview.weekPv }}</div>
        <div class="stat-compare">
          {{ $t('analytics.lastWeek') }}: {{ overview.lastWeekPv }}
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-title">{{ $t('analytics.monthPv') }}</div>
        <div class="stat-value">{{ overview.monthPv }}</div>
        <div class="stat-compare">
          {{ $t('analytics.lastMonth') }}: {{ overview.lastMonthPv }}
        </div>
      </div>
    </div>
    
    <!-- Top Articles -->
    <el-card class="top-articles-card">
      <template #header>
        <div class="card-header">
          <span>{{ $t('analytics.topArticles') }}</span>
          <el-select v-model="topDays" style="width: 120px" @change="loadTopArticles">
            <el-option :value="7" :label="$t('analytics.last7Days')" />
            <el-option :value="30" :label="$t('analytics.last30Days')" />
            <el-option :value="90" :label="$t('analytics.last90Days')" />
          </el-select>
        </div>
      </template>
      <el-table :data="topArticles" v-loading="topLoading">
        <el-table-column type="index" width="60" label="#" />
        <el-table-column prop="article_id" label="ID" width="80" />
        <el-table-column prop="count" :label="$t('analytics.views')" width="100" />
      </el-table>
    </el-card>
    
    <!-- Ranking Config -->
    <el-card class="ranking-config-card">
      <template #header>{{ $t('analytics.rankingConfig') }}</template>
      <el-form :model="rankingConfig" label-width="120px" style="max-width: 400px">
        <el-form-item :label="$t('analytics.viewWeight')">
          <el-input-number v-model="rankingConfig.viewWeight" :min="0" :max="100" />
        </el-form-item>
        <el-form-item :label="$t('analytics.likeWeight')">
          <el-input-number v-model="rankingConfig.likeWeight" :min="0" :max="100" />
        </el-form-item>
        <el-form-item :label="$t('analytics.commentWeight')">
          <el-input-number v-model="rankingConfig.commentWeight" :min="0" :max="100" />
        </el-form-item>
        <el-form-item :label="$t('analytics.favoriteWeight')">
          <el-input-number v-model="rankingConfig.favoriteWeight" :min="0" :max="100" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveRankingConfig">{{ $t('common.save') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import api from '@/api/client'
import { ElMessage } from 'element-plus'
import type { Analytics, RankingConfig } from '@/api/types'

const loading = ref(false)
const topLoading = ref(false)
const topDays = ref(7)

const overview = reactive<Analytics>({
  todayPv: 0,
  todayUv: 0,
  yesterdayPv: 0,
  yesterdayUv: 0,
  weekPv: 0,
  lastWeekPv: 0,
  monthPv: 0,
  lastMonthPv: 0
})

const topArticles = ref<{ article_id: number; count: number }[]>([])

const rankingConfig = reactive<RankingConfig>({
  viewWeight: 1,
  likeWeight: 3,
  commentWeight: 5,
  favoriteWeight: 2
})

const loadOverview = async () => {
  loading.value = true
  try {
    const res = await api.get<{ success: boolean; data: Analytics }>('/api/admin/analytics/overview')
    if (res.success && res.data) {
      Object.assign(overview, res.data)
    }
  } catch (err) {
    console.error('Failed to load overview:', err)
  } finally {
    loading.value = false
  }
}

const loadTopArticles = async () => {
  topLoading.value = true
  try {
    const res = await api.get<{ success: boolean; data: any[] }>(`/api/admin/analytics/top-articles?days=${topDays.value}&limit=10`)
    if (res.success) {
      topArticles.value = res.data || []
    }
  } catch (err) {
    console.error('Failed to load top articles:', err)
  } finally {
    topLoading.value = false
  }
}

const loadRankingConfig = async () => {
  try {
    const res = await api.get<{ success: boolean; data: RankingConfig }>('/api/admin/analytics/ranking-config')
    if (res.success && res.data) {
      Object.assign(rankingConfig, res.data)
    }
  } catch (err) {
    console.error('Failed to load ranking config:', err)
  }
}

const saveRankingConfig = async () => {
  try {
    await api.put('/api/admin/analytics/ranking-config', rankingConfig)
    ElMessage.success('保存成功')
  } catch (err) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadOverview()
  loadTopArticles()
  loadRankingConfig()
})
</script>

<style scoped>
.admin-analytics {
  padding: 20px;
}

.admin-analytics h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 24px 0;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stat-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #333;
}

.stat-compare {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.top-articles-card,
.ranking-config-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
