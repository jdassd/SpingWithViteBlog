<template>
  <section class="container">
    <div v-if="homeMode === 'NAV'" class="home-portal">
      <div class="portal-hero card-surface">
        <div class="portal-title serif">{{ siteName }}</div>
        <p class="muted">{{ siteDescription }}</p>
        <div class="portal-search">
          <el-input v-model="searchKeyword" placeholder="Search the web or site">
            <template #append>
              <el-select v-model="selectedEngine" placeholder="Engine" style="width: 140px">
                <el-option
                  v-for="engine in searchEngines"
                  :key="engine.id"
                  :label="engine.name"
                  :value="engine.id"
                />
              </el-select>
            </template>
          </el-input>
          <el-button type="primary" @click="performSearch">Search</el-button>
        </div>
      </div>
      <div class="portal-groups">
        <div v-for="group in navGroups" :key="group.id" class="portal-group card-surface">
          <div class="section-title">{{ group.name }}</div>
          <div class="portal-links">
            <a
              v-for="link in group.links || []"
              :key="link.id"
              class="portal-link"
              :href="link.url"
              :target="link.openInNew ? '_blank' : '_self'"
              rel="noreferrer"
            >
              <img v-if="link.icon" :src="link.icon" alt="" />
              <div>
                <div class="link-title">{{ link.name }}</div>
                <div class="muted link-desc">{{ link.description }}</div>
              </div>
            </a>
          </div>
        </div>
      </div>
    </div>
    <div v-else-if="homeMode === 'THEME'" class="home-theme card-surface">
      <div v-if="themeHtml" v-html="themeHtml"></div>
      <div v-else class="theme-fallback">
        <div class="section-title">Theme home template missing</div>
        <p class="muted">Switch home mode or upload a theme with templates/home.html.</p>
      </div>
    </div>
    <div v-else class="home-blog">
      <div class="blog-hero card-surface">
        <div>
          <span class="subtle-tag">Latest updates</span>
          <h1 class="hero-title serif">{{ siteName }}</h1>
          <p class="hero-subtitle muted">{{ siteDescription }}</p>
        </div>
        <div class="hero-actions">
          <el-input v-model="searchKeyword" placeholder="Search articles">
            <template #append>
              <el-button @click="goSearch">Search</el-button>
            </template>
          </el-input>
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
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api/client'
import type { ArticleSummary, NavigationGroup, SearchEngine } from '@/api/types'
import { useSiteStore } from '@/stores/site'
import ArticleCard from '@/components/ArticleCard.vue'

const router = useRouter()
const site = useSiteStore()

const articles = ref<ArticleSummary[]>([])
const page = ref(1)
const pageSize = 8
const totalEstimate = ref(80)
const searchKeyword = ref('')

const navGroups = computed<NavigationGroup[]>(() => site.navGroups)
const searchEngines = computed<SearchEngine[]>(() => site.searchEngines.filter((engine) => engine.enabled))
const homeMode = computed(() => site.settings.home_mode || 'BLOG')
const siteName = computed(() => site.settings.site_name || 'Blog')
const siteDescription = computed(() => site.settings.site_description || 'Thoughts and stories')

const selectedEngine = ref<number | null>(null)
const themeHtml = ref('')

const loadArticles = async (newPage?: number) => {
  if (newPage) {
    page.value = newPage
  }
  articles.value = await api.get<ArticleSummary[]>('/api/public/articles', {
    params: { page: page.value, size: pageSize },
  })
}

const loadThemeTemplate = async () => {
  if (!site.activeTheme?.publicPath) {
    return
  }
  try {
    const response = await fetch(`${site.activeTheme.publicPath}/templates/home.html`)
    if (response.ok) {
      themeHtml.value = await response.text()
    }
  } catch {
    themeHtml.value = ''
  }
}

const goSearch = () => {
  if (!searchKeyword.value.trim()) {
    return
  }
  router.push({ path: '/search', query: { q: searchKeyword.value } })
}

const performSearch = () => {
  if (!searchKeyword.value.trim()) {
    return
  }
  const engine = searchEngines.value.find((item) => item.id === selectedEngine.value)
  if (engine && engine.queryUrl.includes('{q}')) {
    const url = engine.queryUrl.replace('{q}', encodeURIComponent(searchKeyword.value))
    window.open(url, engine.isDefault ? '_self' : '_blank')
    return
  }
  router.push({ path: '/search', query: { q: searchKeyword.value } })
}

onMounted(async () => {
  await site.loadPublic()
  if (homeMode.value === 'NAV') {
    selectedEngine.value = searchEngines.value.find((engine) => engine.isDefault)?.id || null
  } else if (homeMode.value === 'THEME') {
    await loadThemeTemplate()
  } else {
    await loadArticles()
  }
})
</script>

<style scoped>
.home-blog {
  display: grid;
  gap: 28px;
}

.blog-hero {
  padding: 32px;
  display: grid;
  gap: 18px;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 0.9fr);
  align-items: end;
  position: relative;
  overflow: hidden;
}

.blog-hero::after {
  content: "";
  position: absolute;
  inset: 0;
  background: radial-gradient(
    400px circle at 12% 0%,
    rgba(30, 94, 255, 0.12),
    transparent 60%
  );
  pointer-events: none;
}

.blog-hero > * {
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: 46px;
  letter-spacing: -0.02em;
}

.hero-subtitle {
  max-width: 520px;
  line-height: 1.6;
}

.hero-actions {
  max-width: 420px;
  justify-self: end;
}

.article-grid {
  display: grid;
  gap: 18px;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
}

.pager {
  display: flex;
  justify-content: center;
}

.home-portal {
  display: grid;
  gap: 28px;
}

.portal-hero {
  padding: 32px;
  display: grid;
  gap: 14px;
}

.portal-title {
  font-size: 34px;
  letter-spacing: -0.02em;
}

.portal-search {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.portal-groups {
  display: grid;
  gap: 18px;
}

.portal-group {
  padding: 20px;
  display: grid;
  gap: 14px;
}

.portal-links {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.portal-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.9);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.portal-link:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow);
  border-color: rgba(15, 23, 42, 0.16);
}

.portal-link img {
  width: 32px;
  height: 32px;
  border-radius: 8px;
}

.link-title {
  font-weight: 600;
}

.link-desc {
  font-size: 12px;
}

.home-theme {
  padding: 24px;
}

.theme-fallback {
  padding: 20px;
}

@media (max-width: 900px) {
  .blog-hero {
    grid-template-columns: 1fr;
  }

  .hero-actions {
    justify-self: stretch;
  }
}
</style>
