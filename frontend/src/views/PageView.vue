<template>
  <section class="container">
    <div v-if="loading" class="card-surface page-card">
      <el-skeleton :rows="5" animated />
    </div>
    <div v-else-if="error" class="card-surface page-card">
      <div class="section-title">{{ error }}</div>
      <el-button class="mt" @click="goBack">Back</el-button>
    </div>
    <div v-else class="card-surface page-card">
      <h1 class="serif">{{ page?.title }}</h1>
      <ContentRenderer :rendered-content="page?.contentHtml" />
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api/client'
import type { Page } from '@/api/types'
import ContentRenderer from '@/components/ContentRenderer.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const error = ref('')
const page = ref<Page | null>(null)

const loadPage = async () => {
  loading.value = true
  error.value = ''
  try {
    const data = await api.get<Page>(`/api/public/pages/${route.params.slug}`)
    if (data.externalUrl) {
      window.location.href = data.externalUrl
      return
    }
    page.value = data
  } catch (err: any) {
    error.value = err?.message || 'Page not found'
  } finally {
    loading.value = false
  }
}

const goBack = () => router.back()

onMounted(loadPage)
</script>

<style scoped>
.page-card {
  padding: 24px;
  display: grid;
  gap: 16px;
}

.mt {
  margin-top: 12px;
}
</style>
