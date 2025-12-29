<template>
  <div class="page-container">
    <div v-if="!page" class="layout-main">
      <el-result icon="warning" title="页面不存在" />
    </div>
    <div v-else class="layout-main">
      <div class="section-title">{{ page.title }}</div>
      <div v-if="page.type === 'LINK'">
        <el-result icon="info" title="外链页面" sub-title="点击按钮前往外链地址">
          <template #extra>
            <el-button type="primary" @click="openLink(page.linkUrl)">打开链接</el-button>
          </template>
        </el-result>
      </div>
      <div v-else class="page-content" v-html="page.contentHtml"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const route = useRoute();
const router = useRouter();
const site = useSiteStore();
const auth = useAuthStore();

const page = computed(() => site.pages.find((item) => item.slug === route.params.slug));

onMounted(() => {
  if (!page.value) return;
  const canView = site.visiblePages(auth.currentUser).some((item) => item.id === page.value?.id);
  if (!canView) {
    router.push(auth.isAuthed ? '/403' : '/401');
  }
});

const openLink = (url?: string) => {
  if (!url || !/^https?:\/\//.test(url)) {
    ElMessage.error('链接不可用');
    return;
  }
  window.open(url, '_blank');
};
</script>

<style scoped>
.page-content :deep(h1),
.page-content :deep(h2) {
  margin-top: 24px;
}
</style>
