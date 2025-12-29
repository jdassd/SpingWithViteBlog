<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <el-input v-model="filters.userId" :placeholder="$t('admin.labels.userId')" />
      <el-input v-model="filters.action" :placeholder="$t('admin.columns.action')" />
      <el-button type="primary" @click="loadLogs">{{ $t('admin.filter') }}</el-button>
    </div>
    <el-table :data="logs" stripe>
      <el-table-column prop="createdAt" :label="$t('admin.columns.time')" width="180" />
      <el-table-column prop="userId" :label="$t('admin.columns.user')" width="100" />
      <el-table-column prop="action" :label="$t('admin.columns.action')" width="200" />
      <el-table-column prop="resourceType" :label="$t('admin.columns.resource')" width="120" />
      <el-table-column prop="resourceId" :label="$t('admin.columns.id')" width="120" />
      <el-table-column prop="result" :label="$t('admin.columns.result')" width="120" />
      <el-table-column prop="message" :label="$t('admin.columns.message')" min-width="180" />
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import api from '@/api/client'
import type { AuditLog } from '@/api/types'

const logs = ref<AuditLog[]>([])
const filters = reactive({
  userId: '',
  action: '',
})

const loadLogs = async () => {
  logs.value = await api.get<AuditLog[]>('/api/admin/audit-logs', {
    params: {
      userId: filters.userId || undefined,
      action: filters.action || undefined,
      page: 1,
      size: 100,
    },
  })
}

onMounted(loadLogs)
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
