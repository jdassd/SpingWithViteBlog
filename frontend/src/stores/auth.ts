import { defineStore } from 'pinia'
import api from '@/api/client'
import type { AuthResponse, UserSummary } from '@/api/types'
import { clearTokens, getAccessToken, getRefreshToken, setTokens } from '@/utils/auth'

type LoginPayload = {
  username: string
  password: string
}

type RegisterPayload = {
  username: string
  email?: string
  password: string
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as UserSummary | null,
    initialized: false,
  }),
  getters: {
    isAuthenticated: (state) => !!state.user,
    isAdmin: (state) => state.user?.role === 'ADMIN',
  },
  actions: {
    async init() {
      if (this.initialized) {
        return
      }
      this.initialized = true
      if (!getAccessToken()) {
        return
      }
      try {
        const me = await api.get<UserSummary>('/api/auth/me')
        this.user = me
      } catch {
        clearTokens()
        this.user = null
      }
    },
    async login(payload: LoginPayload) {
      const data = await api.post<AuthResponse>('/api/auth/login', payload)
      setTokens(data.accessToken, data.refreshToken)
      this.user = data.user
    },
    async register(payload: RegisterPayload) {
      const data = await api.post<AuthResponse>('/api/auth/register', payload)
      setTokens(data.accessToken, data.refreshToken)
      this.user = data.user
    },
    async logout() {
      const refreshToken = getRefreshToken()
      try {
        if (refreshToken) {
          await api.post('/api/auth/logout', { refreshToken })
        }
      } finally {
        clearTokens()
        this.user = null
        this.initialized = false
      }
    },
    async changePassword(currentPassword: string, newPassword: string) {
      await api.post('/api/auth/change-password', {
        currentPassword,
        newPassword,
      })
    },
  },
})
