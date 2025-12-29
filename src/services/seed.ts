import { v4 as uuid } from 'uuid';
import dayjs from 'dayjs';
import type { Article, Comment, PageEntry, SiteSettings, Theme, User } from '../types';
import { storage } from './storage';
import { renderMarkdown, sanitizeHtml } from './text';

export const ensureSeedData = () => {
  const users = storage.getUsers();
  if (users.length === 0) {
    const admin: User = {
      id: uuid(),
      username: 'admin',
      email: 'admin@example.com',
      password: 'admin123',
      role: 'ADMIN',
      status: 'ACTIVE',
      createdAt: dayjs().subtract(3, 'day').toISOString(),
      avatar: 'https://avatars.githubusercontent.com/u/9919?s=200&v=4',
    };
    const user: User = {
      id: uuid(),
      username: 'demo',
      email: 'demo@example.com',
      password: 'demo123',
      role: 'USER',
      status: 'ACTIVE',
      createdAt: dayjs().subtract(2, 'day').toISOString(),
      avatar: 'https://i.pravatar.cc/120?img=68',
    };
    storage.setUsers([admin, user]);
  }

  const articles = storage.getArticles();
  if (articles.length === 0) {
    const author = storage.getUsers().find((item) => item.role === 'ADMIN');
    const markdown = '# 欢迎来到 SpingWithViteBlog\n\n这是 **Markdown** 示例文章，支持表格、代码块与图片。\n\n```ts\nconsole.log(\'Hello V1.0\');\n```\n';
    const richText = '<h2>富文本示例</h2><p>这里展示 <strong>富文本</strong> 渲染效果。</p>';

    const seeded: Article[] = [
      {
        id: uuid(),
        title: 'V1.0 产品预览：博客系统上线',
        summary: '覆盖注册登录、文章、评论、相册、主题与导航管理的完整体验。',
        contentType: 'MARKDOWN',
        contentRaw: markdown,
        contentHtml: renderMarkdown(markdown),
        authorId: author?.id ?? 'system',
        authorName: author?.username ?? 'system',
        status: 'PUBLISHED',
        visibility: 'PUBLIC',
        tags: ['V1.0', '发布'],
        category: '产品更新',
        allowRss: true,
        createdAt: dayjs().subtract(2, 'day').toISOString(),
        updatedAt: dayjs().subtract(1, 'day').toISOString(),
        publishedAt: dayjs().subtract(1, 'day').toISOString(),
        whitelist: [],
        views: 128,
      },
      {
        id: uuid(),
        title: '仅登录可见的团队周报',
        summary: '登录用户可查看的周报内容示例。',
        contentType: 'RICH_TEXT',
        contentRaw: richText,
        contentHtml: sanitizeHtml(richText),
        authorId: author?.id ?? 'system',
        authorName: author?.username ?? 'system',
        status: 'PUBLISHED',
        visibility: 'LOGIN_ONLY',
        tags: ['周报'],
        category: '内部信息',
        allowRss: false,
        createdAt: dayjs().subtract(4, 'day').toISOString(),
        updatedAt: dayjs().subtract(3, 'day').toISOString(),
        publishedAt: dayjs().subtract(3, 'day').toISOString(),
        whitelist: [],
        views: 48,
      },
    ];
    storage.setArticles(seeded);
  }

  const comments = storage.getComments();
  if (comments.length === 0) {
    const article = storage.getArticles()[0];
    const user = storage.getUsers().find((item) => item.role === 'USER');
    if (article && user) {
      const seeded: Comment[] = [
        {
          id: uuid(),
          articleId: article.id,
          authorId: user.id,
          authorName: user.username,
          content: '很棒的 V1.0，上线流程清晰，赞！',
          status: 'PUBLISHED',
          createdAt: dayjs().subtract(1, 'day').toISOString(),
        },
      ];
      storage.setComments(seeded);
    }
  }

  const pages = storage.getPages();
  if (pages.length === 0) {
    const aboutMd = '# 关于我们\n\n这是一个面向多用户的博客系统原型。';
    const entry: PageEntry = {
      id: uuid(),
      title: '关于',
      slug: 'about',
      type: 'MARKDOWN',
      contentRaw: aboutMd,
      contentHtml: renderMarkdown(aboutMd),
      visibility: 'PUBLIC',
      showInNav: true,
      order: 1,
      whitelist: [],
      createdAt: dayjs().toISOString(),
      updatedAt: dayjs().toISOString(),
    };
    storage.setPages([entry]);
  }

  const themes = storage.getThemes();
  if (themes.length === 0) {
    const defaultTheme: Theme = {
      id: uuid(),
      name: '霁蓝主题',
      version: '1.0.0',
      author: 'Design Team',
      source: 'BUILT_IN',
      description: '清爽的蓝色调主题，适合阅读型博客。',
      preview: 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80',
      config: {
        primaryColor: '#2563eb',
        fontFamily: '"Segoe UI", "PingFang SC", sans-serif',
        radius: '12px',
      },
      createdAt: dayjs().toISOString(),
    };
    storage.setThemes([defaultTheme]);
  }

  const settings = storage.getSettings();
  if (!settings) {
    const defaultSettings: SiteSettings = {
      siteName: 'SpingWithViteBlog',
      description: '面向多用户的 V1.0 博客系统原型。',
      homeMode: 'BLOG',
      currentThemeId: undefined,
      allowGuestComment: false,
      commentReviewMode: 'FIRST',
      customCss: '',
      customJs: '',
      enableCustomJs: false,
      rssEnabled: true,
      rssFullContent: false,
      rssPrivateEnabled: true,
      rssTokenExpireDays: 30,
      allowUserPost: true,
      allowUserAlbum: false,
      adminCanViewPrivate: true,
      engines: [
        {
          id: uuid(),
          name: 'Google',
          queryTemplate: 'https://www.google.com/search?q={q}',
          enabled: true,
          isDefault: true,
        },
        {
          id: uuid(),
          name: 'Bing',
          queryTemplate: 'https://www.bing.com/search?q={q}',
          enabled: true,
          isDefault: false,
        },
        {
          id: uuid(),
          name: '站内搜索',
          queryTemplate: 'INTERNAL',
          enabled: true,
          isDefault: false,
        },
      ],
      navigationGroups: [
        {
          id: uuid(),
          name: '常用入口',
          order: 1,
          items: [
            {
              id: uuid(),
              name: 'Element Plus',
              url: 'https://element-plus.org',
              description: '组件库',
              openInNew: true,
            },
            {
              id: uuid(),
              name: 'Vite',
              url: 'https://vitejs.dev',
              description: '前端构建工具',
              openInNew: true,
            },
          ],
        },
      ],
    };
    storage.setSettings(defaultSettings);
  }
};
