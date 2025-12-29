<template>
  <div class="page-container">
    <el-result icon="warning" title="需要登录" sub-title="此内容需要登录后访问">
      <template #extra>
        <el-button type="primary" @click="goLogin">前往登录</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

const goLogin = () => {
  router.push({ name: 'login', query: { redirect: route.query.redirect || '/' } });
};
</script>
