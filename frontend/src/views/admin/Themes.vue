<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <input type="file" @change="importTheme" />
      <el-checkbox v-model="activateOnImport">{{ $t('admin.labels.activateAfterImport') }}</el-checkbox>
    </div>
    <el-table :data="themes" stripe>
      <el-table-column prop="name" :label="$t('admin.columns.name')" min-width="160" />
      <el-table-column prop="version" :label="$t('admin.columns.version')" width="120" />
      <el-table-column prop="author" :label="$t('admin.columns.author')" width="140" />
      <el-table-column prop="isActive" :label="$t('admin.columns.active')" width="100" />
      <el-table-column :label="$t('admin.columns.actions')" width="320">
        <template #default="{ row }">
          <el-button size="small" @click="activateTheme(row)">{{ $t('admin.actions.activate') }}</el-button>
          <el-button size="small" @click="openConfig(row)">{{ $t('admin.actions.config') }}</el-button>
          <el-button size="small" @click="exportTheme(row)">{{ $t('admin.actions.export') }}</el-button>
          <el-button size="small" type="danger" @click="deleteTheme(row)" :disabled="row.isActive">{{ $t('admin.actions.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showConfig" width="600px" :title="$t('admin.dialogs.themeConfig')">
      <el-input v-model="configJson" type="textarea" :rows="10" />
      <template #footer>
        <el-button @click="showConfig = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="saveConfig">{{ $t('common.save') }}</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import api from '@/api/client'
import type { Theme } from '@/api/types'

const { t } = useI18n()
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
    ElMessage.success(t('admin.messages.imported'))
    await loadThemes()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.importFailed'))
  } finally {
    target.value = ''
  }
}

const activateTheme = async (theme: Theme) => {
  await api.post(`/api/admin/themes/${theme.id}/activate`)
  ElMessage.success(t('admin.messages.activated'))
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
    ElMessage.success(t('admin.messages.saved'))
    showConfig.value = false
    await loadThemes()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.saveFailed'))
  }
}

const exportTheme = async (theme: Theme) => {
  const url = `/api/admin/themes/${theme.id}/export?includeCustomCode=false`
  window.open(url, '_blank')
}

const deleteTheme = async (theme: Theme) => {
  await ElMessageBox.confirm(t('admin.confirms.deleteTheme'), t('admin.confirms.confirm'), { type: 'warning' })
  try {
    await api.delete(`/api/admin/themes/${theme.id}`)
    ElMessage.success(t('admin.messages.deleted'))
    await loadThemes()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.deleteFailed'))
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

@media (max-width: 640px) {
  .toolbar {
    flex-direction: column;
    align-items: flex-start;
    padding: 12px;
  }
}
</style>
