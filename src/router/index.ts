import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import FrontLayout from '../views/layouts/FrontLayout.vue';
import AdminLayout from '../views/layouts/AdminLayout.vue';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: FrontLayout,
    children: [
      { path: '', name: 'home', component: () => import('../views/front/HomeView.vue') },
      { path: 'articles', name: 'articles', component: () => import('../views/front/ArticleListView.vue') },
      { path: 'articles/:id', name: 'article-detail', component: () => import('../views/front/ArticleDetailView.vue') },
      { path: 'albums', name: 'albums', component: () => import('../views/front/AlbumListView.vue') },
      { path: 'albums/:id', name: 'album-detail', component: () => import('../views/front/AlbumDetailView.vue') },
      { path: 'pages/:slug', name: 'page-view', component: () => import('../views/front/PageView.vue') },
      { path: 'rss', name: 'rss', component: () => import('../views/front/RssView.vue') },
      { path: 'profile', name: 'profile', component: () => import('../views/front/ProfileView.vue') },
      { path: 'login', name: 'login', component: () => import('../views/front/LoginView.vue') },
      { path: 'register', name: 'register', component: () => import('../views/front/RegisterView.vue') },
    ],
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    children: [
      { path: '', name: 'admin-dashboard', component: () => import('../views/admin/DashboardView.vue') },
      { path: 'articles', name: 'admin-articles', component: () => import('../views/admin/ArticleManageView.vue') },
      { path: 'articles/new', name: 'admin-article-new', component: () => import('../views/admin/ArticleEditorView.vue') },
      { path: 'articles/:id', name: 'admin-article-edit', component: () => import('../views/admin/ArticleEditorView.vue') },
      { path: 'comments', name: 'admin-comments', component: () => import('../views/admin/CommentManageView.vue') },
      { path: 'pages', name: 'admin-pages', component: () => import('../views/admin/PageManageView.vue') },
      { path: 'albums', name: 'admin-albums', component: () => import('../views/admin/AlbumManageView.vue') },
      { path: 'themes', name: 'admin-themes', component: () => import('../views/admin/ThemeManageView.vue') },
      { path: 'settings', name: 'admin-settings', component: () => import('../views/admin/SiteSettingsView.vue') },
      { path: 'users', name: 'admin-users', component: () => import('../views/admin/UserManageView.vue') },
      { path: 'logs', name: 'admin-logs', component: () => import('../views/admin/AuditLogView.vue') },
    ],
  },
  { path: '/403', name: 'forbidden', component: () => import('../views/shared/ForbiddenView.vue') },
  { path: '/401', name: 'unauthorized', component: () => import('../views/shared/UnauthorizedView.vue') },
  { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('../views/shared/NotFoundView.vue') },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 };
  },
});

router.beforeEach((to) => {
  const auth = useAuthStore();
  const requiresAuth = to.meta.requiresAuth as boolean | undefined;
  const roles = to.meta.roles as string[] | undefined;

  if (requiresAuth && !auth.isAuthed) {
    return { name: 'unauthorized', query: { redirect: to.fullPath } };
  }

  if (roles && !roles.includes(auth.role)) {
    return { name: 'forbidden' };
  }

  return true;
});
