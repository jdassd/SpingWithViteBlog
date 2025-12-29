<template>
  <section class="container">
    <div class="list-hero card-surface">
      <div>
        <span class="subtle-tag">My Albums</span>
        <h1 class="serif">Manage your galleries</h1>
      </div>
      <el-button type="primary" @click="openCreate">New Album</el-button>
    </div>
    <el-table :data="albums" stripe>
      <el-table-column prop="title" label="Title" />
      <el-table-column prop="visibility" label="Visibility" width="140" />
      <el-table-column prop="updatedAt" label="Updated" width="160" />
      <el-table-column label="Actions" width="240">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">Edit</el-button>
          <el-button size="small" @click="openManage(row)">Manage</el-button>
          <el-button size="small" type="danger" @click="deleteAlbum(row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showEditor" width="520px" :title="editingAlbum ? 'Edit Album' : 'New Album'">
      <el-form :model="albumForm" label-position="top">
        <el-form-item label="Title">
          <el-input v-model="albumForm.title" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="albumForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="Visibility">
          <el-select v-model="albumForm.visibility">
            <el-option v-for="option in visibilityOptions" :key="option" :label="option" :value="option" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="albumForm.visibility === 'WHITELIST'" label="Whitelist user IDs">
          <el-input v-model="whitelistInput" placeholder="Comma-separated user IDs" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditor = false">Cancel</el-button>
        <el-button type="primary" @click="saveAlbum">Save</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showManager" width="80%" title="Manage Photos">
      <div class="upload-row">
        <input type="file" @change="handleUpload" />
      </div>
      <div class="photo-grid">
        <div v-for="photo in photos" :key="photo.id" class="photo-card">
          <img :src="photo.thumbnailPath || photo.originalPath" alt="" />
          <div class="photo-actions">
            <el-button size="small" @click="setCover(photo)">Set Cover</el-button>
            <el-button size="small" @click="editTags(photo)">Tags</el-button>
            <el-button size="small" type="danger" @click="deletePhoto(photo)">Delete</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api/client'
import type { Album, Photo } from '@/api/types'

const albums = ref<Album[]>([])
const photos = ref<Photo[]>([])
const showEditor = ref(false)
const showManager = ref(false)
const editingAlbum = ref<Album | null>(null)
const currentAlbumId = ref<number | null>(null)
const whitelistInput = ref('')

const albumForm = reactive({
  title: '',
  description: '',
  visibility: 'PUBLIC',
})

const visibilityOptions = ['PUBLIC', 'LOGIN_ONLY', 'WHITELIST', 'PRIVATE']

const loadAlbums = async () => {
  albums.value = await api.get<Album[]>('/api/user/albums')
}

const openCreate = () => {
  editingAlbum.value = null
  albumForm.title = ''
  albumForm.description = ''
  albumForm.visibility = 'PUBLIC'
  whitelistInput.value = ''
  showEditor.value = true
}

const openEdit = (album: Album) => {
  editingAlbum.value = album
  albumForm.title = album.title
  albumForm.description = album.description || ''
  albumForm.visibility = album.visibility
  whitelistInput.value = (album.whitelistUserIds || []).join(',')
  showEditor.value = true
}

const saveAlbum = async () => {
  const payload = {
    ...albumForm,
    whitelistUserIds:
      albumForm.visibility === 'WHITELIST'
        ? whitelistInput.value
            .split(',')
            .map((id) => parseInt(id.trim(), 10))
            .filter((id) => !Number.isNaN(id))
        : [],
  }
  try {
    if (editingAlbum.value) {
      await api.put(`/api/user/albums/${editingAlbum.value.id}`, payload)
    } else {
      await api.post('/api/user/albums', payload)
    }
    ElMessage.success('Saved')
    showEditor.value = false
    await loadAlbums()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const deleteAlbum = async (album: Album) => {
  await ElMessageBox.confirm('Delete this album?', 'Confirm', { type: 'warning' })
  try {
    await api.delete(`/api/user/albums/${album.id}`)
    ElMessage.success('Deleted')
    await loadAlbums()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

const openManage = async (album: Album) => {
  currentAlbumId.value = album.id
  photos.value = await api.get<Photo[]>(`/api/user/albums/${album.id}/photos`)
  showManager.value = true
}

const handleUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  if (!target.files || !currentAlbumId.value) {
    return
  }
  const file = target.files[0]
  const form = new FormData()
  form.append('file', file)
  try {
    await api.post(`/api/user/albums/${currentAlbumId.value}/photos`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    photos.value = await api.get<Photo[]>(`/api/user/albums/${currentAlbumId.value}/photos`)
    ElMessage.success('Uploaded')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Upload failed')
  } finally {
    target.value = ''
  }
}

const deletePhoto = async (photo: Photo) => {
  if (!currentAlbumId.value) {
    return
  }
  await ElMessageBox.confirm('Delete this photo?', 'Confirm', { type: 'warning' })
  try {
    await api.delete(`/api/user/albums/photos/${photo.id}`)
    photos.value = await api.get<Photo[]>(`/api/user/albums/${currentAlbumId.value}/photos`)
    ElMessage.success('Deleted')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

const editTags = async (photo: Photo) => {
  if (!currentAlbumId.value) {
    return
  }
  try {
    const { value } = await ElMessageBox.prompt('Enter tags (comma separated)', 'Edit Tags', {
      inputValue: (photo.tags || []).join(','),
      confirmButtonText: 'Save',
      cancelButtonText: 'Cancel',
    })
    const tags = value
      .split(',')
      .map((tag) => tag.trim())
      .filter((tag) => tag)
    await api.patch(`/api/user/albums/photos/${photo.id}/tags`, { tags })
    photos.value = await api.get<Photo[]>(`/api/user/albums/${currentAlbumId.value}/photos`)
    ElMessage.success('Tags updated')
  } catch (err: any) {
    if (err === 'cancel' || err === 'close') {
      return
    }
    ElMessage.error(err?.message || 'Update failed')
  }
}

const setCover = async (photo: Photo) => {
  if (!currentAlbumId.value) {
    return
  }
  try {
    await api.post(`/api/user/albums/${currentAlbumId.value}/cover`, null, {
      params: { photoId: photo.id },
    })
    ElMessage.success('Cover updated')
    await loadAlbums()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Update failed')
  }
}

onMounted(loadAlbums)
</script>

<style scoped>
.list-hero {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.upload-row {
  margin-bottom: 16px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
}

.photo-card {
  background: #fff;
  padding: 8px;
  border-radius: 12px;
}

.photo-card img {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 8px;
}

.photo-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
}
</style>
