<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <input type="file" @change="importTheme" />
      <el-checkbox v-model="activateOnImport">Activate after import</el-checkbox>
    </div>
    <el-table :data="themes" stripe>
      <el-table-column prop="name" label="Name" min-width="160" />
      <el-table-column prop="version" label="Version" width="120" />
      <el-table-column prop="author" label="Author" width="140" />
      <el-table-column prop="isActive" label="Active" width="100" />
      <el-table-column label="Actions" width="320">
        <template #default="{ row }">
          <el-button size="small" @click="activateTheme(row)">Activate</el-button>
          <el-button size="small" @click="openConfig(row)">Config</el-button>
          <el-button size="small" @click="exportTheme(row)">Export</el-button>
          <el-button size="small" type="danger" @click="deleteTheme(row)" :disabled="row.isActive">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showConfig" width="600px" title="Theme Config">
      <el-input v-model="configJson" type="textarea" :rows="10" />
      <template #footer>
        <el-button @click="showConfig = false">Cancel</el-button>
        <el-button type="primary" @click="saveConfig">Save</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api/client'
import type { Theme } from '@/api/types'

const themes = ref<Theme[]>([])
const activateOnImport = ref(false)
const showConfig = ref(false)
const configJson = ref('')
const editingThemeId = ref<number | null>(null)

const loadThemes = async () => {
  themes.value = await api.get<Theme[]>('/api/admin/themes')
}

const importTheme = async (event: Event) => {
  const target = event.target as HTMLInputElement
  if (!target.files) {
    return
  }
  const file = target.files[0]
  const form = new FormData()
  form.append('file', file)
  try {
    await api.post('/api/admin/themes/import', form, {
      params: { activate: activateOnImport.value },
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    ElMessage.success('Imported')
    await loadThemes()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Import failed')
  } finally {
    target.value = ''
  }
}

const activateTheme = async (theme: Theme) => {
  await api.post(`/api/admin/themes/${theme.id}/activate`)
  ElMessage.success('Activated')
  await loadThemes()
}

const openConfig = (theme: Theme) => {
  editingThemeId.value = theme.id
  configJson.value = theme.configJson || ''
  showConfig.value = true
}

const saveConfig = async () => {
  if (!editingThemeId.value) {
    return
  }
  try {
    await api.put(`/api/admin/themes/${editingThemeId.value}/config`, { configJson: configJson.value })
    ElMessage.success('Saved')
    showConfig.value = false
    await loadThemes()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const exportTheme = async (theme: Theme) => {
  const url = `/api/admin/themes/${theme.id}/export?includeCustomCode=false`
  window.open(url, '_blank')
}

const deleteTheme = async (theme: Theme) => {
  await ElMessageBox.confirm('Delete this theme?', 'Confirm', { type: 'warning' })
  try {
    await api.delete(`/api/admin/themes/${theme.id}`)
    ElMessage.success('Deleted')
    await loadThemes()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

onMounted(loadThemes)
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
  align-items: center;
}
</style>
