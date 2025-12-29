<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <el-button type="primary" @click="openCreate">{{ $t('admin.newAlbum') }}</el-button>
      <el-button @click="testGit">{{ $t('admin.testGitConnection') }}</el-button>
    </div>
    <el-table :data="albums" stripe>
      <el-table-column prop="title" :label="$t('admin.columns.title')" min-width="200" />
      <el-table-column prop="visibility" :label="$t('admin.columns.visibility')" width="140" />
      <el-table-column prop="updatedAt" :label="$t('admin.columns.updated')" width="160" />
      <el-table-column :label="$t('admin.columns.actions')" width="240">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">{{ $t('admin.actions.edit') }}</el-button>
          <el-button size="small" @click="manageAlbum(row)">{{ $t('admin.actions.manage') }}</el-button>
          <el-button size="small" type="danger" @click="deleteAlbum(row)">{{ $t('admin.actions.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showEditor" width="520px" :title="editingAlbum ? $t('admin.dialogs.editAlbum') : $t('admin.dialogs.newAlbum')">
      <el-form :model="albumForm" label-position="top">
        <el-form-item :label="$t('admin.columns.title')">
          <el-input v-model="albumForm.title" />
        </el-form-item>
        <el-form-item :label="$t('admin.labels.description')">
          <el-input v-model="albumForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item :label="$t('admin.labels.visibility')">
          <el-select v-model="albumForm.visibility">
            <el-option v-for="option in visibilityOptions" :key="option" :label="getVisibilityLabel(option)" :value="option" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="albumForm.visibility === 'WHITELIST'" :label="$t('admin.labels.whitelistUsers')">
          <el-select v-model="albumForm.whitelistUserIds" multiple filterable>
            <el-option v-for="user in users" :key="user.id" :label="user.username" :value="user.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditor = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="saveAlbum">{{ $t('common.save') }}</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import api from '@/api/client'
import type { Album, User } from '@/api/types'

const { t } = useI18n()
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

const getVisibilityLabel = (option: string) => {
  const map: Record<string, string> = {
    PUBLIC: t('admin.visibilityOptions.public'),
    LOGIN_ONLY: t('admin.visibilityOptions.loginOnly'),
    WHITELIST: t('admin.visibilityOptions.whitelist'),
    PRIVATE: t('admin.visibilityOptions.private'),
    ADMIN_ONLY: t('admin.visibilityOptions.adminOnly'),
  }
  return map[option] || option
}

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
    ElMessage.success(t('admin.messages.saved'))
    showEditor.value = false
    await loadAlbums()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.saveFailed'))
  }
}

const manageAlbum = (album: Album) => router.push(`/admin/albums/${album.id}`)

const deleteAlbum = async (album: Album) => {
  await ElMessageBox.confirm(t('admin.confirms.deleteAlbum'), t('admin.confirms.confirm'), { type: 'warning' })
  try {
    await api.delete(`/api/admin/albums/${album.id}`)
    ElMessage.success(t('admin.messages.deleted'))
    await loadAlbums()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.deleteFailed'))
  }
}

const testGit = async () => {
  try {
    await api.post('/api/admin/albums/git/test')
    ElMessage.success(t('admin.messages.connectionOk'))
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.testFailed'))
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
