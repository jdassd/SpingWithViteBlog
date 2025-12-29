<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <el-input v-model="filters.keyword" placeholder="Keyword" clearable />
      <el-select v-model="filters.visibility" placeholder="Visibility" clearable>
        <el-option label="Public" value="PUBLIC" />
        <el-option label="Login only" value="LOGIN_ONLY" />
        <el-option label="Whitelist" value="WHITELIST" />
        <el-option label="Private" value="PRIVATE" />
        <el-option label="Admin only" value="ADMIN_ONLY" />
      </el-select>
      <el-select v-model="filters.isNav" placeholder="Nav" clearable>
        <el-option label="Nav" value="true" />
        <el-option label="Hidden" value="false" />
      </el-select>
      <el-button type="primary" @click="loadPages">Filter</el-button>
      <el-button @click="newPage">New Page</el-button>
    </div>
    <el-table :data="pages" stripe>
      <el-table-column prop="title" label="Title" min-width="200" />
      <el-table-column prop="slug" label="Slug" width="160" />
      <el-table-column prop="contentType" label="Type" width="140" />
      <el-table-column prop="visibility" label="Visibility" width="140" />
      <el-table-column prop="isNav" label="Nav" width="100" />
      <el-table-column label="Actions" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="editPage(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="deletePage(row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api/client'
import type { Page } from '@/api/types'

const router = useRouter()
const pages = ref<Page[]>([])
const filters = reactive({
  keyword: '',
  visibility: '',
  isNav: '',
})

const loadPages = async () => {
  pages.value = await api.get<Page[]>('/api/admin/pages', {
    params: {
      keyword: filters.keyword || undefined,
      visibility: filters.visibility || undefined,
      isNav: filters.isNav || undefined,
    },
  })
}

const newPage = () => router.push('/admin/pages/new')
const editPage = (row: Page) => router.push(`/admin/pages/${row.id}`)

const deletePage = async (row: Page) => {
  await ElMessageBox.confirm('Delete this page?', 'Confirm', { type: 'warning' })
  try {
    await api.delete(`/api/admin/pages/${row.id}`)
    ElMessage.success('Deleted')
    await loadPages()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

onMounted(loadPages)
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
  flex-wrap: wrap;
}
</style>
