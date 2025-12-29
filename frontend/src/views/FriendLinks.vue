<template>
  <div class="friend-links-page">
    <h1 class="page-title">{{ $t('friendLinks.title') }}</h1>
    
    <div v-loading="loading">
      <el-empty v-if="!loading && Object.keys(linksByCategory).length === 0" :description="$t('friendLinks.noLinks')" />
      
      <div v-for="(links, category) in linksByCategory" :key="category" class="category-section">
        <h2 class="category-title">{{ category }}</h2>
        <div class="links-grid">
          <a
            v-for="link in links"
            :key="link.id"
            :href="link.url"
            target="_blank"
            rel="noopener noreferrer"
            class="link-card"
          >
            <div class="link-logo" v-if="link.logoUrl">
              <img :src="link.logoUrl" :alt="link.name" />
            </div>
            <div class="link-logo placeholder" v-else>
              <el-icon size="24"><Link /></el-icon>
            </div>
            <div class="link-info">
              <div class="link-name">{{ link.name }}</div>
              <div class="link-desc" v-if="link.description">{{ link.description }}</div>
            </div>
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '@/api/client'
import { Link } from '@element-plus/icons-vue'
import type { FriendLink } from '@/api/types'

const loading = ref(false)
const linksByCategory = ref<Record<string, FriendLink[]>>({})

const loadLinks = async () => {
  loading.value = true
  try {
    const res = await api.get<{ success: boolean; data: Record<string, FriendLink[]> }>('/api/friend-links')
    if (res.success) {
      linksByCategory.value = res.data || {}
    }
  } catch (err) {
    console.error('Failed to load friend links:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadLinks()
})
</script>

<style scoped>
.friend-links-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 32px;
  color: #333;
}

.category-section {
  margin-bottom: 32px;
}

.category-title {
  font-size: 18px;
  font-weight: 600;
  color: #666;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
  display: inline-block;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.link-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  text-decoration: none;
  transition: all 0.2s;
}

.link-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.link-logo {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.link-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.link-logo.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  color: #999;
}

.link-info {
  flex: 1;
  min-width: 0;
}

.link-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.link-desc {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
