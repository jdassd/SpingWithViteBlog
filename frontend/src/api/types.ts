export type UserSummary = {
  id: number
  username: string
  role: 'ADMIN' | 'USER'
}

export type AuthResponse = {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresInSeconds: number
  user: UserSummary
}

export type ArticleSummary = {
  id: number
  title: string
  summary: string
  authorId: number
  contentType: string
  status: string
  visibility: string
  coverUrl?: string
  createdAt: string
  publishedAt?: string
  categories?: string[]
  tags?: string[]
}

export type ArticleDetail = {
  id: number
  title: string
  authorId: number
  contentType: string
  renderedContent: string
  visibility: string
  status: string
  coverUrl?: string
  createdAt: string
  updatedAt: string
  publishedAt?: string
  categories?: string[]
  tags?: string[]
}

export type ArticleEdit = {
  id: number
  title: string
  contentType: string
  contentRaw: string
  status: string
  visibility: string
  rssEnabled?: boolean
  allowIndex?: boolean
  summary?: string
  coverUrl?: string
  categories?: string[]
  tags?: string[]
  whitelistUserIds?: number[]
}

export type Comment = {
  id: number
  articleId: number
  userId?: number
  parentId?: number
  guestName?: string
  content: string
  status: string
  createdAt: string
  replies?: Comment[]
}

export type Page = {
  id: number
  title: string
  slug: string
  contentType?: string
  contentHtml?: string
  contentRaw?: string
  visibility: string
  isNav?: boolean
  sortOrder?: number
  externalUrl?: string
  whitelistUserIds?: number[]
  createdAt?: string
  updatedAt?: string
}

export type NavigationGroup = {
  id: number
  name: string
  sortOrder?: number
  links?: NavigationLink[]
}

export type NavigationLink = {
  id: number
  groupId: number
  name: string
  url: string
  icon?: string
  description?: string
  openInNew?: boolean
  sortOrder?: number
}

export type SearchEngine = {
  id: number
  name: string
  queryUrl: string
  enabled: boolean
  isDefault: boolean
}

export type Album = {
  id: number
  title: string
  description?: string
  coverPhotoId?: number
  ownerId: number
  visibility: string
  createdAt: string
  updatedAt: string
  whitelistUserIds?: number[]
}

export type Photo = {
  id: number
  albumId: number
  filename: string
  originalPath: string
  thumbnailPath?: string
  externalUrl?: string
  syncStatus: string
  syncError?: string
  exifJson?: string
  takenAt?: string
  createdAt: string
  tags?: string[]
}

export type Theme = {
  id: number
  name: string
  version: string
  author?: string
  description?: string
  isActive: boolean
  themeJson?: string
  configJson?: string
  publicPath?: string
}

export type CustomCodeVersion = {
  id: number
  type: string
  content: string
  enabled: boolean
  isActive: boolean
  createdAt: string
}

export type User = {
  id: number
  username: string
  email?: string
  role: string
  status: string
  isDefaultAdmin: boolean
  createdAt: string
}

export type AuditLog = {
  id: number
  userId?: number
  action: string
  resourceType?: string
  resourceId?: string
  result?: string
  message?: string
  createdAt: string
}

export type DashboardStats = {
  articlesTotal: number
  articlesPublished: number
  articlesDraft: number
  articlesArchived: number
  commentsTotal: number
  commentsPending: number
  commentsPublished: number
  commentsBlocked: number
  usersTotal: number
  albumsTotal: number
  photosTotal: number
  recentArticles: ArticleSummary[]
  recentComments: Comment[]
  recentAudits: AuditLog[]
}
