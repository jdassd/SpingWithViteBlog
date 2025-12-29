<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <el-button type="primary" @click="openCreate">New Album</el-button>
      <el-button @click="testGit">Test Git Connection</el-button>
    </div>
    <el-table :data="albums" stripe>
      <el-table-column prop="title" label="Title" min-width="200" />
      <el-table-column prop="visibility" label="Visibility" width="140" />
      <el-table-column prop="updatedAt" label="Updated" width="160" />
      <el-table-column label="Actions" width="240">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">Edit</el-button>
          <el-button size="small" @click="manageAlbum(row)">Manage</el-button>
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
        <el-form-item v-if="albumForm.visibility === 'WHITELIST'" label="Whitelist users">
          <el-select v-model="albumForm.whitelistUserIds" multiple filterable>
            <el-option v-for="user in users" :key="user.id" :label="user.username" :value="user.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditor = false">Cancel</el-button>
        <el-button type="primary" @click="saveAlbum">Save</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api/client'
import type { Album, User } from '@/api/types'

const router = useRouter()
const albums = ref<Album[]>([])
const users = ref<User[]>([])
const showEditor = ref(false)
const editingAlbum = ref<Album | null>(null)

const albumForm = reactive({
  title: '',
  description: '',
  visibility: 'PUBLIC',
  whitelistUserIds: [] as number[],
})

const visibilityOptions = ['PUBLIC', 'LOGIN_ONLY', 'WHITELIST', 'PRIVATE', 'ADMIN_ONLY']

const loadAlbums = async () => {
  albums.value = await api.get<Album[]>('/api/admin/albums')
}

const loadUsers = async () => {
  users.value = await api.get<User[]>('/api/admin/users')
}

const openCreate = () => {
  editingAlbum.value = null
  albumForm.title = ''
  albumForm.description = ''
  albumForm.visibility = 'PUBLIC'
  albumForm.whitelistUserIds = []
  showEditor.value = true
}

const openEdit = (album: Album) => {
  editingAlbum.value = album
  albumForm.title = album.title
  albumForm.description = album.description || ''
  albumForm.visibility = album.visibility
  albumForm.whitelistUserIds = album.whitelistUserIds || []
  showEditor.value = true
}

const saveAlbum = async () => {
  try {
    if (editingAlbum.value) {
      await api.put(`/api/admin/albums/${editingAlbum.value.id}`, albumForm)
    } else {
      await api.post('/api/admin/albums', albumForm)
    }
    ElMessage.success('Saved')
    showEditor.value = false
    await loadAlbums()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const manageAlbum = (album: Album) => router.push(`/admin/albums/${album.id}`)

const deleteAlbum = async (album: Album) => {
  await ElMessageBox.confirm('Delete this album?', 'Confirm', { type: 'warning' })
  try {
    await api.delete(`/api/admin/albums/${album.id}`)
    ElMessage.success('Deleted')
    await loadAlbums()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

const testGit = async () => {
  try {
    await api.post('/api/admin/albums/git/test')
    ElMessage.success('Connection OK')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Test failed')
  }
}

onMounted(async () => {
  await loadUsers()
  await loadAlbums()
})
</script>

<style scoped>
.admin-section {
  display: grid;
  gap: 16px;
}

.toolbar {
  padding: 16px;
  display: flex;
  gap: 12px;
}
</style>
