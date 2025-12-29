<template>
  <div class="page-container">
    <div v-if="!album" class="layout-main">
      <el-result icon="warning" title="相册不存在" />
    </div>
    <div v-else class="layout-main">
      <div class="section-title">{{ album.title }}</div>
      <p>{{ album.description }}</p>
      <div class="photo-grid">
        <el-image
          v-for="photo in albumPhotos"
          :key="photo.id"
          :src="photo.thumbUrl"
          :preview-src-list="previewList"
          :initial-index="previewList.indexOf(photo.url)"
          fit="cover"
          class="photo"
        />
      </div>
      <el-empty v-if="albumPhotos.length === 0" description="该相册暂无照片" class="empty-state" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const route = useRoute();
const router = useRouter();
const site = useSiteStore();
const auth = useAuthStore();

const album = computed(() => site.albums.find((item) => item.id === route.params.id));

if (album.value) {
  const canView = site.visibleAlbums(auth.currentUser).some((item) => item.id === album.value?.id);
  if (!canView) {
    router.push(auth.isAuthed ? '/403' : '/401');
  }
}

const albumPhotos = computed(() => site.photos.filter((item) => item.albumId === route.params.id));
const previewList = computed(() => albumPhotos.value.map((item) => item.url));
</script>

<style scoped>
.photo-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.photo {
  width: 100%;
  height: 160px;
  border-radius: 12px;
}
</style>
