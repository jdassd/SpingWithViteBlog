<template>
  <section class="container">
    <div v-if="loading" class="card-surface album-card">
      <el-skeleton :rows="4" animated />
    </div>
    <div v-else class="album-wrapper">
      <div class="card-surface album-card">
        <div class="album-head">
          <div>
            <span class="subtle-tag">{{ album?.visibility }}</span>
            <h1 class="serif">{{ album?.title }}</h1>
            <p class="muted">{{ album?.description }}</p>
          </div>
          <el-button @click="goBack">Back</el-button>
        </div>
      </div>
      <div class="photo-grid">
        <div v-for="photo in photos" :key="photo.id" class="photo-card card-surface" @click="openPhoto(photo)">
          <img :src="photo.thumbnailPath || photo.originalPath" alt="" />
        </div>
      </div>
    </div>
    <el-dialog v-model="showPreview" width="80%">
      <img v-if="previewPhoto" :src="previewPhoto.originalPath" alt="" class="preview-image" />
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api/client'
import type { Album, Photo } from '@/api/types'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const album = ref<Album | null>(null)
const photos = ref<Photo[]>([])
const showPreview = ref(false)
const previewPhoto = ref<Photo | null>(null)

const loadAlbum = async () => {
  loading.value = true
  try {
    album.value = await api.get<Album>(`/api/public/albums/${route.params.id}`)
    photos.value = await api.get<Photo[]>(`/api/public/albums/${route.params.id}/photos`)
  } finally {
    loading.value = false
  }
}

const openPhoto = (photo: Photo) => {
  previewPhoto.value = photo
  showPreview.value = true
}

const goBack = () => router.back()

onMounted(loadAlbum)
</script>

<style scoped>
.album-wrapper {
  display: grid;
  gap: 20px;
}

.album-card {
  padding: 22px;
}

.album-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
}

.photo-card {
  overflow: hidden;
  border-radius: 14px;
  cursor: pointer;
}

.photo-card img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  transition: transform 0.2s ease;
}

.photo-card:hover img {
  transform: scale(1.04);
}

.preview-image {
  width: 100%;
  border-radius: 12px;
}

@media (max-width: 640px) {
  .album-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 10px;
  }

  .photo-card img {
    height: 140px;
  }
}

@media (max-width: 480px) {
  .album-card {
    padding: 16px;
  }

  .photo-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .photo-card img {
    height: 120px;
  }
}
</style>
