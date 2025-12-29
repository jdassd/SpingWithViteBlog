<template>
  <section class="container auth-container">
    <div class="auth-card card-surface">
      <div class="section-title">{{ $t('register.title') }}</div>
      <el-form :model="form" label-position="top">
        <el-form-item :label="$t('register.username')">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item :label="$t('register.email')">
          <el-input v-model="form.email" autocomplete="email" />
        </el-form-item>
        <el-form-item :label="$t('register.password')">
          <el-input v-model="form.password" type="password" autocomplete="new-password" />
        </el-form-item>
        <el-button type="primary" class="full" @click="handleRegister">{{ $t('register.registerButton') }}</el-button>
      </el-form>
      <p class="muted">
        {{ $t('register.hasAccount') }}
        <RouterLink to="/login">{{ $t('register.loginNow') }}</RouterLink>
      </p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
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
    ElMessage.success(t('register.success'))
    router.push('/')
  } catch (err: any) {
    ElMessage.error(err?.message || t('register.failed'))
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
