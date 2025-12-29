import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/PublicLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('@/views/Home.vue'), meta: { title: 'Home' } },
      { path: 'articles', name: 'article-list', component: () => import('@/views/ArticleList.vue'), meta: { title: 'Articles' } },
      { path: 'articles/:id', name: 'article-detail', component: () => import('@/views/ArticleDetail.vue'), meta: { title: 'Article' } },
      { path: 'search', name: 'search', component: () => import('@/views/Search.vue'), meta: { title: 'Search' } },
      { path: 'albums', name: 'albums', component: () => import('@/views/Albums.vue'), meta: { title: 'Albums' } },
      { path: 'albums/:id', name: 'album-detail', component: () => import('@/views/AlbumDetail.vue'), meta: { title: 'Album' } },
      { path: 'pages/:slug', name: 'page', component: () => import('@/views/PageView.vue'), meta: { title: 'Page' } },
      { path: 'login', name: 'login', component: () => import('@/views/Login.vue'), meta: { title: 'Login' } },
      { path: 'register', name: 'register', component: () => import('@/views/Register.vue'), meta: { title: 'Register' } },
      { path: 'profile', name: 'profile', component: () => import('@/views/Profile.vue'), meta: { requiresAuth: true, title: 'Profile' } },
      { path: 'me/articles', name: 'my-articles', component: () => import('@/views/MyArticles.vue'), meta: { requiresAuth: true, title: 'My Articles' } },
      { path: 'me/articles/new', name: 'my-article-new', component: () => import('@/views/MyArticleEditor.vue'), meta: { requiresAuth: true, title: 'New Article' } },
      { path: 'me/articles/:id', name: 'my-article-edit', component: () => import('@/views/MyArticleEditor.vue'), meta: { requiresAuth: true, title: 'Edit Article' } },
      { path: 'me/albums', name: 'my-albums', component: () => import('@/views/MyAlbums.vue'), meta: { requiresAuth: true, title: 'My Albums' } },
      { path: 'forbidden', name: 'forbidden', component: () => import('@/views/Forbidden.vue'), meta: { title: 'Forbidden' } },
    ],
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: () => import('@/views/admin/Dashboard.vue'), meta: { title: 'Dashboard' } },
      { path: 'articles', component: () => import('@/views/admin/Articles.vue'), meta: { title: 'Articles' } },
      { path: 'articles/new', component: () => import('@/views/admin/ArticleEditor.vue'), meta: { title: 'New Article' } },
      { path: 'articles/:id', component: () => import('@/views/admin/ArticleEditor.vue'), meta: { title: 'Edit Article' } },
      { path: 'comments', component: () => import('@/views/admin/Comments.vue'), meta: { title: 'Comments' } },
      { path: 'pages', component: () => import('@/views/admin/Pages.vue'), meta: { title: 'Pages' } },
      { path: 'pages/new', component: () => import('@/views/admin/PageEditor.vue'), meta: { title: 'New Page' } },
      { path: 'pages/:id', component: () => import('@/views/admin/PageEditor.vue'), meta: { title: 'Edit Page' } },
      { path: 'navigation', component: () => import('@/views/admin/Navigation.vue'), meta: { title: 'Navigation' } },
      { path: 'albums', component: () => import('@/views/admin/Albums.vue'), meta: { title: 'Albums' } },
      { path: 'albums/:id', component: () => import('@/views/admin/AlbumDetail.vue'), meta: { title: 'Album' } },
      { path: 'themes', component: () => import('@/views/admin/Themes.vue'), meta: { title: 'Themes' } },
      { path: 'settings', component: () => import('@/views/admin/Settings.vue'), meta: { title: 'Settings' } },
      { path: 'users', component: () => import('@/views/admin/Users.vue'), meta: { title: 'Users' } },
      { path: 'audit-logs', component: () => import('@/views/admin/AuditLogs.vue'), meta: { title: 'Audit Logs' } },
    ],
  },
  { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/NotFound.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  await auth.init()
  if (to.meta?.requiresAuth && !auth.isAuthenticated) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  const roles = Array.isArray(to.meta?.roles) ? (to.meta.roles as string[]) : []
  if (roles.length > 0 && auth.user && !roles.includes(auth.user.role)) {
    return { path: '/forbidden' }
  }
  if ((to.path === '/login' || to.path === '/register') && auth.isAuthenticated) {
    return { path: '/' }
  }
  return true
})

export default router
