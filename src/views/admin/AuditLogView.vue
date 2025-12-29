<template>
  <div>
    <div class="section-title">审计日志</div>
    <el-table :data="logs" stripe>
      <el-table-column prop="createdAt" label="时间" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column prop="actorName" label="操作者" width="120" />
      <el-table-column prop="action" label="动作" width="160" />
      <el-table-column prop="target" label="资源" min-width="200" />
      <el-table-column prop="result" label="结果" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.result === 'SUCCESS' ? 'success' : 'danger'">
            {{ scope.row.result }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="detail" label="备注" min-width="200" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import dayjs from 'dayjs';
import { useSiteStore } from '../../stores/site';

const site = useSiteStore();
const logs = computed(() => site.auditLogs);

const formatTime = (value: string) => dayjs(value).format('YYYY-MM-DD HH:mm');
</script>
