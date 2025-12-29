import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
      '/rss': 'http://localhost:8080',
      '/custom.css': 'http://localhost:8080',
      '/custom.js': 'http://localhost:8080',
      '/uploads': 'http://localhost:8080',
      '/themes': 'http://localhost:8080',
    },
  },
})
