<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div class="sidebar-brand">
        <span class="brand-text">Admin Console</span>
      </div>
      <el-menu :default-active="activePath" class="menu" router>
        <el-menu-item index="/admin/dashboard">Dashboard</el-menu-item>
        <el-menu-item index="/admin/articles">Articles</el-menu-item>
        <el-menu-item index="/admin/comments">Comments</el-menu-item>
        <el-menu-item index="/admin/pages">Pages</el-menu-item>
        <el-menu-item index="/admin/navigation">Navigation</el-menu-item>
        <el-menu-item index="/admin/albums">Albums</el-menu-item>
        <el-menu-item index="/admin/themes">Themes</el-menu-item>
        <el-menu-item index="/admin/settings">Settings</el-menu-item>
        <el-menu-item index="/admin/users">Users</el-menu-item>
        <el-menu-item index="/admin/audit-logs">Audit Logs</el-menu-item>
      </el-menu>
    </aside>
    <section class="admin-main">
      <header class="admin-topbar">
        <div class="topbar-title">{{ pageTitle }}</div>
        <div class="topbar-actions">
          <el-button size="small" @click="goHome">View Site</el-button>
          <el-dropdown>
            <span class="user-chip">{{ auth.user?.username }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goProfile">Profile</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">Logout</el-dropdown-item>
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

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const activePath = computed(() => route.path)
const pageTitle = computed(() => (route.meta?.title as string) || 'Admin')

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
  background: #f6f4ef;
}

.admin-sidebar {
  background: #151310;
  color: #f5f1ea;
  padding: 20px 0;
}

.sidebar-brand {
  padding: 0 20px 18px;
  font-weight: 700;
  letter-spacing: 0.4px;
}

.menu {
  background: transparent;
  border: none;
}

.menu :deep(.el-menu-item) {
  color: #e2ddd3;
}

.menu :deep(.el-menu-item.is-active) {
  background: rgba(214, 106, 45, 0.2);
  color: #fff;
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
  border-bottom: 1px solid rgba(30, 27, 22, 0.1);
  background: rgba(255, 255, 255, 0.8);
}

.topbar-title {
  font-size: 20px;
  font-weight: 600;
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
  border: 1px solid var(--border);
  cursor: pointer;
}

.admin-content {
  padding: 24px 28px 40px;
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
