<template>
  <div class="favorites-page">
    <div class="page-header">
      <h1>{{ $t('favorites.title') }}</h1>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        {{ $t('favorites.create') }}
      </el-button>
    </div>
    
    <div v-loading="loading">
      <el-empty v-if="!loading && favorites.length === 0" :description="$t('favorites.noFavorites')" />
      
      <div class="favorites-list">
        <div v-for="fav in favorites" :key="fav.id" class="favorite-card" @click="viewFavorite(fav)">
          <div class="favorite-header">
            <el-icon class="folder-icon" :size="24">
              <Folder />
            </el-icon>
            <div class="favorite-info">
              <div class="favorite-name">
                {{ fav.name }}
                <el-tag v-if="fav.isDefault" size="small" type="info">{{ $t('favorites.default') }}</el-tag>
                <el-tag v-if="fav.isPublic" size="small" type="success">{{ $t('favorites.public') }}</el-tag>
              </div>
              <div class="favorite-desc" v-if="fav.description">{{ fav.description }}</div>
            </div>
          </div>
          <div class="favorite-actions" @click.stop>
            <el-button v-if="!fav.isDefault" size="small" text @click="editFavorite(fav)">
              {{ $t('common.edit') }}
            </el-button>
            <el-button v-if="!fav.isDefault" size="small" text type="danger" @click="deleteFavorite(fav)">
              {{ $t('common.delete') }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Create/Edit Dialog -->
    <el-dialog v-model="showCreateDialog" :title="editingFavorite ? $t('favorites.edit') : $t('favorites.create')" width="400px">
      <el-form :model="form" label-position="top">
        <el-form-item :label="$t('favorites.name')">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item :label="$t('favorites.description')">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item :label="$t('favorites.isPublic')">
          <el-switch v-model="form.isPublic" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="saveFavorite">{{ $t('common.save') }}</el-button>
      </template>
    </el-dialog>
    
    <!-- Articles Drawer -->
    <el-drawer v-model="showArticlesDrawer" :title="selectedFavorite?.name" size="500px">
      <div v-loading="articlesLoading">
        <el-empty v-if="!articlesLoading && favoriteArticles.length === 0" :description="$t('favorites.noArticles')" />
        <div v-for="article in favoriteArticles" :key="article.id" class="article-item">
          <router-link :to="`/articles/${article.id}`" class="article-title">
            {{ article.title }}
          </router-link>
          <div class="article-meta">
            {{ formatDate(article.favoritedAt) }}
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import api from '@/api/client'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Folder } from '@element-plus/icons-vue'
import type { Favorite } from '@/api/types'

interface FavoriteArticleItem {
  id: number
  title: string
  summary?: string
  coverUrl?: string
  favoritedAt: string
}

const loading = ref(false)
const favorites = ref<Favorite[]>([])
const showCreateDialog = ref(false)
const editingFavorite = ref<Favorite | null>(null)
const showArticlesDrawer = ref(false)
const selectedFavorite = ref<Favorite | null>(null)
const articlesLoading = ref(false)
const favoriteArticles = ref<FavoriteArticleItem[]>([])

const form = reactive({
  name: '',
  description: '',
  isPublic: false
})

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString()
}

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await api.get<Favorite[]>('/api/favorites')
    favorites.value = res || []
  } catch (err) {
    console.error('Failed to load favorites:', err)
  } finally {
    loading.value = false
  }
}

const viewFavorite = async (fav: Favorite) => {
  selectedFavorite.value = fav
  showArticlesDrawer.value = true
  articlesLoading.value = true
  try {
    const res = await api.get<FavoriteArticleItem[]>(`/api/favorites/${fav.id}/articles`)
    favoriteArticles.value = res || []
  } catch (err) {
    console.error('Failed to load articles:', err)
  } finally {
    articlesLoading.value = false
  }
}

const editFavorite = (fav: Favorite) => {
  editingFavorite.value = fav
  form.name = fav.name
  form.description = fav.description || ''
  form.isPublic = fav.isPublic
  showCreateDialog.value = true
}

const saveFavorite = async () => {
  if (!form.name.trim()) {
    ElMessage.error('请输入收藏夹名称')
    return
  }
  try {
    if (editingFavorite.value) {
      await api.put(`/api/favorites/${editingFavorite.value.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await api.post('/api/favorites', form)
      ElMessage.success('创建成功')
    }
    showCreateDialog.value = false
    editingFavorite.value = null
    form.name = ''
    form.description = ''
    form.isPublic = false
    loadFavorites()
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

const deleteFavorite = async (fav: Favorite) => {
  try {
    await ElMessageBox.confirm('确定要删除这个收藏夹吗？其中的文章会移动到默认收藏夹。', '确认删除')
    await api.delete(`/api/favorites/${fav.id}`)
    ElMessage.success('删除成功')
    loadFavorites()
  } catch (err) {
    // cancelled
  }
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.favorite-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.2s;
}

.favorite-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.favorite-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.folder-icon {
  color: #ffa940;
}

.favorite-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.favorite-desc {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.article-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.article-item:last-child {
  border-bottom: none;
}

.article-title {
  font-size: 15px;
  color: #333;
  text-decoration: none;
}

.article-title:hover {
  color: #409eff;
}

.article-meta {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>
