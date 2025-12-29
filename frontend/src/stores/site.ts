import { defineStore } from 'pinia'
import api from '@/api/client'
import type { NavigationGroup, Page, SearchEngine, Theme } from '@/api/types'

type SettingMap = Record<string, string>

export const useSiteStore = defineStore('site', {
  state: () => ({
    settings: {} as SettingMap,
    navPages: [] as Page[],
    navGroups: [] as NavigationGroup[],
    searchEngines: [] as SearchEngine[],
    activeTheme: null as Theme | null,
    loaded: false,
  }),
  actions: {
    async loadPublic() {
      if (this.loaded) {
        return
      }
      await Promise.all([this.loadSettings(), this.loadNavPages(), this.loadNavigation(), this.loadTheme()])
      this.applyBranding()
      this.loaded = true
    },
    async loadSettings() {
      const keys = [
        'site_name',
        'site_description',
        'site_logo',
        'site_favicon',
        'home_mode',
        'allow_guest_comment',
        'comment_review_mode',
        'rss_public_enabled',
        'rss_private_enabled',
        'rss_full_content',
        'site_url',
      ]
      const data = await api.get<SettingMap>('/api/public/settings', {
        params: { keys: keys.join(',') },
      })
      this.settings = { ...this.settings, ...data }
    },
    async loadNavPages() {
      this.navPages = await api.get<Page[]>('/api/public/pages/nav')
    },
    async loadNavigation() {
      const [groups, engines] = await Promise.all([
        api.get<NavigationGroup[]>('/api/public/navigation'),
        api.get<SearchEngine[]>('/api/public/navigation/search-engines'),
      ])
      this.navGroups = groups
      this.searchEngines = engines
    },
    async loadTheme() {
      this.activeTheme = await api.get<Theme | null>('/api/public/themes/active')
    },
    applyBranding() {
      if (this.settings.site_name) {
        document.title = this.settings.site_name
      }
      const favicon = this.settings.site_favicon
      if (favicon) {
        const link = document.querySelector("link[rel='icon']") as HTMLLinkElement | null
        if (link) {
          link.href = favicon
        }
      }
    },
  },
})
