<template>
  <section class="container auth-container">
    <div class="auth-card card-surface">
      <div class="section-title">Create account</div>
      <el-form :model="form" label-position="top">
        <el-form-item label="Username">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="Email (optional)">
          <el-input v-model="form.email" autocomplete="email" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="form.password" type="password" autocomplete="new-password" />
        </el-form-item>
        <el-button type="primary" class="full" @click="handleRegister">Register</el-button>
      </el-form>
      <p class="muted">
        Already have an account?
        <RouterLink to="/login">Login</RouterLink>
      </p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const form = reactive({
  username: '',
  email: '',
  password: '',
})

const handleRegister = async () => {
  try {
    await auth.register(form)
    ElMessage.success('Account created')
    router.push('/')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Registration failed')
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
