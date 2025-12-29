<template>
  <section class="container">
    <div class="album-hero card-surface">
      <div>
        <span class="subtle-tag">Albums</span>
        <h1 class="serif">Photo collections</h1>
        <p class="muted">Curated visual stories and archives.</p>
      </div>
    </div>
    <div class="album-grid">
      <RouterLink
        v-for="album in albums"
        :key="album.id"
        :to="`/albums/${album.id}`"
        class="album-card card-surface"
      >
        <div class="cover" :style="{ backgroundImage: `url(${album.coverUrl || fallbackCover})` }"></div>
        <div class="album-info">
          <div class="album-title">{{ album.title }}</div>
          <div class="muted">{{ album.description || 'Untitled collection' }}</div>
        </div>
      </RouterLink>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import api from '@/api/client'
import type { Album, Photo } from '@/api/types'

type AlbumView = Album & { coverUrl?: string }

const albums = ref<AlbumView[]>([])
const fallbackCover =
  'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?q=80&w=800&auto=format&fit=crop'

const loadAlbums = async () => {
  const data = await api.get<Album[]>('/api/public/albums')
  const enriched = await Promise.all(
    data.map(async (album) => {
      try {
        const photos = await api.get<Photo[]>(`/api/public/albums/${album.id}/photos`)
        const cover = photos.find((photo) => photo.id === album.coverPhotoId) || photos[0]
        return { ...album, coverUrl: cover?.thumbnailPath || cover?.originalPath }
      } catch {
        return { ...album }
      }
    })
  )
  albums.value = enriched
}

onMounted(loadAlbums)
</script>

<style scoped>
.album-hero {
  padding: 24px;
  margin-bottom: 20px;
}

.album-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.album-card {
  display: grid;
  gap: 12px;
  overflow: hidden;
}

.cover {
  height: 180px;
  background-size: cover;
  background-position: center;
}

.album-info {
  padding: 12px 14px 18px;
  display: grid;
  gap: 6px;
}

.album-title {
  font-weight: 600;
}
</style>
