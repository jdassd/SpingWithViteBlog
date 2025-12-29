<template>
  <div>
    <div class="section-title">仪表盘</div>
    <div class="card-grid">
      <el-card>
        <div class="metric">
          <div class="metric-value">{{ publishedCount }}</div>
          <div class="metric-label">已发布文章</div>
        </div>
      </el-card>
      <el-card>
        <div class="metric">
          <div class="metric-value">{{ draftCount }}</div>
          <div class="metric-label">草稿文章</div>
        </div>
      </el-card>
      <el-card>
        <div class="metric">
          <div class="metric-value">{{ pendingComments }}</div>
          <div class="metric-label">待审评论</div>
        </div>
      </el-card>
      <el-card>
        <div class="metric">
          <div class="metric-value">{{ userCount }}</div>
          <div class="metric-label">用户数</div>
        </div>
      </el-card>
    </div>

    <el-divider />

    <div class="section-title">最近动态</div>
    <el-timeline>
      <el-timeline-item v-for="log in recentLogs" :key="log.id" :timestamp="formatTime(log.createdAt)">
        {{ log.actorName }} · {{ log.action }} · {{ log.target }}
      </el-timeline-item>
    </el-timeline>
    <el-empty v-if="recentLogs.length === 0" description="暂无动态" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import dayjs from 'dayjs';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';
import { storage } from '../../services/storage';

const site = useSiteStore();
const auth = useAuthStore();

const publishedCount = computed(() => site.articles.filter((item) => item.status === 'PUBLISHED').length);
const draftCount = computed(() => site.articles.filter((item) => item.status === 'DRAFT').length);
const pendingComments = computed(() => site.comments.filter((item) => item.status === 'PENDING').length);
const userCount = computed(() => storage.getUsers().length);

const recentLogs = computed(() => site.auditLogs.slice(0, 8));
const formatTime = (value: string) => dayjs(value).format('YYYY-MM-DD HH:mm');

if (site.auditLogs.length === 0) {
  site.log(auth.currentUser, '初始化', '系统种子数据', 'SUCCESS');
}
</script>

<style scoped>
.metric {
  text-align: center;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
}

.metric-label {
  color: #64748b;
  margin-top: 6px;
}
</style>
