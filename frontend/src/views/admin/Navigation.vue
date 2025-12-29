<template>
  <section class="admin-section">
    <div class="card-surface panel">
      <div class="panel-head">
        <div class="section-title">Navigation Groups</div>
        <el-button type="primary" @click="openGroupEditor()">New Group</el-button>
      </div>
      <div class="group-grid">
        <div v-for="group in groups" :key="group.id" class="group-card card-surface">
          <div class="group-head">
            <div class="group-title">{{ group.name }}</div>
            <div class="group-actions">
              <el-button size="small" @click="openGroupEditor(group)">Edit</el-button>
              <el-button size="small" type="danger" @click="deleteGroup(group)">Delete</el-button>
              <el-button size="small" type="primary" @click="openLinkEditor(group)">Add Link</el-button>
            </div>
          </div>
          <div class="link-list">
            <div v-for="link in group.links || []" :key="link.id" class="link-row">
              <div>
                <div class="link-name">{{ link.name }}</div>
                <div class="muted">{{ link.url }}</div>
              </div>
              <div class="link-actions">
                <el-button size="small" @click="openLinkEditor(group, link)">Edit</el-button>
                <el-button size="small" type="danger" @click="deleteLink(link)">Delete</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card-surface panel">
      <div class="panel-head">
        <div class="section-title">Search Engines</div>
        <el-button type="primary" @click="openEngineEditor()">New Engine</el-button>
      </div>
      <el-table :data="searchEngines" stripe>
        <el-table-column prop="name" label="Name" width="160" />
        <el-table-column prop="queryUrl" label="Query URL" min-width="220" />
        <el-table-column prop="enabled" label="Enabled" width="100" />
        <el-table-column prop="isDefault" label="Default" width="100" />
        <el-table-column label="Actions" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="openEngineEditor(row)">Edit</el-button>
            <el-button size="small" type="danger" @click="deleteEngine(row)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="showGroupEditor" width="420px" :title="editingGroup ? 'Edit Group' : 'New Group'">
      <el-form :model="groupForm" label-position="top">
        <el-form-item label="Name">
          <el-input v-model="groupForm.name" />
        </el-form-item>
        <el-form-item label="Sort Order">
          <el-input-number v-model="groupForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showGroupEditor = false">Cancel</el-button>
        <el-button type="primary" @click="saveGroup">Save</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showLinkEditor" width="520px" :title="editingLink ? 'Edit Link' : 'New Link'">
      <el-form :model="linkForm" label-position="top">
        <el-form-item label="Group">
          <el-select v-model="linkForm.groupId">
            <el-option v-for="group in groups" :key="group.id" :label="group.name" :value="group.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Name">
          <el-input v-model="linkForm.name" />
        </el-form-item>
        <el-form-item label="URL">
          <el-input v-model="linkForm.url" />
        </el-form-item>
        <el-form-item label="Icon URL">
          <el-input v-model="linkForm.icon" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="linkForm.description" />
        </el-form-item>
        <el-form-item label="Open in new">
          <el-switch v-model="linkForm.openInNew" />
        </el-form-item>
        <el-form-item label="Sort Order">
          <el-input-number v-model="linkForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showLinkEditor = false">Cancel</el-button>
        <el-button type="primary" @click="saveLink">Save</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showEngineEditor" width="520px" :title="editingEngine ? 'Edit Engine' : 'New Engine'">
      <el-form :model="engineForm" label-position="top">
        <el-form-item label="Name">
          <el-input v-model="engineForm.name" />
        </el-form-item>
        <el-form-item label="Query URL">
          <el-input v-model="engineForm.queryUrl" placeholder="https://search?q={q}" />
        </el-form-item>
        <el-form-item label="Enabled">
          <el-switch v-model="engineForm.enabled" />
        </el-form-item>
        <el-form-item label="Default">
          <el-switch v-model="engineForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEngineEditor = false">Cancel</el-button>
        <el-button type="primary" @click="saveEngine">Save</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api/client'
import type { NavigationGroup, NavigationLink, SearchEngine } from '@/api/types'

const groups = ref<NavigationGroup[]>([])
const searchEngines = ref<SearchEngine[]>([])

const showGroupEditor = ref(false)
const editingGroup = ref<NavigationGroup | null>(null)
const groupForm = reactive({
  name: '',
  sortOrder: 0,
})

