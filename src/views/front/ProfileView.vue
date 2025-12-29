<template>
  <div class="page-container">
    <div class="layout-main">
      <div class="section-title">个人资料</div>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ auth.currentUser?.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ auth.currentUser?.email }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ auth.currentUser?.role }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatTime(auth.currentUser?.createdAt) }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script setup lang="ts">
import dayjs from 'dayjs';
import { useAuthStore } from '../../stores/auth';

const auth = useAuthStore();

const formatTime = (value?: string) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-');
</script>
