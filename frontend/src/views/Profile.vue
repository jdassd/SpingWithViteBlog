<template>
  <section class="container profile-container">
    <div class="card-surface profile-card">
      <div class="section-title">{{ $t('profile.title') }}</div>
      <div class="profile-info">
        <div><strong>{{ $t('login.username') }}:</strong> {{ auth.user?.username }}</div>
        <div><strong>Role:</strong> {{ auth.user?.role }}</div>
      </div>
      <el-divider />
      <div class="section-title">{{ $t('profile.changePassword') }}</div>
      <el-form :model="form" label-position="top">
        <el-form-item :label="$t('profile.currentPassword')">
          <el-input v-model="form.currentPassword" type="password" autocomplete="current-password" />
        </el-form-item>
        <el-form-item :label="$t('profile.newPassword')">
          <el-input v-model="form.newPassword" type="password" autocomplete="new-password" />
        </el-form-item>
        <el-button type="primary" @click="submit">{{ $t('profile.changePassword') }}</el-button>
      </el-form>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const auth = useAuthStore()

const form = reactive({
  currentPassword: '',
  newPassword: '',
})

const submit = async () => {
  try {
    await auth.changePassword(form.currentPassword, form.newPassword)
    form.currentPassword = ''
    form.newPassword = ''
    ElMessage.success(t('profile.passwordChanged'))
  } catch (err: any) {
    ElMessage.error(err?.message || t('common.error'))
  }
}
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
}

.profile-card {
  width: 100%;
  max-width: 520px;
  padding: 24px;
  display: grid;
  gap: 16px;
}

.profile-info {
  display: grid;
  gap: 6px;
}

@media (max-width: 480px) {
  .profile-card {
    padding: 18px;
  }
}
</style>
