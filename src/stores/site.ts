import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { v4 as uuid } from 'uuid';
import dayjs from 'dayjs';
import type {
  Article,
  Comment,
  PageEntry,
  Album,
  Photo,
  Theme,
  SiteSettings,
  Visibility,
  User,
  AuditLog,
} from '../types';
import { storage } from '../services/storage';
import { renderMarkdown, sanitizeHtml, truncateText } from '../services/text';
import { ensureSeedData } from '../services/seed';

export const useSiteStore = defineStore('site', () => {
  ensureSeedData();

  const articles = ref<Article[]>(storage.getArticles());
  const comments = ref<Comment[]>(storage.getComments());
  const pages = ref<PageEntry[]>(storage.getPages());
  const albums = ref<Album[]>(storage.getAlbums());
  const photos = ref<Photo[]>(storage.getPhotos());
  const themes = ref<Theme[]>(storage.getThemes());
  const settings = ref<SiteSettings>(storage.getSettings()!);
  const auditLogs = ref<AuditLog[]>(storage.getAuditLogs());

  const persist = () => {
    storage.setArticles(articles.value);
    storage.setComments(comments.value);
    storage.setPages(pages.value);
    storage.setAlbums(albums.value);
    storage.setPhotos(photos.value);
    storage.setThemes(themes.value);
    storage.setSettings(settings.value);
    storage.setAuditLogs(auditLogs.value);
  };

  const log = (actor: User | null, action: string, target: string, result: 'SUCCESS' | 'FAILED', detail?: string) => {
    const entry: AuditLog = {
      id: uuid(),
      actorId: actor?.id ?? 'system',
      actorName: actor?.username ?? 'system',
      action,
      target,
      result,
      detail,
      createdAt: new Date().toISOString(),
    };
    auditLogs.value = [entry, ...auditLogs.value].slice(0, 200);
    storage.setAuditLogs(auditLogs.value);
  };

  const canView = (visibility: Visibility, user: User | null, whitelist: string[]) => {
    if (visibility === 'PUBLIC') return true;
    if (!user) return false;
    if (user.role === 'ADMIN' && settings.value.adminCanViewPrivate) return true;
    if (visibility === 'LOGIN_ONLY') return true;
    if (visibility === 'PRIVATE') return whitelist.includes(user.id);
    if (visibility === 'WHITELIST') return whitelist.includes(user.id);
    if (visibility === 'ADMIN_ONLY') return user.role === 'ADMIN';
    return false;
  };

  const visibleArticles = (user: User | null) =>
    articles.value.filter((item) =>
      item.status === 'PUBLISHED' && canView(item.visibility, user, item.whitelist)
    );

  const visiblePages = (user: User | null) =>
    pages.value.filter((item) => canView(item.visibility, user, item.whitelist));

  const visibleAlbums = (user: User | null) =>
    albums.value.filter((item) => canView(item.visibility, user, [item.ownerId]));

  const addArticle = (payload: Partial<Article>, author: User) => {
    const now = new Date().toISOString();
    const contentHtml = payload.contentType === 'MARKDOWN'
      ? renderMarkdown(payload.contentRaw ?? '')
      : sanitizeHtml(payload.contentRaw ?? '');
    const article: Article = {
      id: uuid(),
      title: payload.title ?? '',
      summary: payload.summary ?? truncateText(contentHtml),
      contentType: payload.contentType ?? 'MARKDOWN',
      contentRaw: payload.contentRaw ?? '',
      contentHtml,
      authorId: author.id,
      authorName: author.username,
      status: payload.status ?? 'DRAFT',
      visibility: payload.visibility ?? 'PUBLIC',
      tags: payload.tags ?? [],
      category: payload.category,
      allowRss: payload.allowRss ?? false,
      createdAt: now,
      updatedAt: now,
      publishedAt: payload.status === 'PUBLISHED' ? now : undefined,
      whitelist: payload.whitelist ?? [author.id],
      views: 0,
    };
    articles.value = [article, ...articles.value];
    persist();
    return article;
  };

  const updateArticle = (articleId: string, payload: Partial<Article>) => {
    articles.value = articles.value.map((item) => {
      if (item.id !== articleId) return item;
      const contentRaw = payload.contentRaw ?? item.contentRaw;
      const contentType = payload.contentType ?? item.contentType;
      const contentHtml = contentType === 'MARKDOWN' ? renderMarkdown(contentRaw) : sanitizeHtml(contentRaw);
      const updated: Article = {
        ...item,
        ...payload,
        contentRaw,
        contentHtml,
        summary: payload.summary ?? truncateText(contentHtml),
        updatedAt: new Date().toISOString(),
        publishedAt:
          payload.status === 'PUBLISHED' && !item.publishedAt
            ? new Date().toISOString()
            : item.publishedAt,
      };
      return updated;
    });
    persist();
  };

  const removeArticle = (articleId: string) => {
    articles.value = articles.value.filter((item) => item.id !== articleId);
    comments.value = comments.value.filter((item) => item.articleId !== articleId);
    persist();
  };

  const addComment = (payload: Partial<Comment>) => {
    const comment: Comment = {
      id: uuid(),
      articleId: payload.articleId ?? '',
      authorId: payload.authorId ?? '',
      authorName: payload.authorName ?? '',
      content: payload.content ?? '',
      status: payload.status ?? 'PUBLISHED',
      parentId: payload.parentId,
      createdAt: new Date().toISOString(),
    };
    comments.value = [comment, ...comments.value];
    persist();
  };

  const updateCommentStatus = (commentId: string, status: Comment['status']) => {
    comments.value = comments.value.map((item) => (item.id === commentId ? { ...item, status } : item));
    persist();
  };

  const removeComment = (commentId: string) => {
    comments.value = comments.value.filter((item) => item.id !== commentId && item.parentId !== commentId);
    persist();
  };

  const addPage = (payload: Partial<PageEntry>) => {
    const now = dayjs().toISOString();
    const contentHtml = payload.type === 'MARKDOWN'
      ? renderMarkdown(payload.contentRaw ?? '')
      : payload.type === 'RICH_TEXT'
        ? sanitizeHtml(payload.contentRaw ?? '')
        : '';
    const page: PageEntry = {
      id: uuid(),
      title: payload.title ?? '',
      slug: payload.slug ?? '',
      type: payload.type ?? 'MARKDOWN',
      contentRaw: payload.contentRaw ?? '',
      contentHtml,
      linkUrl: payload.linkUrl,
      visibility: payload.visibility ?? 'PUBLIC',
      showInNav: payload.showInNav ?? false,
      order: payload.order ?? 0,
      whitelist: payload.whitelist ?? [],
      createdAt: now,
      updatedAt: now,
    };
    pages.value = [...pages.value, page].sort((a, b) => a.order - b.order);
    persist();
  };

  const updatePage = (pageId: string, payload: Partial<PageEntry>) => {
    pages.value = pages.value.map((item) => {
      if (item.id !== pageId) return item;
      const contentRaw = payload.contentRaw ?? item.contentRaw;
      const contentHtml = payload.type === 'MARKDOWN'
        ? renderMarkdown(contentRaw)
        : payload.type === 'RICH_TEXT'
          ? sanitizeHtml(contentRaw)
          : item.contentHtml;
      return {
        ...item,
        ...payload,
        contentRaw,
        contentHtml,
        updatedAt: dayjs().toISOString(),
      };
    });
    persist();
  };

  const removePage = (pageId: string) => {
    pages.value = pages.value.filter((item) => item.id !== pageId);
    persist();
  };

  const addAlbum = (payload: Partial<Album>, owner: User) => {
    const now = new Date().toISOString();
    const album: Album = {
      id: uuid(),
      title: payload.title ?? '',
      description: payload.description,
      coverUrl: payload.coverUrl,
      visibility: payload.visibility ?? 'PUBLIC',
      ownerId: owner.id,
      createdAt: now,
      updatedAt: now,
    };
    albums.value = [album, ...albums.value];
    persist();
  };

  const updateAlbum = (albumId: string, payload: Partial<Album>) => {
    albums.value = albums.value.map((item) =>
      item.id === albumId ? { ...item, ...payload, updatedAt: new Date().toISOString() } : item
    );
    persist();
  };

  const removeAlbum = (albumId: string) => {
    albums.value = albums.value.filter((item) => item.id !== albumId);
    photos.value = photos.value.filter((item) => item.albumId !== albumId);
    persist();
  };

  const addPhoto = (payload: Partial<Photo>) => {
    const photo: Photo = {
      id: uuid(),
      albumId: payload.albumId ?? '',
      title: payload.title,
      url: payload.url ?? '',
      thumbUrl: payload.thumbUrl ?? payload.url ?? '',
      status: payload.status ?? 'LOCAL',
      exif: payload.exif,
      createdAt: new Date().toISOString(),
    };
    photos.value = [photo, ...photos.value];
    persist();
  };

  const updatePhoto = (photoId: string, payload: Partial<Photo>) => {
    photos.value = photos.value.map((item) => (item.id === photoId ? { ...item, ...payload } : item));
    persist();
  };

  const removePhoto = (photoId: string) => {
    photos.value = photos.value.filter((item) => item.id !== photoId);
    persist();
  };

  const addTheme = (payload: Partial<Theme>) => {
    const theme: Theme = {
      id: uuid(),
      name: payload.name ?? '新主题',
      version: payload.version ?? '1.0.0',
      author: payload.author ?? '未知',
      source: payload.source ?? 'IMPORTED',
      description: payload.description ?? '',
      preview: payload.preview,
      config: payload.config ?? {
        primaryColor: '#2563eb',
        fontFamily: '"Segoe UI", "PingFang SC", sans-serif',
        radius: '12px',
      },
      createdAt: new Date().toISOString(),
    };
    themes.value = [...themes.value, theme];
    persist();
  };

  const updateTheme = (themeId: string, payload: Partial<Theme>) => {
    themes.value = themes.value.map((item) => (item.id === themeId ? { ...item, ...payload } : item));
    persist();
  };

  const removeTheme = (themeId: string) => {
    themes.value = themes.value.filter((item) => item.id !== themeId);
    persist();
  };

  const updateSettings = (payload: Partial<SiteSettings>) => {
    settings.value = { ...settings.value, ...payload };
    storage.setSettings(settings.value);
  };

  const updateNavigationGroups = (groups: SiteSettings['navigationGroups']) => {
    settings.value = { ...settings.value, navigationGroups: groups };
    storage.setSettings(settings.value);
  };

  const updateEngines = (engines: SiteSettings['engines']) => {
    settings.value = { ...settings.value, engines };
    storage.setSettings(settings.value);
  };

  return {
    articles,
    comments,
    pages,
    albums,
    photos,
    themes,
    settings,
    auditLogs,
    visibleArticles,
    visiblePages,
    visibleAlbums,
    addArticle,
    updateArticle,
    removeArticle,
    addComment,
    updateCommentStatus,
    removeComment,
    addPage,
    updatePage,
    removePage,
    addAlbum,
    updateAlbum,
    removeAlbum,
    addPhoto,
    updatePhoto,
    removePhoto,
    addTheme,
    updateTheme,
    removeTheme,
    updateSettings,
    updateNavigationGroups,
    updateEngines,
    log,
  };
});
