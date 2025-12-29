<template>
  <section class="container state-container">
    <div class="card-surface state-card">
      <div class="section-title">404 - Not Found</div>
      <p class="muted">We could not find the page you were looking for.</p>
      <el-button type="primary" @click="goHome">Back Home</el-button>
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