const showLinkEditor = ref(false)
const editingLink = ref<NavigationLink | null>(null)
const linkForm = reactive({
  groupId: 0,
  name: '',
  url: '',
  icon: '',
  description: '',
  openInNew: false,
  sortOrder: 0,
})

const showEngineEditor = ref(false)
const editingEngine = ref<SearchEngine | null>(null)
const engineForm = reactive({
  name: '',
  queryUrl: '',
  enabled: true,
  isDefault: false,
})

const loadNavigation = async () => {
  groups.value = await api.get<NavigationGroup[]>('/api/admin/navigation/groups')
  searchEngines.value = await api.get<SearchEngine[]>('/api/admin/navigation/search-engines')
}

const openGroupEditor = (group?: NavigationGroup) => {
  editingGroup.value = group || null
  groupForm.name = group?.name || ''
  groupForm.sortOrder = group?.sortOrder || 0
  showGroupEditor.value = true
}

const saveGroup = async () => {
  try {
    if (editingGroup.value) {
      await api.put(`/api/admin/navigation/groups/${editingGroup.value.id}`, groupForm)
    } else {
      await api.post('/api/admin/navigation/groups', groupForm)
    }
    ElMessage.success('Saved')
    showGroupEditor.value = false
    await loadNavigation()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const deleteGroup = async (group: NavigationGroup) => {
  await ElMessageBox.confirm('Delete this group?', 'Confirm', { type: 'warning' })
  try {
    await api.delete(`/api/admin/navigation/groups/${group.id}`)
    ElMessage.success('Deleted')
    await loadNavigation()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

const openLinkEditor = (group: NavigationGroup, link?: NavigationLink) => {
  editingLink.value = link || null
  linkForm.groupId = link?.groupId || group.id
  linkForm.name = link?.name || ''
  linkForm.url = link?.url || ''
  linkForm.icon = link?.icon || ''
  linkForm.description = link?.description || ''
  linkForm.openInNew = link?.openInNew || false
  linkForm.sortOrder = link?.sortOrder || 0
  showLinkEditor.value = true
}

const saveLink = async () => {
  try {
    if (editingLink.value) {
      await api.put(`/api/admin/navigation/links/${editingLink.value.id}`, linkForm)
    } else {
      await api.post('/api/admin/navigation/links', linkForm)
    }
    ElMessage.success('Saved')
    showLinkEditor.value = false
    await loadNavigation()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const deleteLink = async (link: NavigationLink) => {
  await ElMessageBox.confirm('Delete this link?', 'Confirm', { type: 'warning' })
  try {
    await api.delete(`/api/admin/navigation/links/${link.id}`)
    ElMessage.success('Deleted')
    await loadNavigation()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

const openEngineEditor = (engine?: SearchEngine) => {
  editingEngine.value = engine || null
  engineForm.name = engine?.name || ''
  engineForm.queryUrl = engine?.queryUrl || ''
  engineForm.enabled = engine?.enabled ?? true
  engineForm.isDefault = engine?.isDefault ?? false
  showEngineEditor.value = true
}

const saveEngine = async () => {
  try {
    if (editingEngine.value) {
      await api.put(`/api/admin/navigation/search-engines/${editingEngine.value.id}`, engineForm)
    } else {
      await api.post('/api/admin/navigation/search-engines', engineForm)
    }
    ElMessage.success('Saved')
    showEngineEditor.value = false
    await loadNavigation()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const deleteEngine = async (engine: SearchEngine) => {
  await ElMessageBox.confirm('Delete this engine?', 'Confirm', { type: 'warning' })
  try {
    await api.delete(`/api/admin/navigation/search-engines/${engine.id}`)
    ElMessage.success('Deleted')
    await loadNavigation()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

onMounted(loadNavigation)
</script>

<style scoped>
.admin-section {
  display: grid;
  gap: 16px;
}

.panel {
  padding: 18px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.group-grid {
  display: grid;
  gap: 16px;
}

.group-card {
  padding: 16px;
  display: grid;
  gap: 12px;
}

.group-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.link-list {
  display: grid;
  gap: 10px;
}

.link-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-radius: 10px;
  background: var(--surface-muted);
}

.link-name {
  font-weight: 600;
}

.link-actions {
  display: flex;
  gap: 8px;
}
</style>
