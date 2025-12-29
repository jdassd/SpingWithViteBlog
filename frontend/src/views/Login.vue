<template>
  <section class="container auth-container">
    <div class="auth-card card-surface">
      <div class="section-title">{{ $t('login.title') }}</div>
      <el-form :model="form" label-position="top">
        <el-form-item :label="$t('login.username')">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item :label="$t('login.password')">
          <el-input v-model="form.password" type="password" autocomplete="current-password" />
        </el-form-item>
        <el-button type="primary" class="full" @click="handleLogin">{{ $t('login.loginButton') }}</el-button>
      </el-form>
      <p class="muted">
        {{ $t('login.noAccount') }}
        <RouterLink to="/register">{{ $t('login.createOne') }}</RouterLink>
      </p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
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
    ElMessage.success(t('login.success'))
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (err: any) {
    ElMessage.error(err?.message || t('login.failed'))
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
  width: 100%;
  max-width: 420px;
  padding: 28px;
  display: grid;
  gap: 16px;
}

@media (max-width: 480px) {
  .auth-card {
    padding: 20px;
  }
}

.full {
  width: 100%;
}
</style>
