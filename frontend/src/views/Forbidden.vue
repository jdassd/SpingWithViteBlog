<template>
  <section class="container state-container">
    <div class="card-surface state-card">
      <div class="section-title">403 - {{ $t('error.forbidden') }}</div>
      <p class="muted">{{ $t('error.forbiddenDesc') }}</p>
      <el-button type="primary" @click="goHome">{{ $t('error.backHome') }}</el-button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
const goHome = () => router.push('/')
</script>

<style scoped>
.state-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.state-card {
  padding: 24px;
  display: grid;
  gap: 12px;
  width: 420px;
  text-align: center;
}
</style>
