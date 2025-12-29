<template>
  <section class="container auth-container">
    <div class="auth-card card-surface">
      <div class="section-title">Welcome back</div>
      <el-form :model="form" label-position="top">
        <el-form-item label="Username">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="form.password" type="password" autocomplete="current-password" />
        </el-form-item>
        <el-button type="primary" class="full" @click="handleLogin">Login</el-button>
      </el-form>
      <p class="muted">
        No account?
        <RouterLink to="/register">Create one</RouterLink>
      </p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const form = reactive({
  username: '',
  password: '',
})

const handleLogin = async () => {
  try {
    await auth.login(form)
    ElMessage.success('Logged in')
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (err: any) {
    ElMessage.error(err?.message || 'Login failed')
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.auth-card {
  width: 420px;
  padding: 28px;
  display: grid;
  gap: 16px;
}

.full {
  width: 100%;
}
</style>
