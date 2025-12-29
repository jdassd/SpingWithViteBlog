<template>
  <div>
    <div class="section-title">用户管理</div>
    <el-table :data="users" stripe>
      <el-table-column prop="username" label="用户名" width="160" />
      <el-table-column prop="email" label="邮箱" min-width="200" />
      <el-table-column prop="role" label="角色" width="120" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'warning'">
            {{ scope.row.status === 'ACTIVE' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button link @click="toggleStatus(scope.row)">
            {{ scope.row.status === 'ACTIVE' ? '禁用' : '启用' }}
          </el-button>
          <el-button link @click="toggleRole(scope.row)">
            设为 {{ scope.row.role === 'ADMIN' ? 'User' : 'Admin' }}
          </el-button>
          <el-button link type="danger" @click="resetPassword(scope.row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import type { User } from '../../types';
import { storage } from '../../services/storage';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const site = useSiteStore();
const auth = useAuthStore();

const users = computed(() => storage.getUsers());

const saveUsers = (updated: User[]) => storage.setUsers(updated);

const toggleStatus = (user: User) => {
  const updated = users.value.map((item) =>
    item.id === user.id ? { ...item, status: item.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE' } : item
  );
  saveUsers(updated);
  site.log(auth.currentUser, '更新用户状态', user.username, 'SUCCESS');
};

const toggleRole = async (user: User) => {
  if (user.role !== 'ADMIN') {
    await ElMessageBox.confirm('确认将该用户提升为管理员？', '权限变更', { type: 'warning' });
  }
  const updated = users.value.map((item) =>
    item.id === user.id ? { ...item, role: item.role === 'ADMIN' ? 'USER' : 'ADMIN' } : item
  );
  saveUsers(updated);
  site.log(auth.currentUser, '更新用户角色', user.username, 'SUCCESS');
};

const resetPassword = async (user: User) => {
  await ElMessageBox.confirm('确认重置该用户密码？', '危险操作', { type: 'warning' });
  const updated = users.value.map((item) => (item.id === user.id ? { ...item, password: 'Temp1234' } : item));
  saveUsers(updated);
  ElMessage.success('已重置为 Temp1234');
  site.log(auth.currentUser, '重置密码', user.username, 'SUCCESS');
};
</script>
