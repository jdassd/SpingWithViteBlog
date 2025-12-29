import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import type { Role, User } from '../types';
import { storage } from '../services/storage';
import { ensureSeedData } from '../services/seed';

export const useAuthStore = defineStore('auth', () => {
  ensureSeedData();

  const currentUser = ref<User | null>(null);

  const hydrate = () => {
    const auth = storage.getAuth();
    if (!auth.userId) {
      currentUser.value = null;
      return;
    }
    const user = storage.getUsers().find((item) => item.id === auth.userId) ?? null;
    currentUser.value = user?.status === 'ACTIVE' ? user : null;
  };

  const login = (username: string, password: string) => {
    const users = storage.getUsers();
    const user = users.find((item) => item.username === username && item.password === password);
    if (!user || user.status === 'DISABLED') {
      throw new Error('账号或密码错误，或账号已被禁用');
    }
    storage.setAuth({ userId: user.id });
    currentUser.value = user;
  };

  const register = (username: string, email: string, password: string) => {
    const users = storage.getUsers();
    if (users.some((item) => item.username === username)) {
      throw new Error('用户名已存在');
    }
    if (users.some((item) => item.email === email)) {
      throw new Error('邮箱已存在');
    }
    const newUser: User = {
      id: crypto.randomUUID(),
      username,
      email,
      password,
      role: 'USER',
      status: 'ACTIVE',
      createdAt: new Date().toISOString(),
      avatar: `https://i.pravatar.cc/120?u=${username}`,
    };
    storage.setUsers([...users, newUser]);
    storage.setAuth({ userId: newUser.id });
    currentUser.value = newUser;
  };

  const logout = () => {
    storage.setAuth({ userId: null });
    currentUser.value = null;
  };

  const role = computed<Role>(() => {
    if (!currentUser.value) return 'GUEST';
    return currentUser.value.role === 'ADMIN' ? 'ADMIN' : 'USER';
  });

  const isAdmin = computed(() => role.value === 'ADMIN');
  const isAuthed = computed(() => role.value !== 'GUEST');

  hydrate();

  return {
    currentUser,
    role,
    isAdmin,
    isAuthed,
    login,
    register,
    logout,
    hydrate,
  };
});
