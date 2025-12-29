<template>
  <div class="public-shell">
    <header class="public-header">
      <div class="container header-inner">
        <RouterLink to="/" class="brand">
          <img v-if="logo" :src="logo" alt="logo" class="brand-logo" />
          <div class="brand-text">
            <div class="brand-title">{{ siteName }}</div>
            <div class="brand-subtitle muted">{{ siteDescription }}</div>
          </div>
        </RouterLink>
        <nav class="nav-links">
          <RouterLink to="/" class="nav-link">Home</RouterLink>
          <RouterLink to="/articles" class="nav-link">Articles</RouterLink>
          <RouterLink to="/albums" class="nav-link">Albums</RouterLink>
          <RouterLink to="/search" class="nav-link">Search</RouterLink>
          <template v-for="page in navPages" :key="page.id">
            <a
              v-if="page.externalUrl"
              class="nav-link"
              :href="page.externalUrl"
              target="_blank"
              rel="noreferrer"
            >
              {{ page.title }}
            </a>
            <RouterLink v-else class="nav-link" :to="`/pages/${page.slug}`">
              {{ page.title }}
            </RouterLink>
          </template>
        </nav>
        <div class="header-actions">
          <template v-if="auth.isAuthenticated">
            <el-dropdown>
              <span class="user-chip">
                {{ auth.user?.username }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goProfile">Profile</el-dropdown-item>
                  <el-dropdown-item @click="goMyArticles">My Articles</el-dropdown-item>
                  <el-dropdown-item @click="goMyAlbums">My Albums</el-dropdown-item>
                  <el-dropdown-item v-if="auth.isAdmin" @click="goAdmin">Admin</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">Logout</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <RouterLink to="/login" class="nav-button">Login</RouterLink>
            <RouterLink to="/register" class="nav-button ghost">Register</RouterLink>
          </template>
        </div>
      </div>
    </header>
    <main>
      <RouterView />
    </main>
    <footer class="public-footer">
      <div class="container footer-inner">
        <span class="muted">© {{ new Date().getFullYear() }} {{ siteName }}</span>
        <span class="muted">Built with Spring + Vite</span>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useSiteStore } from '@/stores/site'

const auth = useAuthStore()
const site = useSiteStore()
const router = useRouter()

const siteName = computed(() => site.settings.site_name || 'Blog')
const siteDescription = computed(() => site.settings.site_description || 'Ideas, notes, and drafts')
const logo = computed(() => site.settings.site_logo || '')
const navPages = computed(() => site.navPages)

const appliedLinks: HTMLLinkElement[] = []
const appliedScripts: HTMLScriptElement[] = []

const handleLogout = async () => {
  await auth.logout()
  router.push('/')
}

const goAdmin = () => router.push('/admin')
const goProfile = () => router.push('/profile')
const goMyArticles = () => router.push('/me/articles')
const goMyAlbums = () => router.push('/me/albums')

const cleanupThemeAssets = () => {
  appliedLinks.forEach((link) => link.remove())
  appliedScripts.forEach((script) => script.remove())
  appliedLinks.length = 0
  appliedScripts.length = 0
}

const applyThemeAssets = () => {
  cleanupThemeAssets()
  if (!site.activeTheme?.publicPath || !site.activeTheme.themeJson) {
    return
  }
  let meta: any
  try {
    meta = JSON.parse(site.activeTheme.themeJson)
  } catch {
    return
  }
  const cssFiles = meta?.assets?.css || meta?.css || []
  const jsFiles = meta?.assets?.js || meta?.js || []
  cssFiles.forEach((file: string) => {
    const link = document.createElement('link')
    link.rel = 'stylesheet'
    link.href = `${site.activeTheme?.publicPath}/${file}`
    document.head.appendChild(link)
    appliedLinks.push(link)
  })
  jsFiles.forEach((file: string) => {
    const script = document.createElement('script')
    script.src = `${site.activeTheme?.publicPath}/${file}`
    script.defer = true
    document.body.appendChild(script)
    appliedScripts.push(script)
  })
}

onMounted(async () => {
  await site.loadPublic()
  applyThemeAssets()
})

watch(
  () => site.activeTheme,
  () => applyThemeAssets()
)

onBeforeUnmount(() => {
  cleanupThemeAssets()
})
</script>

<style scoped>
.public-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.public-header {
  position: sticky;
  top: 0;
  z-index: 20;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(18px);
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.header-inner {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 24px;
  min-height: 72px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-logo {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  object-fit: cover;
  border: 1px solid rgba(15, 23, 42, 0.12);
  background: #fff;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
}

.brand-title {
  font-size: 20px;
  font-weight: 600;
  font-family: var(--font-display);
  letter-spacing: -0.02em;
}

.brand-subtitle {
  font-size: 12px;
}

.nav-links {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  align-items: center;
}

.nav-link {
  position: relative;
  padding: 6px 4px;
  font-weight: 500;
  color: var(--ink-muted);
  transition: color 0.2s ease;
}

.nav-link::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: -6px;
  height: 2px;
  border-radius: 999px;
  background: var(--brand);
  opacity: 0;
  transform: scaleX(0.6);
  transition: transform 0.2s ease, opacity 0.2s ease;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: var(--ink);
}

.nav-link:hover::after,
.nav-link.router-link-active::after {
  opacity: 1;
  transform: scaleX(1);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-button {
  padding: 8px 14px;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--brand), #1d4ed8);
  color: #fff;
  font-weight: 600;
  box-shadow: 0 10px 22px rgba(30, 94, 255, 0.25);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.nav-button.ghost {
  background: rgba(30, 94, 255, 0.08);
  color: var(--brand);
  border: 1px solid rgba(30, 94, 255, 0.3);
  box-shadow: none;
}

.nav-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 28px rgba(30, 94, 255, 0.28);
}

.nav-button.ghost:hover {
  background: rgba(30, 94, 255, 0.14);
  box-shadow: none;
}

.user-chip {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(15, 23, 42, 0.12);
  cursor: pointer;
  font-weight: 600;
}

.public-footer {
  margin-top: auto;
  border-top: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.75);
}

.footer-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}

@media (max-width: 900px) {
  .header-inner {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  .nav-links {
    gap: 8px;
  }
  .header-actions {
    justify-content: space-between;
  }
}
</style>
