<template>
  <section class="container">
    <div class="search-hero card-surface">
      <div>
        <span class="subtle-tag">Search</span>
        <h1 class="serif">Find an article</h1>
      </div>
      <div class="search-box">
        <el-input v-model="keyword" placeholder="Search articles" @keyup.enter="runSearch" />
        <el-button type="primary" @click="runSearch">Search</el-button>
      </div>
    </div>
    <div v-if="results.length" class="results">
      <ArticleCard v-for="article in results" :key="article.id" :article="article" />
    </div>
    <el-empty v-else description="No articles matched" />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api/client'
import type { ArticleSummary } from '@/api/types'
import ArticleCard from '@/components/ArticleCard.vue'

const route = useRoute()
const router = useRouter()
const keyword = ref((route.query.q as string) || '')
const results = ref<ArticleSummary[]>([])

const runSearch = async () => {
  if (!keyword.value.trim()) {
    results.value = []
    return
  }
  results.value = await api.get<ArticleSummary[]>('/api/public/search/articles', {
    params: { keyword: keyword.value, page: 1, size: 20 },
  })
  router.replace({ query: { q: keyword.value } })
}

watch(
  () => route.query.q,
  (value) => {
    if (typeof value === 'string') {
      keyword.value = value
      runSearch()
    }
  }
)

onMounted(runSearch)
</script>

<style scoped>
.search-hero {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.results {
  display: grid;
  gap: 16px;
}
</style>
