<template>
  <div class="page-container">
    <el-card class="auth-card">
      <div class="section-title">登录</div>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="submit">登录</el-button>
      </el-form>
      <div class="auth-tip">
        没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const route = useRoute();
const auth = useAuthStore();

const form = reactive({
  username: '',
  password: '',
});

const submit = () => {
  try {
    auth.login(form.username, form.password);
    ElMessage.success('登录成功');
    const redirect = (route.query.redirect as string) || '/';
    router.push(redirect);
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
