<template>
  <div>
    <el-affix>
      <el-header height="64px" class="header">
        <div class="header-left">
          <router-link to="/" class="header-logo">{{ settings.siteName }}</router-link>
          <nav class="nav-links">
            <router-link to="/">首页</router-link>
            <router-link to="/articles">文章</router-link>
            <router-link to="/albums">相册</router-link>
            <router-link v-for="page in navPages" :key="page.id" :to="`/pages/${page.slug}`">
              {{ page.title }}
            </router-link>
          </nav>
        </div>
        <div class="header-right">
          <el-space>
            <el-button v-if="!auth.isAuthed" type="primary" link @click="goLogin">登录</el-button>
            <el-button v-if="!auth.isAuthed" type="primary" link @click="goRegister">注册</el-button>
            <el-dropdown v-else>
              <span class="el-dropdown-link">
                <el-avatar :src="auth.currentUser?.avatar" size="small" />
                <span>{{ auth.currentUser?.username }}</span>
                <el-icon><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goProfile">个人资料</el-dropdown-item>
                  <el-dropdown-item v-if="settings.allowUserPost" @click="goMyArticles">我的文章</el-dropdown-item>
                  <el-dropdown-item @click="goMyComments">我的评论</el-dropdown-item>
                  <el-dropdown-item v-if="auth.isAdmin" @click="goAdmin">后台管理</el-dropdown-item>
                  <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-space>
        </div>
      </el-header>
    </el-affix>
    <router-view />
    <el-footer height="80px" class="footer">
      <div>© 2025 {{ settings.siteName }} · {{ settings.description }}</div>
    </el-footer>
  </div>
</template>

<script setup lang="ts">
import { computed, watchEffect } from 'vue';
import { useRouter } from 'vue-router';
import { ArrowDown } from '@element-plus/icons-vue';
import { useAuthStore } from '../../stores/auth';
import { useSiteStore } from '../../stores/site';

const router = useRouter();
const auth = useAuthStore();
const site = useSiteStore();

const settings = computed(() => site.settings);
const navPages = computed(() =>
  site.pages
    .filter((page) => page.showInNav)
    .sort((a, b) => a.order - b.order)
);

const currentTheme = computed(() =>
  site.themes.find((theme) => theme.id === settings.value.currentThemeId) ?? site.themes[0]
);

watchEffect(() => {
  if (!currentTheme.value) return;
  const root = document.documentElement;
  root.style.setProperty('--el-color-primary', currentTheme.value.config.primaryColor);
  root.style.setProperty('--app-font-family', currentTheme.value.config.fontFamily);
  root.style.setProperty('--app-radius', currentTheme.value.config.radius);
  document.body.style.fontFamily = currentTheme.value.config.fontFamily;
});

watchEffect(() => {
  const existingStyle = document.getElementById(customCssId) as HTMLStyleElement | null;
  if (settings.value.customCss) {
    const style = existingStyle ?? document.createElement('style');
    style.id = customCssId;
    style.textContent = settings.value.customCss;
    if (!existingStyle) document.head.appendChild(style);
  } else if (existingStyle) {
    existingStyle.remove();
  }
});

watchEffect(() => {
  const existingScript = document.getElementById(customJsId) as HTMLScriptElement | null;
  if (settings.value.enableCustomJs && settings.value.customJs) {
    const script = existingScript ?? document.createElement('script');
    script.id = customJsId;
    script.type = 'text/javascript';
    script.text = settings.value.customJs;
    if (!existingScript) document.body.appendChild(script);
  } else if (existingScript) {
    existingScript.remove();
  }
});

const customCssId = 'custom-css';
const customJsId = 'custom-js';

const goLogin = () => router.push('/login');
const goRegister = () => router.push('/register');
const goProfile = () => router.push('/profile');
const goMyArticles = () => router.push('/admin/articles');
const goMyComments = () => router.push('/admin/comments');
const goAdmin = () => router.push('/admin');
const logout = () => auth.logout();
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.footer {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  font-size: 14px;
}
</style>
