<template>
  <div>
    <div class="section-title">{{ isEdit ? '编辑文章' : '新建文章' }}</div>
    <el-form :model="form" label-position="top">
      <el-form-item label="标题" required>
        <el-input v-model="form.title" placeholder="请输入标题" />
      </el-form-item>
      <el-form-item label="类型">
        <el-radio-group v-model="form.contentType">
          <el-radio label="MARKDOWN">Markdown</el-radio>
          <el-radio label="RICH_TEXT">富文本</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="内容">
        <el-input v-model="form.contentRaw" type="textarea" :rows="10" placeholder="输入内容" />
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="可选摘要" />
      </el-form-item>
      <el-form-item label="分类">
        <el-input v-model="form.category" placeholder="分类" />
      </el-form-item>
      <el-form-item label="标签">
        <el-select v-model="form.tags" multiple filterable allow-create default-first-option>
          <el-option v-for="tag in allTags" :key="tag" :label="tag" :value="tag" />
        </el-select>
      </el-form-item>
      <el-form-item label="可见性">
        <el-select v-model="form.visibility">
          <el-option v-for="item in visibilities" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="form.status">
          <el-option label="草稿" value="DRAFT" />
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="已下线" value="ARCHIVED" />
        </el-select>
      </el-form-item>
      <el-form-item label="RSS 收录">
        <el-switch v-model="form.allowRss" />
      </el-form-item>
      <el-form-item label="白名单用户 (WHITELIST 可见)">
        <el-select v-model="form.whitelist" multiple filterable>
          <el-option v-for="user in users" :key="user.id" :label="user.username" :value="user.id" />
        </el-select>
      </el-form-item>
      <el-space>
        <el-button type="primary" @click="save">保存</el-button>
        <el-button @click="goBack">取消</el-button>
      </el-space>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import type { Visibility } from '../../types';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';
import { storage } from '../../services/storage';

const route = useRoute();
const router = useRouter();
const site = useSiteStore();
const auth = useAuthStore();

const articleId = route.params.id as string | undefined;
const isEdit = computed(() => Boolean(articleId));

const existing = computed(() => site.articles.find((item) => item.id === articleId));

const form = reactive({
  title: existing.value?.title ?? '',
  contentType: existing.value?.contentType ?? 'MARKDOWN',
  contentRaw: existing.value?.contentRaw ?? '',
  summary: existing.value?.summary ?? '',
  category: existing.value?.category ?? '',
  tags: existing.value?.tags ?? [],
  visibility: existing.value?.visibility ?? 'PUBLIC',
  status: existing.value?.status ?? 'DRAFT',
  allowRss: existing.value?.allowRss ?? false,
  whitelist: existing.value?.whitelist ?? [],
});

const users = computed(() => storage.getUsers());
const visibilities: Visibility[] = ['PUBLIC', 'LOGIN_ONLY', 'WHITELIST', 'PRIVATE', 'ADMIN_ONLY'];
const allTags = computed(() => Array.from(new Set(site.articles.flatMap((item) => item.tags))));

const save = () => {
  if (!form.title.trim()) {
    ElMessage.error('标题不能为空');
    return;
  }
  if (isEdit.value && existing.value) {
    site.updateArticle(existing.value.id, { ...form });
    site.log(auth.currentUser, '更新文章', form.title, 'SUCCESS');
  } else {
    if (!auth.currentUser) return;
    site.addArticle({ ...form }, auth.currentUser);
    site.log(auth.currentUser, '新建文章', form.title, 'SUCCESS');
  }
  router.push('/admin/articles');
};

const goBack = () => router.push('/admin/articles');
</script>
