<template>
  <el-container class="admin-layout">
    <el-aside width="240px" class="sidebar">
      <div class="sidebar-logo" @click="goAdmin">后台管理</div>
      <el-menu :default-active="activePath" router>
        <el-menu-item index="/admin">
          <el-icon><home-filled /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/articles">
          <el-icon><document /></el-icon>
          <span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/comments">
          <el-icon><chat-dot-round /></el-icon>
          <span>评论管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/pages">
          <el-icon><tickets /></el-icon>
          <span>页面与导航</span>
        </el-menu-item>
        <el-menu-item index="/admin/albums">
          <el-icon><picture /></el-icon>
          <span>相册管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/themes">
          <el-icon><brush /></el-icon>
          <span>主题管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/settings">
          <el-icon><setting /></el-icon>
          <span>站点设置</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><user-filled /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/logs">
          <el-icon><warning /></el-icon>
          <span>审计日志</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header height="64px" class="admin-header">
        <div>当前管理员：{{ auth.currentUser?.username }}</div>
        <el-space>
          <el-button type="primary" link @click="goHome">前台首页</el-button>
          <el-button type="danger" link @click="logout">退出登录</el-button>
        </el-space>
      </el-header>
      <el-main class="admin-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  HomeFilled,
  Document,
  ChatDotRound,
  Tickets,
  Picture,
  Brush,
  Setting,
  UserFilled,
  Warning,
} from '@element-plus/icons-vue';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const route = useRoute();
const auth = useAuthStore();

const activePath = computed(() => route.path);

const goHome = () => router.push('/');
const goAdmin = () => router.push('/admin');
const logout = () => {
  auth.logout();
  router.push('/');
};
</script>

<style scoped>
.sidebar {
  background: #0f172a;
  color: #e2e8f0;
}

.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  cursor: pointer;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
}

.el-menu {
  background: transparent;
  border: none;
}

.el-menu-item {
  color: #e2e8f0;
}

.el-menu-item.is-active {
  background: rgba(148, 163, 184, 0.2);
}

.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  padding: 0 24px;
}
</style>
