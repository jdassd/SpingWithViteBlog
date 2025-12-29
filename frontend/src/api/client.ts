import axios, { type AxiosError, type AxiosInstance, type AxiosRequestConfig } from 'axios'
import { clearTokens, getAccessToken, getRefreshToken, setTokens } from '@/utils/auth'

type ApiError = { code?: string; message?: string }
type ApiResponse<T> = { success: boolean; data: T; error?: ApiError }
type ApiInstance = Omit<
  AxiosInstance,
  'request' | 'get' | 'delete' | 'post' | 'put' | 'patch'
> & {
  <T = any, D = any>(config: AxiosRequestConfig<D>): Promise<T>
  request<T = any, D = any>(config: AxiosRequestConfig<D>): Promise<T>
  get<T = any, D = any>(url: string, config?: AxiosRequestConfig<D>): Promise<T>
  delete<T = any, D = any>(url: string, config?: AxiosRequestConfig<D>): Promise<T>
  post<T = any, D = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<T>
  put<T = any, D = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<T>
  patch<T = any, D = any>(url: string, data?: D, config?: AxiosRequestConfig<D>): Promise<T>
}

const apiBase = import.meta.env.VITE_API_BASE || ''

const api = axios.create({
  baseURL: apiBase,
  timeout: 15000,
}) as ApiInstance

api.interceptors.request.use((config) => {
  const token = getAccessToken()
  if (token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let refreshPromise: Promise<string> | null = null

async function refreshAccessToken(): Promise<string> {
  if (refreshPromise) {
    return refreshPromise
  }
  const refreshToken = getRefreshToken()
  if (!refreshToken) {
    throw new Error('Missing refresh token')
  }
  refreshPromise = fetch(`${apiBase}/api/auth/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken }),
  })
    .then(async (res) => {
      if (!res.ok) {
        throw new Error('Refresh failed')
      }
      const body = (await res.json()) as ApiResponse<{ accessToken: string; refreshToken: string }>
      if (!body.success) {
        throw new Error(body.error?.message || 'Refresh failed')
      }
      setTokens(body.data.accessToken, body.data.refreshToken)
      return body.data.accessToken
    })
    .finally(() => {
      refreshPromise = null
    })
  return refreshPromise
}

function unwrapResponse<T>(data: ApiResponse<T> | T): T {
  if (data && typeof data === 'object' && 'success' in data) {
    const body = data as ApiResponse<T>
    if (!body.success) {
      throw body.error || { message: 'Request failed' }
    }
    return body.data
  }
  return data as T
}

api.interceptors.response.use(
  (response) => unwrapResponse(response.data),
  async (error: AxiosError<ApiError>) => {
    const original = error.config as AxiosRequestConfig & { _retry?: boolean }
    if (error.response?.status === 401 && original && !original._retry) {
      original._retry = true
      try {
        const accessToken = await refreshAccessToken()
        original.headers = original.headers || {}
        original.headers.Authorization = `Bearer ${accessToken}`
        return api(original)
      } catch (refreshError) {
        clearTokens()
        return Promise.reject(refreshError)
      }
    }
    const data = error.response?.data as any
    if (data?.error?.message) {
      return Promise.reject({ message: data.error.message, code: data.error.code })
    }
    if (data?.message) {
      return Promise.reject({ message: data.message })
    }
    return Promise.reject(error)
  }
)

export default api
