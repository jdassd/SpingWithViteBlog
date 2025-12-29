<template>
  <section class="container">
    <div class="card-surface album-card">
      <div class="album-head">
        <div>
          <span class="subtle-tag">{{ album?.visibility }}</span>
          <h1 class="serif">{{ album?.title }}</h1>
          <p class="muted">{{ album?.description }}</p>
        </div>
        <div class="album-actions">
          <el-switch v-model="autoSync" :active-text="$t('admin.labels.autoSync')" />
          <el-button @click="goBack">{{ $t('admin.actions.back') }}</el-button>
        </div>
      </div>
    </div>
    <div class="upload-card card-surface">
      <input type="file" @change="handleUpload" />
    </div>
    <div class="photo-grid">
      <div v-for="photo in photos" :key="photo.id" class="photo-card card-surface">
        <img :src="photo.thumbnailPath || photo.originalPath" alt="" />
        <div class="photo-meta">
          <span class="muted">{{ photo.syncStatus }}</span>
          <span v-if="photo.syncError" class="muted">{{ photo.syncError }}</span>
        </div>
        <div class="photo-actions">
          <el-button size="small" @click="setCover(photo)">{{ $t('admin.actions.setCover') }}</el-button>
          <el-button size="small" @click="editTags(photo)">{{ $t('admin.labels.tags') }}</el-button>
          <el-button size="small" @click="syncPhoto(photo)">{{ $t('admin.actions.sync') }}</el-button>
          <el-button size="small" type="danger" @click="deletePhoto(photo)">{{ $t('admin.actions.delete') }}</el-button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import api from '@/api/client'
import type { Album, Photo } from '@/api/types'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const album = ref<Album | null>(null)
const photos = ref<Photo[]>([])
const autoSync = ref(false)

const loadAlbum = async () => {
  album.value = await api.get<Album>(`/api/admin/albums/${route.params.id}`)
  photos.value = await api.get<Photo[]>(`/api/admin/albums/${route.params.id}/photos`)
}

const handleUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  if (!target.files) {
    return
  }
  const file = target.files[0]
  const form = new FormData()
  form.append('file', file)
  try {
    await api.post(`/api/admin/albums/${route.params.id}/photos`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
      params: { autoSync: autoSync.value },
    })
    photos.value = await api.get<Photo[]>(`/api/admin/albums/${route.params.id}/photos`)
    ElMessage.success(t('admin.messages.uploaded'))
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.uploadFailed'))
  } finally {
    target.value = ''
  }
}

const setCover = async (photo: Photo) => {
  try {
    await api.post(`/api/admin/albums/${route.params.id}/cover`, null, {
      params: { photoId: photo.id },
    })
    ElMessage.success(t('admin.messages.coverUpdated'))
    await loadAlbum()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.updateFailed'))
  }
}

const syncPhoto = async (photo: Photo) => {
  try {
    await api.post(`/api/admin/albums/photos/${photo.id}/sync`)
    ElMessage.success(t('admin.messages.syncTriggered'))
    await loadAlbum()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.syncFailed'))
  }
}

const editTags = async (photo: Photo) => {
  try {
    const { value } = await ElMessageBox.prompt(t('admin.messages.enterTags'), t('admin.dialogs.editTags'), {
      inputValue: (photo.tags || []).join(','),
      confirmButtonText: t('common.save'),
      cancelButtonText: t('common.cancel'),
    })
    const tags = value
      .split(',')
      .map((tag) => tag.trim())
      .filter((tag) => tag)
    await api.patch(`/api/admin/albums/photos/${photo.id}/tags`, { tags })
    ElMessage.success(t('admin.messages.tagsUpdated'))
    await loadAlbum()
  } catch (err: any) {
    if (err === 'cancel' || err === 'close') {
      return
    }
    ElMessage.error(err?.message || t('admin.messages.updateFailed'))
  }
}

const deletePhoto = async (photo: Photo) => {
  await ElMessageBox.confirm(t('admin.confirms.deletePhoto'), t('admin.confirms.confirm'), { type: 'warning' })
  try {
    await api.delete(`/api/admin/albums/photos/${photo.id}`)
    ElMessage.success(t('admin.messages.deleted'))
    await loadAlbum()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.deleteFailed'))
  }
}

const goBack = () => router.back()

onMounted(loadAlbum)
</script>

<style scoped>
.album-card {
  padding: 20px;
  margin-bottom: 16px;
}

.album-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.album-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.upload-card {
  padding: 16px;
  margin-bottom: 16px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.photo-card {
  padding: 10px;
  display: grid;
  gap: 8px;
}

.photo-card img {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 10px;
}

.photo-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

@media (max-width: 640px) {
  .album-head {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .album-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .album-card,
  .upload-card {
    padding: 12px;
  }

  .photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }

  .photo-card img {
    height: 120px;
  }
}
</style>
