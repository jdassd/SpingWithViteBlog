<template>
  <section class="admin-section">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="Site" name="site">
        <div class="card-surface panel">
          <el-form label-position="top">
            <el-form-item label="Site name">
              <el-input v-model="settings.site_name" />
            </el-form-item>
            <el-form-item label="Description">
              <el-input v-model="settings.site_description" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item label="Site URL">
              <el-input v-model="settings.site_url" placeholder="http://localhost:8080" />
            </el-form-item>
            <el-form-item label="Home Mode">
              <el-select v-model="settings.home_mode">
                <el-option label="Blog" value="BLOG" />
                <el-option label="Navigation Portal" value="NAV" />
                <el-option label="Theme Home" value="THEME" />
              </el-select>
            </el-form-item>
            <el-button type="primary" @click="saveSection(['site_name','site_description','site_url','home_mode'])">
              Save Site
            </el-button>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Branding" name="branding">
        <div class="card-surface panel">
          <div class="upload-row">
            <span>Logo</span>
            <input type="file" @change="uploadAsset($event, 'logo')" />
          </div>
          <div class="upload-row">
            <span>Favicon</span>
            <input type="file" @change="uploadAsset($event, 'favicon')" />
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="RSS" name="rss">
        <div class="card-surface panel">
          <el-form label-position="top">
            <el-form-item label="Public RSS">
              <el-switch v-model="rss.publicEnabled" />
            </el-form-item>
            <el-form-item label="Private RSS">
              <el-switch v-model="rss.privateEnabled" />
            </el-form-item>
            <el-form-item label="Full Content">
              <el-switch v-model="rss.fullContent" />
            </el-form-item>
            <el-form-item label="Token days">
              <el-input-number v-model="rss.tokenDays" :min="1" />
            </el-form-item>
            <el-button type="primary" @click="saveRss">Save RSS</el-button>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Comments" name="comments">
        <div class="card-surface panel">
          <el-form label-position="top">
            <el-form-item label="Allow guest comments">
              <el-switch v-model="comments.allowGuest" />
            </el-form-item>
            <el-form-item label="Review mode">
              <el-select v-model="comments.reviewMode">
                <el-option label="None" value="" />
                <el-option label="First" value="FIRST" />
                <el-option label="All" value="ALL" />
              </el-select>
            </el-form-item>
            <el-form-item label="Rate limit (per minute)">
              <el-input-number v-model="comments.rateLimit" :min="1" />
            </el-form-item>
            <el-form-item label="Sensitive words (comma separated)">
              <el-input v-model="comments.sensitiveWords" />
            </el-form-item>
            <el-form-item label="Sensitive strategy">
              <el-select v-model="comments.sensitiveStrategy">
                <el-option label="Pending" value="PENDING" />
                <el-option label="Reject" value="REJECT" />
              </el-select>
            </el-form-item>
            <el-button type="primary" @click="saveComments">Save Comments</el-button>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Access" name="access">
        <div class="card-surface panel">
          <el-form label-position="top">
            <el-form-item label="Allow user posting">
              <el-switch v-model="access.allowUserPost" />
            </el-form-item>
            <el-form-item label="Allow user albums">
              <el-switch v-model="access.allowUserAlbum" />
            </el-form-item>
            <el-form-item label="Admin can view private">
              <el-switch v-model="access.adminCanViewPrivate" />
            </el-form-item>
            <el-button type="primary" @click="saveAccess">Save Access</el-button>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Custom Code" name="code">
        <div class="card-surface panel">
          <el-form label-position="top">
            <el-form-item label="Enable custom JS">
              <el-switch v-model="custom.enableJs" />
            </el-form-item>
            <el-form-item label="Custom CSS">
              <el-input v-model="custom.cssContent" type="textarea" :rows="6" />
            </el-form-item>
            <el-form-item label="Custom JS">
              <el-input v-model="custom.jsContent" type="textarea" :rows="6" />
            </el-form-item>
            <el-button type="primary" @click="saveCustomCode">Save Custom Code</el-button>
          </el-form>
          <el-divider />
          <div class="history-grid">
            <div>
              <div class="section-title">CSS Versions</div>
              <el-table :data="cssVersions" size="small">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="createdAt" label="Created" />
                <el-table-column label="Actions" width="120">
                  <template #default="{ row }">
                    <el-button size="small" @click="activateCustom(row.id, 'CSS')">Activate</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div>
              <div class="section-title">JS Versions</div>
              <el-table :data="jsVersions" size="small">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="createdAt" label="Created" />
                <el-table-column label="Actions" width="120">
                  <template #default="{ row }">
                    <el-button size="small" @click="activateCustom(row.id, 'JS')">Activate</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Media" name="media">
        <div class="card-surface panel">
          <el-form label-position="top">
            <el-form-item label="EXIF enabled">
              <el-switch v-model="media.exifEnabled" />
            </el-form-item>
            <el-form-item label="Strip geo location">
              <el-switch v-model="media.stripExifGeo" />
            </el-form-item>
            <el-button type="primary" @click="saveMedia">Save Media</el-button>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Git Sync" name="git">
        <div class="card-surface panel">
          <el-form label-position="top">
            <el-form-item label="Provider">
              <el-select v-model="git.provider">
                <el-option label="GitHub" value="GITHUB" />
                <el-option label="Gitee" value="GITEE" />
              </el-select>
            </el-form-item>
            <el-form-item label="Repo (owner/name)">
              <el-input v-model="git.repo" />
            </el-form-item>
            <el-form-item label="Branch">
              <el-input v-model="git.branch" />
            </el-form-item>
            <el-form-item label="Token">
              <el-input v-model="git.token" type="password" />
            </el-form-item>
            <el-form-item label="Path prefix">
              <el-input v-model="git.pathPrefix" placeholder="/images/" />
            </el-form-item>
            <el-form-item label="Commit message">
              <el-input v-model="git.commitMessage" />
            </el-form-item>
            <el-form-item label="Date partition">
              <el-switch v-model="git.datePartition" />
            </el-form-item>
            <el-form-item label="Overwrite">
              <el-switch v-model="git.overwrite" />
            </el-form-item>
            <div class="git-actions">
              <el-button type="primary" @click="saveGit">Save Git Settings</el-button>
              <el-button @click="testGit">Test Connection</el-button>
            </div>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api/client'
