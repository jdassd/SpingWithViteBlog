<template>
  <div class="page-container">
    <div class="layout-main">
      <div class="section-title">文章列表</div>
      <div class="filters">
        <el-input v-model="filters.keyword" placeholder="标题/摘要关键词" clearable />
        <el-select v-model="filters.category" placeholder="分类" clearable>
          <el-option v-for="category in categories" :key="category" :label="category" :value="category" />
        </el-select>
        <el-select v-model="filters.tag" placeholder="标签" clearable>
          <el-option v-for="tag in tags" :key="tag" :label="tag" :value="tag" />
        </el-select>
        <el-button type="primary" @click="applyFilters">筛选</el-button>
      </div>
      <div class="card-grid">
        <ArticleCard v-for="article in filteredArticles" :key="article.id" :article="article" />
      </div>
      <el-empty v-if="filteredArticles.length === 0" description="没有符合条件的文章" class="empty-state" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue';
import { useRoute } from 'vue-router';
import ArticleCard from '../../components/ArticleCard.vue';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const site = useSiteStore();
const auth = useAuthStore();
const route = useRoute();

const filters = reactive({
  keyword: (route.query.q as string) || '',
  category: '',
  tag: '',
});

const articles = computed(() => site.visibleArticles(auth.currentUser));

const categories = computed(() =>
  Array.from(new Set(articles.value.map((item) => item.category).filter(Boolean))) as string[]
);
const tags = computed(() => Array.from(new Set(articles.value.flatMap((item) => item.tags))));

const filteredArticles = computed(() => {
  return articles.value.filter((item) => {
    if (filters.keyword && !(item.title.includes(filters.keyword) || item.summary.includes(filters.keyword))) {
      return false;
    }
    if (filters.category && item.category !== filters.category) return false;
    if (filters.tag && !item.tags.includes(filters.tag)) return false;
    return true;
  });
});

const applyFilters = () => {
  return filteredArticles.value;
};
</script>

<style scoped>
.filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}
</style>
