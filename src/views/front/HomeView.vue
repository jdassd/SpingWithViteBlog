<template>
  <div class="page-container">
    <div class="layout-main">
      <template v-if="settings.homeMode === 'BLOG'">
        <div class="section-title">{{ settings.siteName }}</div>
        <p>{{ settings.description }}</p>
        <el-input
          v-model="keyword"
          placeholder="搜索文章标题或摘要"
          :prefix-icon="Search"
          class="search-input"
          @keyup.enter="search"
        />
        <el-tabs v-model="activeTab" class="tabs">
          <el-tab-pane label="最新" name="latest" />
          <el-tab-pane label="分类" name="category" />
          <el-tab-pane label="标签" name="tag" />
        </el-tabs>
        <div class="card-grid">
          <ArticleCard v-for="article in filteredArticles" :key="article.id" :article="article" />
        </div>
        <el-empty v-if="filteredArticles.length === 0" description="没有符合条件的文章" class="empty-state" />
      </template>

      <template v-else-if="settings.homeMode === 'PORTAL'">
        <div class="section-title">导航门户</div>
        <div class="portal-search">
          <el-input v-model="portalKeyword" placeholder="输入关键词" @keyup.enter="runSearch">
            <template #append>
              <el-select v-model="selectedEngine" style="width: 160px">
                <el-option
                  v-for="engine in enabledEngines"
                  :key="engine.id"
                  :label="engine.name"
                  :value="engine.id"
                />
              </el-select>
            </template>
          </el-input>
          <el-button type="primary" @click="runSearch">搜索</el-button>
        </div>
        <div v-for="group in settings.navigationGroups" :key="group.id" class="nav-group">
          <div class="section-title">{{ group.name }}</div>
          <div class="card-grid">
            <el-card
              v-for="item in group.items"
              :key="item.id"
              class="nav-card"
              shadow="hover"
              @click="openLink(item.url, item.openInNew)"
            >
              <div class="nav-card-title">{{ item.name }}</div>
              <div class="nav-card-desc">{{ item.description }}</div>
            </el-card>
          </div>
        </div>
      </template>

      <template v-else>
        <el-result icon="warning" title="主题自定义首页" sub-title="当前主题正在自定义首页模式。">
          <template #extra>
            <el-button type="primary" @click="router.push('/articles')">浏览文章</el-button>
          </template>
        </el-result>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import ArticleCard from '../../components/ArticleCard.vue';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const site = useSiteStore();
const auth = useAuthStore();
const router = useRouter();

const settings = computed(() => site.settings);
const keyword = ref('');
const activeTab = ref('latest');

const filteredArticles = computed(() => {
  const list = site.visibleArticles(auth.currentUser);
  if (!keyword.value.trim()) return list;
  return list.filter((item) =>
    item.title.includes(keyword.value) || item.summary.includes(keyword.value)
  );
});

const search = () => {
  if (!keyword.value.trim()) {
    ElMessage.warning('请输入关键字');
  }
};

const portalKeyword = ref('');
const enabledEngines = computed(() => settings.value.engines.filter((engine) => engine.enabled));
const selectedEngine = ref(enabledEngines.value.find((engine) => engine.isDefault)?.id || enabledEngines.value[0]?.id);

const runSearch = () => {
  if (!portalKeyword.value.trim()) {
    ElMessage.warning('请输入关键字');
    return;
  }
  const engine = enabledEngines.value.find((item) => item.id === selectedEngine.value);
  if (!engine) return;
  if (engine.queryTemplate === 'INTERNAL') {
    router.push({ name: 'articles', query: { q: portalKeyword.value } });
    return;
  }
  const url = engine.queryTemplate.replace('{q}', encodeURIComponent(portalKeyword.value));
  window.open(url, '_blank');
};

const openLink = (url: string, openInNew?: boolean) => {
  if (!/^https?:\/\//.test(url)) {
    ElMessage.error('链接不可用');
    return;
  }
  if (openInNew) {
    window.open(url, '_blank');
  } else {
    window.location.href = url;
  }
};
</script>

<style scoped>
.search-input {
  margin: 16px 0;
}

.tabs {
  margin-bottom: 16px;
}

.portal-search {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 24px;
}

.nav-group {
  margin-top: 24px;
}

.nav-card {
  cursor: pointer;
}

.nav-card-title {
  font-weight: 600;
}

.nav-card-desc {
  color: #6b7280;
  margin-top: 6px;
}
</style>
