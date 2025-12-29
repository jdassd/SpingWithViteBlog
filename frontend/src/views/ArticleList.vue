<template>
  <section class="container">
    <div class="list-hero card-surface">
      <div>
        <span class="subtle-tag">{{ $t('nav.articles') }}</span>
        <h1 class="serif">{{ $t('article.explore') }}</h1>
        <p class="muted">{{ $t('article.browseLatest') }}</p>
      </div>
      <div class="filters">
        <el-input v-model="keyword" :placeholder="$t('article.searchByTitle')" clearable />
        <el-select v-model="category" :placeholder="$t('article.category')" clearable>
          <el-option v-for="item in categories" :key="item.slug" :label="item.name" :value="item.slug" />
        </el-select>
        <el-select v-model="tag" :placeholder="$t('article.tag')" clearable>
          <el-option v-for="item in tags" :key="item.slug" :label="item.name" :value="item.slug" />
        </el-select>
        <el-button type="primary" @click="applySearch">{{ $t('common.search') }}</el-button>
      </div>
    </div>
    <div class="article-grid">
      <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
    </div>
    <div class="pager">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="totalEstimate"
        :page-size="pageSize"
        :current-page="page"
        @current-change="loadArticles"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import api from '@/api/client'
import type { ArticleSummary } from '@/api/types'
import ArticleCard from '@/components/ArticleCard.vue'

const articles = ref<ArticleSummary[]>([])
const page = ref(1)
const pageSize = 10
const totalEstimate = ref(120)
const keyword = ref('')
const category = ref('')
const tag = ref('')
const categories = ref<{ name: string; slug: string }[]>([])
const tags = ref<{ name: string; slug: string }[]>([])

const loadArticles = async (newPage?: number) => {
  if (newPage) {
    page.value = newPage
  }
  if (keyword.value.trim()) {
    articles.value = await api.get<ArticleSummary[]>('/api/public/search/articles', {
      params: { keyword: keyword.value, page: page.value, size: pageSize },
    })
    return
  }
  articles.value = await api.get<ArticleSummary[]>('/api/public/articles', {
    params: {
      page: page.value,
      size: pageSize,
      category: category.value || undefined,
      tag: tag.value || undefined,
    },
  })
}

const applySearch = () => {
  page.value = 1
  loadArticles()
}

const loadTaxonomy = async () => {
  categories.value = await api.get('/api/public/taxonomy/categories')
  tags.value = await api.get('/api/public/taxonomy/tags')
}

onMounted(async () => {
  await loadTaxonomy()
  await loadArticles()
})
</script>

<style scoped>
.list-hero {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.article-grid {
  display: grid;
  gap: 16px;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .list-hero {
    flex-direction: column;
    align-items: stretch;
    gap: 14px;
  }

  .filters {
    flex-direction: column;
  }

  .filters .el-input,
  .filters .el-select,
  .filters .el-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .list-hero {
    padding: 18px;
  }

  .article-grid {
    gap: 12px;
  }
}
</style>
