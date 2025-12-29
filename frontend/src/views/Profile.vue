<template>
  <section class="container profile-container">
    <div class="card-surface profile-card">
      <div class="section-title">Profile</div>
      <div class="profile-info">
        <div><strong>Username:</strong> {{ auth.user?.username }}</div>
        <div><strong>Role:</strong> {{ auth.user?.role }}</div>
      </div>
      <el-divider />
      <div class="section-title">Change Password</div>
      <el-form :model="form" label-position="top">
        <el-form-item label="Current password">
          <el-input v-model="form.currentPassword" type="password" autocomplete="current-password" />
        </el-form-item>
        <el-form-item label="New password">
          <el-input v-model="form.newPassword" type="password" autocomplete="new-password" />
        </el-form-item>
        <el-button type="primary" @click="submit">Update password</el-button>
      </el-form>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

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
    ElMessage.success('Password updated')
  } catch (err: any) {
    ElMessage.error(err?.message || 'Update failed')
  }
}
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
}

.profile-card {
  width: 520px;
  padding: 24px;
  display: grid;
  gap: 16px;
}

.profile-info {
  display: grid;
  gap: 6px;
}
</style>