import type { CustomCodeVersion } from '@/api/types'

const activeTab = ref('site')
const settings = reactive<Record<string, string>>({})
const cssVersions = ref<CustomCodeVersion[]>([])
const jsVersions = ref<CustomCodeVersion[]>([])

const rss = reactive({
  publicEnabled: true,
  privateEnabled: true,
  fullContent: false,
  tokenDays: 30,
})

const comments = reactive({
  allowGuest: false,
  reviewMode: '',
  rateLimit: 5,
  sensitiveWords: '',
  sensitiveStrategy: 'PENDING',
})

const access = reactive({
  allowUserPost: false,
  allowUserAlbum: false,
  adminCanViewPrivate: true,
})

const custom = reactive({
  enableJs: false,
  cssContent: '',
  jsContent: '',
})

const media = reactive({
  exifEnabled: true,
  stripExifGeo: true,
})

const git = reactive({
  provider: 'GITHUB',
  repo: '',
  branch: 'main',
  token: '',
  pathPrefix: '/images/',
  commitMessage: 'sync images',
  datePartition: true,
  overwrite: false,
})

const loadSettings = async () => {
  const keys = [
    'site_name',
    'site_description',
    'site_url',
    'home_mode',
    'site_logo',
    'site_favicon',
    'rss_public_enabled',
    'rss_private_enabled',
    'rss_full_content',
    'rss_token_days',
    'allow_guest_comment',
    'comment_review_mode',
    'comment_rate_limit',
    'sensitive_words',
    'sensitive_strategy',
    'allow_user_post',
    'allow_user_album',
    'admin_can_view_private',
    'enable_custom_js',
    'exif_enabled',
    'strip_exif_geo',
    'git_provider',
    'git_repo',
    'git_branch',
    'git_token',
    'git_path_prefix',
    'git_commit_message',
    'git_date_partition',
    'git_overwrite',
  ]
  const data = await api.get<Record<string, string>>('/api/admin/settings', { params: { keys: keys.join(',') } })
  Object.assign(settings, data)
  rss.publicEnabled = data.rss_public_enabled !== 'false'
  rss.privateEnabled = data.rss_private_enabled !== 'false'
  rss.fullContent = data.rss_full_content === 'true'
  rss.tokenDays = parseInt(data.rss_token_days || '30', 10)
  comments.allowGuest = data.allow_guest_comment === 'true'
  comments.reviewMode = data.comment_review_mode || ''
  comments.rateLimit = parseInt(data.comment_rate_limit || '5', 10)
  comments.sensitiveWords = data.sensitive_words || ''
  comments.sensitiveStrategy = data.sensitive_strategy || 'PENDING'
  access.allowUserPost = data.allow_user_post === 'true'
  access.allowUserAlbum = data.allow_user_album === 'true'
  access.adminCanViewPrivate = data.admin_can_view_private !== 'false'
  custom.enableJs = data.enable_custom_js === 'true'
  media.exifEnabled = data.exif_enabled !== 'false'
  media.stripExifGeo = data.strip_exif_geo !== 'false'
  git.provider = data.git_provider || 'GITHUB'
  git.repo = data.git_repo || ''
  git.branch = data.git_branch || 'main'
  git.token = data.git_token || ''
  git.pathPrefix = data.git_path_prefix || '/images/'
  git.commitMessage = data.git_commit_message || 'sync images'
  git.datePartition = data.git_date_partition !== 'false'
  git.overwrite = data.git_overwrite === 'true'
}

