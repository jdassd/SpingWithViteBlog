<template>
  <div class="admin-series">
    <div class="page-header">
      <h1>{{ $t('menu.series') }}</h1>
      <el-button type="primary" @click="$router.push('/admin/series/new')">
        <el-icon><Plus /></el-icon>
        {{ $t('series.create') }}
      </el-button>
    </div>
    
    <el-table :data="seriesList" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" :label="$t('series.titleCol')" min-width="200">
        <template #default="{ row }">
          <div class="series-title-cell">
            <img v-if="row.coverUrl" :src="row.coverUrl" class="cover-thumb" />
            <span>{{ row.title }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="status" :label="$t('series.status')" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'info'" size="small">
            {{ row.status === 'COMPLETED' ? $t('series.completed') : $t('series.ongoing') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="visibility" :label="$t('common.visibility')" width="100">
        <template #default="{ row }">
          {{ $t(`visibility.${row.visibility.toLowerCase()}`) }}
        </template>
      </el-table-column>
      <el-table-column prop="articleCount" :label="$t('series.articleCount')" width="100" />
      <el-table-column prop="sortOrder" :label="$t('series.sortOrder')" width="80" />
      <el-table-column :label="$t('common.actions')" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" text @click="$router.push(`/admin/series/${row.id}`)">
            {{ $t('common.edit') }}
          </el-button>
          <el-button size="small" text type="danger" @click="deleteSeries(row)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '@/api/client'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { Series } from '@/api/types'

const loading = ref(false)
const seriesList = ref<Series[]>([])

const loadSeries = async () => {
  loading.value = true
  try {
    const res = await api.get<{ success: boolean; data: Series[] }>('/api/admin/series')
    if (res.success) {
      seriesList.value = res.data || []
    }
  } catch (err) {
    console.error('Failed to load series:', err)
  } finally {
    loading.value = false
  }
}

const deleteSeries = async (series: Series) => {
  try {
    await ElMessageBox.confirm(`确定要删除系列 "${series.title}" 吗？`, '确认删除', { type: 'warning' })
    await api.delete(`/api/admin/series/${series.id}`)
    ElMessage.success('删除成功')
    loadSeries()
  } catch (err) {
    // cancelled
  }
}

onMounted(() => {
  loadSeries()
})
</script>

<style scoped>
.admin-series {
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

.series-title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cover-thumb {
  width: 40px;
  height: 30px;
  object-fit: cover;
  border-radius: 4px;
}
</style>
