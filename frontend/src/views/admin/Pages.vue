<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <el-input v-model="filters.keyword" :placeholder="$t('admin.labels.keyword')" clearable />
      <el-select v-model="filters.visibility" :placeholder="$t('admin.columns.visibility')" clearable>
        <el-option :label="$t('admin.visibilityOptions.public')" value="PUBLIC" />
        <el-option :label="$t('admin.visibilityOptions.loginOnly')" value="LOGIN_ONLY" />
        <el-option :label="$t('admin.visibilityOptions.whitelist')" value="WHITELIST" />
        <el-option :label="$t('admin.visibilityOptions.private')" value="PRIVATE" />
        <el-option :label="$t('admin.visibilityOptions.adminOnly')" value="ADMIN_ONLY" />
      </el-select>
      <el-select v-model="filters.isNav" :placeholder="$t('admin.columns.nav')" clearable>
        <el-option :label="$t('admin.columns.nav')" value="true" />
        <el-option :label="$t('admin.labels.hidden')" value="false" />
      </el-select>
      <el-button type="primary" @click="loadPages">{{ $t('admin.filter') }}</el-button>
      <el-button @click="newPage">{{ $t('admin.newPage') }}</el-button>
    </div>
    <el-table :data="pages" stripe>
      <el-table-column prop="title" :label="$t('admin.columns.title')" min-width="200" />
      <el-table-column prop="slug" :label="$t('admin.columns.slug')" width="160" />
      <el-table-column prop="contentType" :label="$t('admin.columns.type')" width="140" />
      <el-table-column prop="visibility" :label="$t('admin.columns.visibility')" width="140" />
      <el-table-column prop="isNav" :label="$t('admin.columns.nav')" width="100" />
      <el-table-column :label="$t('admin.columns.actions')" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="editPage(row)">{{ $t('admin.actions.edit') }}</el-button>
          <el-button size="small" type="danger" @click="deletePage(row)">{{ $t('admin.actions.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import api from '@/api/client'
import type { Page } from '@/api/types'

const { t } = useI18n()
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
  await ElMessageBox.confirm(t('admin.confirms.deletePage'), t('admin.confirms.confirm'), { type: 'warning' })
  try {
    await api.delete(`/api/admin/pages/${row.id}`)
    ElMessage.success(t('admin.messages.deleted'))
    await loadPages()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.deleteFailed'))
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

@media (max-width: 640px) {
  .toolbar {
    padding: 12px;
  }

  .toolbar > * {
    flex: 1 1 100%;
  }
}
</style>
