<template>
  <div>
    <div class="section-title">文章管理</div>
    <div class="filters">
      <el-input v-model="filters.keyword" placeholder="标题关键字" clearable />
      <el-select v-model="filters.status" placeholder="状态" clearable>
        <el-option label="草稿" value="DRAFT" />
        <el-option label="已发布" value="PUBLISHED" />
        <el-option label="已下线" value="ARCHIVED" />
      </el-select>
      <el-select v-model="filters.visibility" placeholder="可见性" clearable>
        <el-option v-for="item in visibilities" :key="item" :label="item" :value="item" />
      </el-select>
      <el-button type="primary" @click="createArticle">新建文章</el-button>
    </div>
    <el-table :data="filteredArticles" stripe>
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="authorName" label="作者" width="120" />
      <el-table-column prop="contentType" label="类型" width="120" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag>{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="visibility" label="可见性" width="140" />
      <el-table-column prop="publishedAt" label="发布时间" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.publishedAt || scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button link type="primary" @click="editArticle(scope.row.id)">编辑</el-button>
          <el-button link @click="togglePublish(scope.row)">
            {{ scope.row.status === 'PUBLISHED' ? '下线' : '发布' }}
          </el-button>
          <el-button link type="danger" @click="removeArticle(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessageBox } from 'element-plus';
import dayjs from 'dayjs';
import type { Visibility } from '../../types';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const site = useSiteStore();
const auth = useAuthStore();

const visibilities: Visibility[] = ['PUBLIC', 'LOGIN_ONLY', 'WHITELIST', 'PRIVATE', 'ADMIN_ONLY'];

const filters = reactive({
  keyword: '',
  status: '',
  visibility: '',
});

const filteredArticles = computed(() =>
  site.articles.filter((article) => {
    if (filters.keyword && !article.title.includes(filters.keyword)) return false;
    if (filters.status && article.status !== filters.status) return false;
    if (filters.visibility && article.visibility !== filters.visibility) return false;
    return true;
  })
);

const createArticle = () => router.push('/admin/articles/new');
const editArticle = (id: string) => router.push(`/admin/articles/${id}`);

const togglePublish = (article: typeof site.articles.value[number]) => {
  const status = article.status === 'PUBLISHED' ? 'ARCHIVED' : 'PUBLISHED';
  site.updateArticle(article.id, { status });
  site.log(auth.currentUser, '更新文章状态', article.title, 'SUCCESS');
};

const removeArticle = async (id: string) => {
  await ElMessageBox.confirm('确认删除该文章？', '危险操作', { type: 'warning' });
  const title = site.articles.find((item) => item.id === id)?.title ?? '';
  site.removeArticle(id);
  site.log(auth.currentUser, '删除文章', title, 'SUCCESS');
};

const formatTime = (value: string) => dayjs(value).format('YYYY-MM-DD HH:mm');
</script>

<style scoped>
.filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}
</style>
