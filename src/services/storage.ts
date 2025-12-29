import type { Article, Comment, Album, Photo, PageEntry, Theme, User, SiteSettings, AuditLog } from '../types';

const STORAGE_KEYS = {
  users: 'svb_users',
  articles: 'svb_articles',
  comments: 'svb_comments',
  albums: 'svb_albums',
  photos: 'svb_photos',
  pages: 'svb_pages',
  themes: 'svb_themes',
  settings: 'svb_settings',
  logs: 'svb_audit_logs',
  auth: 'svb_auth',
};

const safeParse = <T>(value: string | null, fallback: T): T => {
  if (!value) return fallback;
  try {
    return JSON.parse(value) as T;
  } catch {
    return fallback;
  }
};

export const storage = {
  getUsers(): User[] {
    return safeParse(localStorage.getItem(STORAGE_KEYS.users), [] as User[]);
  },
  setUsers(users: User[]) {
    localStorage.setItem(STORAGE_KEYS.users, JSON.stringify(users));
  },
  getArticles(): Article[] {
    return safeParse(localStorage.getItem(STORAGE_KEYS.articles), [] as Article[]);
  },
  setArticles(articles: Article[]) {
    localStorage.setItem(STORAGE_KEYS.articles, JSON.stringify(articles));
  },
  getComments(): Comment[] {
    return safeParse(localStorage.getItem(STORAGE_KEYS.comments), [] as Comment[]);
  },
  setComments(comments: Comment[]) {
    localStorage.setItem(STORAGE_KEYS.comments, JSON.stringify(comments));
  },
  getAlbums(): Album[] {
    return safeParse(localStorage.getItem(STORAGE_KEYS.albums), [] as Album[]);
  },
  setAlbums(albums: Album[]) {
    localStorage.setItem(STORAGE_KEYS.albums, JSON.stringify(albums));
  },
  getPhotos(): Photo[] {
    return safeParse(localStorage.getItem(STORAGE_KEYS.photos), [] as Photo[]);
  },
  setPhotos(photos: Photo[]) {
    localStorage.setItem(STORAGE_KEYS.photos, JSON.stringify(photos));
  },
  getPages(): PageEntry[] {
    return safeParse(localStorage.getItem(STORAGE_KEYS.pages), [] as PageEntry[]);
  },
  setPages(pages: PageEntry[]) {
    localStorage.setItem(STORAGE_KEYS.pages, JSON.stringify(pages));
  },
  getThemes(): Theme[] {
    return safeParse(localStorage.getItem(STORAGE_KEYS.themes), [] as Theme[]);
  },
  setThemes(themes: Theme[]) {
    localStorage.setItem(STORAGE_KEYS.themes, JSON.stringify(themes));
  },
  getSettings(): SiteSettings | null {
    return safeParse(localStorage.getItem(STORAGE_KEYS.settings), null as SiteSettings | null);
  },
  setSettings(settings: SiteSettings) {
    localStorage.setItem(STORAGE_KEYS.settings, JSON.stringify(settings));
  },
  getAuditLogs(): AuditLog[] {
    return safeParse(localStorage.getItem(STORAGE_KEYS.logs), [] as AuditLog[]);
  },
  setAuditLogs(logs: AuditLog[]) {
    localStorage.setItem(STORAGE_KEYS.logs, JSON.stringify(logs));
  },
  getAuth(): { userId: string | null } {
    return safeParse(localStorage.getItem(STORAGE_KEYS.auth), { userId: null });
  },
  setAuth(auth: { userId: string | null }) {
    localStorage.setItem(STORAGE_KEYS.auth, JSON.stringify(auth));
  },
  clear() {
    Object.values(STORAGE_KEYS).forEach((key) => localStorage.removeItem(key));
  },
};
