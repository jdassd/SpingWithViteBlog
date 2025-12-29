<template>
  <div class="page-container">
    <el-card class="auth-card">
      <div class="section-title">注册</div>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="submit">注册</el-button>
      </el-form>
      <div class="auth-tip">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const auth = useAuthStore();

const form = reactive({
  username: '',
  email: '',
  password: '',
});

const submit = () => {
  try {
    auth.register(form.username, form.email, form.password);
    ElMessage.success('注册成功');
    router.push('/');
  } catch (error) {
    ElMessage.error((error as Error).message);
  }
};
</script>

<style scoped>
.auth-card {
  max-width: 420px;
  margin: 40px auto;
  padding: 16px;
}

.auth-tip {
  margin-top: 12px;
  text-align: center;
}
</style>
