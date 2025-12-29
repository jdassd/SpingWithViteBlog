<template>
  <div class="page-container">
    <div class="layout-main">
      <div class="section-title">RSS 订阅</div>
      <el-alert type="info" show-icon :closable="false" title="该页面展示 RSS XML 输出，可复制或下载" />
      <div v-if="settings.rssEnabled">
        <el-input v-model="rssContent" type="textarea" :rows="14" readonly class="rss-output" />
        <el-space>
          <el-button type="primary" @click="download">下载 RSS</el-button>
          <el-button @click="copy">复制到剪贴板</el-button>
        </el-space>
      </div>
      <el-empty v-else description="RSS 功能已关闭" class="empty-state" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const site = useSiteStore();
const auth = useAuthStore();

const settings = computed(() => site.settings);

const rssContent = computed(() => {
  const items = site
    .visibleArticles(auth.currentUser)
    .filter((article) => article.allowRss)
    .map((article) => {
      const content = site.settings.rssFullContent ? article.contentHtml : article.summary;
      return `\n    <item>\n      <title>${article.title}</title>\n      <link>${window.location.origin}/articles/${article.id}</link>\n      <pubDate>${dayjs(article.publishedAt || article.createdAt).toISOString()}</pubDate>\n      <author>${article.authorName}</author>\n      <description><![CDATA[${content}]]></description>\n    </item>`;
    })
    .join('');

  return `<?xml version="1.0" encoding="UTF-8"?>\n<rss version="2.0">\n  <channel>\n    <title>${settings.value.siteName}</title>\n    <link>${window.location.origin}</link>\n    <description>${settings.value.description}</description>\n    <lastBuildDate>${dayjs().toISOString()}</lastBuildDate>${items}\n  </channel>\n</rss>`;
});

const download = () => {
  const blob = new Blob([rssContent.value], { type: 'application/rss+xml' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = 'rss.xml';
  link.click();
};

const copy = async () => {
  await navigator.clipboard.writeText(rssContent.value);
  ElMessage.success('已复制 RSS 内容');
};
</script>

<style scoped>
.rss-output {
  margin: 16px 0;
}
</style>
