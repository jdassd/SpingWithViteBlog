<template>
  <div class="admin-friend-links">
    <div class="page-header">
      <h1>{{ $t('menu.friendLinks') }}</h1>
      <div class="header-actions">
        <el-button @click="checkAllLinks" :loading="checkingAll">
          {{ $t('friendLinks.checkAll') }}
        </el-button>
        <el-button type="primary" @click="openEditor()">
          <el-icon><Plus /></el-icon>
          {{ $t('friendLinks.create') }}
        </el-button>
      </div>
    </div>
    
    <el-table :data="links" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" :label="$t('friendLinks.name')" min-width="120">
        <template #default="{ row }">
          <div class="link-cell">
            <img v-if="row.logoUrl" :src="row.logoUrl" class="logo-thumb" />
            <span>{{ row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="url" :label="$t('friendLinks.url')" min-width="200">
        <template #default="{ row }">
          <a :href="row.url" target="_blank" rel="noopener noreferrer">{{ row.url }}</a>
        </template>
      </el-table-column>
      <el-table-column prop="category" :label="$t('friendLinks.category')" width="100" />
      <el-table-column prop="status" :label="$t('friendLinks.status')" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isEnabled" :label="$t('friendLinks.enabled')" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.isEnabled" @change="toggleEnabled(row)" />
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" :label="$t('series.sortOrder')" width="80" />
      <el-table-column :label="$t('common.actions')" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" text @click="checkLink(row)">
            {{ $t('friendLinks.check') }}
          </el-button>
          <el-button size="small" text @click="openEditor(row)">
            {{ $t('common.edit') }}
          </el-button>
          <el-button size="small" text type="danger" @click="deleteLink(row)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- Editor Dialog -->
    <el-dialog v-model="showEditor" :title="editingLink ? $t('friendLinks.edit') : $t('friendLinks.create')" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item :label="$t('friendLinks.name')" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item :label="$t('friendLinks.url')" required>
          <el-input v-model="form.url" placeholder="https://..." />
        </el-form-item>
        <el-form-item :label="$t('friendLinks.description')">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item :label="$t('friendLinks.logoUrl')">
          <el-input v-model="form.logoUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item :label="$t('friendLinks.category')">
          <el-input v-model="form.category" />
        </el-form-item>
        <el-form-item :label="$t('series.sortOrder')">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item :label="$t('friendLinks.enabled')">
          <el-switch v-model="form.isEnabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditor = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="save">{{ $t('common.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { api } from '@/api/client'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FriendLink } from '@/api/types'

const loading = ref(false)
const checkingAll = ref(false)
const links = ref<FriendLink[]>([])
const showEditor = ref(false)
const editingLink = ref<FriendLink | null>(null)

const form = reactive({
  name: '',
  url: '',
  description: '',
  logoUrl: '',
  category: '',
  sortOrder: 0,
  isEnabled: true
})

const statusType = (status: string) => {
  if (status === 'OK') return 'success'
  if (status === 'FAILED') return 'danger'
  return 'info'
}

const loadLinks = async () => {
  loading.value = true
  try {
    const res = await api.get<{ success: boolean; data: FriendLink[] }>('/api/admin/friend-links')
    if (res.success) {
      links.value = res.data || []
    }
  } catch (err) {
    console.error('Failed to load links:', err)
  } finally {
    loading.value = false
  }
}

const openEditor = (link?: FriendLink) => {
  if (link) {
    editingLink.value = link
    form.name = link.name
    form.url = link.url
    form.description = link.description || ''
    form.logoUrl = link.logoUrl || ''
    form.category = link.category || ''
    form.sortOrder = link.sortOrder
    form.isEnabled = link.isEnabled
  } else {
    editingLink.value = null
    form.name = ''
    form.url = ''
    form.description = ''
    form.logoUrl = ''
    form.category = ''
    form.sortOrder = 0
    form.isEnabled = true
  }
  showEditor.value = true
}

const save = async () => {
  if (!form.name.trim() || !form.url.trim()) {
    ElMessage.error('请填写名称和URL')
    return
  }
  try {
    if (editingLink.value) {
      await api.put(`/api/admin/friend-links/${editingLink.value.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await api.post('/api/admin/friend-links', form)
      ElMessage.success('创建成功')
    }
    showEditor.value = false
    loadLinks()
  } catch (err) {
    ElMessage.error('保存失败')
  }
}

const deleteLink = async (link: FriendLink) => {
  try {
    await ElMessageBox.confirm(`确定要删除 "${link.name}" 吗？`, '确认删除', { type: 'warning' })
    await api.delete(`/api/admin/friend-links/${link.id}`)
    ElMessage.success('删除成功')
    loadLinks()
  } catch (err) {
    // cancelled
  }
}

const toggleEnabled = async (link: FriendLink) => {
  try {
    await api.put(`/api/admin/friend-links/${link.id}`, { ...link })
  } catch (err) {
    ElMessage.error('更新失败')
  }
}

const checkLink = async (link: FriendLink) => {
  try {
    const res = await api.post<{ success: boolean; data: { status: string } }>(`/api/admin/friend-links/${link.id}/check`)
    if (res.success) {
      ElMessage.success(`检测结果: ${res.data.status}`)
      loadLinks()
    }
  } catch (err) {
    ElMessage.error('检测失败')
  }
}

const checkAllLinks = async () => {
  checkingAll.value = true
  try {
    await api.post('/api/admin/friend-links/check-all')
    ElMessage.success('批量检测完成')
    loadLinks()
  } catch (err) {
    ElMessage.error('批量检测失败')
  } finally {
    checkingAll.value = false
  }
}

onMounted(() => {
  loadLinks()
})
</script>

<style scoped>
.admin-friend-links {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.link-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-thumb {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  object-fit: cover;
}
</style>
