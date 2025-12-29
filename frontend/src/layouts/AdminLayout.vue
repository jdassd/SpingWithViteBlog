<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div class="sidebar-brand">
        <span class="brand-text">{{ $t('nav.admin') }}</span>
      </div>
      <el-menu :default-active="activePath" class="menu" router>
        <el-menu-item index="/admin/dashboard">{{ $t('admin.dashboard') }}</el-menu-item>
        <el-menu-item index="/admin/articles">{{ $t('admin.articles') }}</el-menu-item>
        <el-menu-item index="/admin/series">{{ $t('menu.series') }}</el-menu-item>
        <el-menu-item index="/admin/comments">{{ $t('admin.comments') }}</el-menu-item>
        <el-menu-item index="/admin/pages">{{ $t('admin.pages') }}</el-menu-item>
        <el-menu-item index="/admin/navigation">{{ $t('admin.navigation') }}</el-menu-item>
        <el-menu-item index="/admin/albums">{{ $t('admin.albums') }}</el-menu-item>
        <el-menu-item index="/admin/friend-links">{{ $t('menu.friendLinks') }}</el-menu-item>
        <el-menu-item index="/admin/analytics">{{ $t('menu.analytics') }}</el-menu-item>
        <el-menu-item index="/admin/themes">{{ $t('admin.themes') }}</el-menu-item>
        <el-menu-item index="/admin/settings">{{ $t('admin.settings') }}</el-menu-item>
        <el-menu-item index="/admin/users">{{ $t('admin.users') }}</el-menu-item>
        <el-menu-item index="/admin/audit-logs">{{ $t('admin.auditLogs') }}</el-menu-item>
      </el-menu>
    </aside>
    <section class="admin-main">
      <header class="admin-topbar">
        <div class="topbar-title">{{ pageTitle }}</div>
        <div class="topbar-actions">
          <el-dropdown @command="handleLanguageChange" class="lang-switcher">
            <span class="lang-chip">🌐 {{ currentLanguageLabel }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-for="lang in languageOptions" :key="lang.value" :command="lang.value">
                  {{ lang.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button size="small" @click="goHome">{{ $t('nav.home') }}</el-button>
          <el-dropdown>
            <span class="user-chip">{{ auth.user?.username }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goProfile">{{ $t('nav.profile') }}</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">{{ $t('nav.logout') }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <div class="admin-content">
        <RouterView />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { setLocale, getLocale, languageOptions } from '@/i18n'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const activePath = computed(() => route.path)
const pageTitle = computed(() => (route.meta?.title as string) || 'Admin')

// Language switcher
const currentLanguageLabel = computed(() => {
  const current = getLocale()
  return languageOptions.find((l) => l.value === current)?.label || '简体中文'
})

const handleLanguageChange = (locale: string) => {
  setLocale(locale)
}

const handleLogout = async () => {
  await auth.logout()
  router.push('/login')
}

const goHome = () => router.push('/')
const goProfile = () => router.push('/profile')
</script>

<style scoped>
.admin-shell {
  display: grid;
  grid-template-columns: 240px 1fr;
  min-height: 100vh;
  background: var(--bg);
}

.admin-sidebar {
  background: #0b1220;
  color: #e2e8f0;
  padding: 20px 0;
  border-right: 1px solid rgba(15, 23, 42, 0.6);
}

.sidebar-brand {
  padding: 0 20px 18px;
  font-weight: 700;
  letter-spacing: 0.4px;
  text-transform: uppercase;
  font-size: 12px;
  color: rgba(226, 232, 240, 0.8);
}

.menu {
  background: transparent;
  border: none;
}

.menu :deep(.el-menu-item) {
  color: rgba(226, 232, 240, 0.8);
  border-radius: 10px;
  margin: 4px 12px;
}

.menu :deep(.el-menu-item:hover) {
  background: rgba(30, 94, 255, 0.16);
  color: #fff;
}

.menu :deep(.el-menu-item.is-active) {
  background: rgba(30, 94, 255, 0.24);
  color: #fff;
  box-shadow: inset 0 0 0 1px rgba(30, 94, 255, 0.4);
}

.admin-main {
  display: flex;
  flex-direction: column;
}

.admin-topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 28px;
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
}

.topbar-title {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 10px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid rgba(15, 23, 42, 0.12);
  cursor: pointer;
}

.lang-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid rgba(15, 23, 42, 0.12);
  cursor: pointer;
  font-size: 13px;
}

.admin-content {
  padding: 28px 32px 50px;
}

@media (max-width: 980px) {
  .admin-shell {
    grid-template-columns: 1fr;
  }
  .admin-sidebar {
    position: sticky;
    top: 0;
    z-index: 5;
  }
}
</style>
