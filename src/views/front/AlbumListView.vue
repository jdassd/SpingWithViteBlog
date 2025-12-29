<template>
  <div class="page-container">
    <div class="layout-main">
      <div class="section-title">摄影集</div>
      <div class="card-grid">
        <el-card v-for="album in visibleAlbums" :key="album.id" shadow="hover" class="album-card" @click="goDetail(album.id)">
          <img v-if="album.coverUrl" :src="album.coverUrl" class="cover" />
          <div class="album-content">
            <h3>{{ album.title }}</h3>
            <p>{{ album.description }}</p>
            <el-tag size="small">{{ album.visibility }}</el-tag>
          </div>
        </el-card>
      </div>
      <el-empty v-if="visibleAlbums.length === 0" description="暂无相册" class="empty-state" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const site = useSiteStore();
const auth = useAuthStore();

const visibleAlbums = computed(() => site.visibleAlbums(auth.currentUser));

const goDetail = (id: string) => router.push(`/albums/${id}`);
</script>

<style scoped>
.album-card {
  cursor: pointer;
  overflow: hidden;
}

.cover {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 12px;
}

.album-content {
  margin-top: 12px;
}
</style>