const saveSetting = async (key: string, value: string) => {
  await api.post('/api/admin/settings', { key, value })
  settings[key] = value
}

const saveSection = async (keys: string[]) => {
  try {
    await Promise.all(keys.map((key) => saveSetting(key, settings[key] || '')))
    ElMessage.success('Saved')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const saveRss = async () => {
  try {
    await saveSetting('rss_public_enabled', String(rss.publicEnabled))
    await saveSetting('rss_private_enabled', String(rss.privateEnabled))
    await saveSetting('rss_full_content', String(rss.fullContent))
    await saveSetting('rss_token_days', String(rss.tokenDays))
    ElMessage.success('Saved')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const saveComments = async () => {
  try {
    await saveSetting('allow_guest_comment', String(comments.allowGuest))
    await saveSetting('comment_review_mode', comments.reviewMode)
    await saveSetting('comment_rate_limit', String(comments.rateLimit))
    await saveSetting('sensitive_words', comments.sensitiveWords)
    await saveSetting('sensitive_strategy', comments.sensitiveStrategy)
    ElMessage.success('Saved')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const saveAccess = async () => {
  try {
    await saveSetting('allow_user_post', String(access.allowUserPost))
    await saveSetting('allow_user_album', String(access.allowUserAlbum))
    await saveSetting('admin_can_view_private', String(access.adminCanViewPrivate))
    ElMessage.success('Saved')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const saveCustomCode = async () => {
  try {
    await saveSetting('enable_custom_js', String(custom.enableJs))
    if (custom.cssContent.trim()) {
      await api.post('/api/admin/settings/custom-code', { type: 'CSS', content: custom.cssContent })
    }
    if (custom.jsContent.trim()) {
      await api.post('/api/admin/settings/custom-code', { type: 'JS', content: custom.jsContent, enabled: true })
    }
    ElMessage.success('Saved')
    await loadCustomCode()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const loadCustomCode = async () => {
  cssVersions.value = await api.get<CustomCodeVersion[]>('/api/admin/settings/custom-code', { params: { type: 'CSS' } })
  jsVersions.value = await api.get<CustomCodeVersion[]>('/api/admin/settings/custom-code', { params: { type: 'JS' } })
  const activeCss = cssVersions.value.find((item) => item.isActive)
  const activeJs = jsVersions.value.find((item) => item.isActive)
  if (activeCss) {
    custom.cssContent = activeCss.content
  }
  if (activeJs) {
    custom.jsContent = activeJs.content
  }
}

const activateCustom = async (id: number, type: string) => {
  await api.post(`/api/admin/settings/custom-code/${id}/activate`, null, { params: { type } })
  ElMessage.success('Activated')
  await loadCustomCode()
}

const saveMedia = async () => {
  try {
    await saveSetting('exif_enabled', String(media.exifEnabled))
    await saveSetting('strip_exif_geo', String(media.stripExifGeo))
    ElMessage.success('Saved')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const saveGit = async () => {
  try {
    await saveSetting('git_provider', git.provider)
    await saveSetting('git_repo', git.repo)
    await saveSetting('git_branch', git.branch)
    await saveSetting('git_token', git.token)
    await saveSetting('git_path_prefix', git.pathPrefix)
    await saveSetting('git_commit_message', git.commitMessage)
    await saveSetting('git_date_partition', String(git.datePartition))
    await saveSetting('git_overwrite', String(git.overwrite))
    ElMessage.success('Saved')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Save failed')
  }
}

const testGit = async () => {
  try {
    await api.post('/api/admin/albums/git/test')
    ElMessage.success('Connection OK')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Test failed')
  }
}

const uploadAsset = async (event: Event, type: 'logo' | 'favicon') => {
  const target = event.target as HTMLInputElement
  if (!target.files) {
    return
  }
  const file = target.files[0]
  const form = new FormData()
  form.append('file', file)
  try {
    const url = await api.post<string>(`/api/admin/assets/${type}`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    await saveSetting(type === 'logo' ? 'site_logo' : 'site_favicon', url)
    ElMessage.success('Uploaded')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Upload failed')
  } finally {
    target.value = ''
  }
}

onMounted(async () => {
  await loadSettings()
  await loadCustomCode()
})
</script>

<style scoped>
.admin-section {
  display: grid;
  gap: 16px;
}

.panel {
  padding: 18px;
  display: grid;
  gap: 16px;
}

.upload-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.history-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.git-actions {
  display: flex;
  gap: 12px;
}
</style>
