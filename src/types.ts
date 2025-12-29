export type Role = 'GUEST' | 'USER' | 'ADMIN';
export type Visibility = 'PUBLIC' | 'LOGIN_ONLY' | 'WHITELIST' | 'PRIVATE' | 'ADMIN_ONLY';
export type ContentType = 'MARKDOWN' | 'RICH_TEXT';
export type ArticleStatus = 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
export type CommentStatus = 'PENDING' | 'PUBLISHED' | 'BLOCKED';
export type PageType = 'MARKDOWN' | 'RICH_TEXT' | 'LINK';
export type HomeMode = 'BLOG' | 'PORTAL' | 'THEME';
export type ThemeSource = 'BUILT_IN' | 'IMPORTED';

export interface User {
  id: string;
  username: string;
  email: string;
  password: string;
  role: Role;
  status: 'ACTIVE' | 'DISABLED';
  avatar?: string;
  createdAt: string;
}

export interface Article {
  id: string;
  title: string;
  summary: string;
  contentType: ContentType;
  contentRaw: string;
  contentHtml: string;
  authorId: string;
  authorName: string;
  status: ArticleStatus;
  visibility: Visibility;
  tags: string[];
  category?: string;
  allowRss: boolean;
  createdAt: string;
  updatedAt: string;
  publishedAt?: string;
  whitelist: string[];
  views: number;
}

export interface Comment {
  id: string;
  articleId: string;
  authorId: string;
  authorName: string;
  content: string;
  status: CommentStatus;
  parentId?: string;
  createdAt: string;
}

export interface Album {
  id: string;
  title: string;
  description?: string;
  coverUrl?: string;
  visibility: Visibility;
  ownerId: string;
  createdAt: string;
  updatedAt: string;
}

export interface Photo {
  id: string;
  albumId: string;
  title?: string;
  url: string;
  thumbUrl: string;
  status: 'LOCAL' | 'SYNCED' | 'FAILED';
  exif?: string;
  createdAt: string;
}

export interface PageEntry {
  id: string;
  title: string;
  slug: string;
  type: PageType;
  contentRaw: string;
  contentHtml: string;
  linkUrl?: string;
  visibility: Visibility;
  showInNav: boolean;
  order: number;
  whitelist: string[];
  createdAt: string;
  updatedAt: string;
}

export interface ThemeConfig {
  primaryColor: string;
  fontFamily: string;
  radius: string;
}

export interface Theme {
  id: string;
  name: string;
  version: string;
  author: string;
  source: ThemeSource;
  description: string;
  preview?: string;
  config: ThemeConfig;
  createdAt: string;
}

export interface NavigationGroup {
  id: string;
  name: string;
  order: number;
  items: NavigationItem[];
}

export interface NavigationItem {
  id: string;
  name: string;
  url: string;
  description?: string;
  icon?: string;
  openInNew?: boolean;
}

export interface SearchEngine {
  id: string;
  name: string;
  queryTemplate: string;
  enabled: boolean;
  isDefault: boolean;
}

export interface SiteSettings {
  siteName: string;
  description: string;
  logoUrl?: string;
  homeMode: HomeMode;
  currentThemeId?: string;
  allowGuestComment: boolean;
  commentReviewMode: 'NONE' | 'FIRST' | 'ALL';
  customCss: string;
  customJs: string;
  enableCustomJs: boolean;
  rssEnabled: boolean;
  rssFullContent: boolean;
  rssPrivateEnabled: boolean;
  rssTokenExpireDays: number;
  allowUserPost: boolean;
  allowUserAlbum: boolean;
  adminCanViewPrivate: boolean;
  engines: SearchEngine[];
  navigationGroups: NavigationGroup[];
}

export interface AuditLog {
  id: string;
  actorId: string;
  actorName: string;
  action: string;
  target: string;
  result: 'SUCCESS' | 'FAILED';
  createdAt: string;
  detail?: string;
}
