<template>
  <div class="admin-series-editor">
    <div class="page-header">
      <h1>{{ isEdit ? $t('series.edit') : $t('series.create') }}</h1>
    </div>
    
    <el-form :model="form" label-width="100px" style="max-width: 600px" v-loading="loading">
      <el-form-item :label="$t('series.titleCol')" required>
        <el-input v-model="form.title" />
      </el-form-item>
      
      <el-form-item :label="$t('series.description')">
        <el-input v-model="form.description" type="textarea" :rows="4" />
      </el-form-item>
      
      <el-form-item :label="$t('series.coverUrl')">
        <el-input v-model="form.coverUrl" placeholder="https://..." />
      </el-form-item>
      
      <el-form-item :label="$t('series.status')">
        <el-select v-model="form.status" style="width: 200px">
          <el-option value="ONGOING" :label="$t('series.ongoing')" />
          <el-option value="COMPLETED" :label="$t('series.completed')" />
        </el-select>
      </el-form-item>
      
      <el-form-item :label="$t('common.visibility')">
        <el-select v-model="form.visibility" style="width: 200px">
          <el-option value="PUBLIC" :label="$t('visibility.public')" />
          <el-option value="LOGIN_ONLY" :label="$t('visibility.login_only')" />
          <el-option value="WHITELIST" :label="$t('visibility.whitelist')" />
          <el-option value="ADMIN_ONLY" :label="$t('visibility.admin_only')" />
        </el-select>
      </el-form-item>
      
      <el-form-item :label="$t('series.sortOrder')">
        <el-input-number v-model="form.sortOrder" :min="0" />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="save">{{ $t('common.save') }}</el-button>
        <el-button @click="$router.back()">{{ $t('common.cancel') }}</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 文章管理 -->
    <template v-if="isEdit">
      <el-divider />
      <h2>{{ $t('series.manageArticles') }}</h2>
      <p class="hint">{{ $t('series.dragToReorder') }}</p>
      
      <el-table :data="articles" v-loading="articlesLoading">
        <el-table-column prop="seriesOrder" :label="$t('series.order')" width="80" />
        <el-table-column prop="title" :label="$t('article.title')" />
        <el-table-column :label="$t('common.actions')" width="100">
          <template #default="{ row }">
            <el-button size="small" text type="danger" @click="removeArticle(row.id)">
              {{ $t('common.remove') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api/client'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => route.params.id !== undefined)
const loading = ref(false)
const articlesLoading = ref(false)

const form = reactive({
  title: '',
  description: '',
  coverUrl: '',
  status: 'ONGOING',
  visibility: 'PUBLIC',
  sortOrder: 0
})

const articles = ref<{ id: number; title: string; seriesOrder: number }[]>([])

const loadSeries = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const res = await api.get<{ success: boolean; data: any }>(`/api/admin/series/${route.params.id}`)
    if (res.success && res.data) {
      form.title = res.data.title
      form.description = res.data.description || ''
      form.coverUrl = res.data.coverUrl || ''
      form.status = res.data.status
      form.visibility = res.data.visibility
      form.sortOrder = res.data.sortOrder || 0
    }
  } catch (err) {
    console.error('Failed to load series:', err)
  } finally {
    loading.value = false
  }
}

const loadArticles = async () => {
  if (!isEdit.value) return
  articlesLoading.value = true
  try {
    const res = await api.get<{ success: boolean; data: any[] }>(`/api/admin/series/${route.params.id}/articles`)
    if (res.success) {
      articles.value = res.data || []
    }
  } catch (err) {
    console.error('Failed to load articles:', err)
  } finally {
    articlesLoading.value = false
  }
}

const save = async () => {
  if (!form.title.trim()) {
    ElMessage.error('请输入系列标题')
    return
  }
  loading.value = true
  try {
    if (isEdit.value) {
      await api.put(`/api/admin/series/${route.params.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await api.post('/api/admin/series', form)
      ElMessage.success('创建成功')
      router.push('/admin/series')
    }
  } catch (err) {
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
}

const removeArticle = async (articleId: number) => {
  // TODO: implement remove article from series
  console.log('Remove article:', articleId)
  ElMessage.info('移除文章功能待实现')
}

onMounted(() => {
  loadSeries()
  loadArticles()
})
</script>

<style scoped>
.admin-series-editor {
  padding: 20px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 24px 0;
}

.hint {
  color: #999;
  font-size: 13px;
  margin-bottom: 12px;
}

@media (max-width: 640px) {
  .admin-series-editor {
    padding: 12px;
  }

  .page-header h1 {
    font-size: 20px;
  }

  .el-form {
    max-width: 100% !important;
  }
}
</style>
