<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <el-input v-model="filters.keyword" placeholder="Keyword" clearable />
      <el-select v-model="filters.role" placeholder="Role" clearable>
        <el-option label="Admin" value="ADMIN" />
        <el-option label="User" value="USER" />
      </el-select>
      <el-select v-model="filters.status" placeholder="Status" clearable>
        <el-option label="Active" value="ACTIVE" />
        <el-option label="DISABLED" value="DISABLED" />
      </el-select>
      <el-button type="primary" @click="loadUsers">Filter</el-button>
      <el-button @click="openCreate">New User</el-button>
    </div>
    <el-table :data="users" stripe>
      <el-table-column prop="username" label="Username" min-width="160" />
      <el-table-column prop="email" label="Email" min-width="180" />
      <el-table-column prop="role" label="Role" width="120" />
      <el-table-column prop="status" label="Status" width="120" />
      <el-table-column prop="isDefaultAdmin" label="Default" width="120" />
      <el-table-column label="Actions" width="320">
        <template #default="{ row }">
          <el-button size="small" @click="openRole(row)">Role</el-button>
          <el-button size="small" @click="openStatus(row)">Status</el-button>
          <el-button size="small" @click="openReset(row)">Reset PW</el-button>
          <el-button size="small" type="danger" @click="deleteUser(row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showCreate" width="420px" title="Create User">
      <el-form :model="createForm" label-position="top">
        <el-form-item label="Username">
          <el-input v-model="createForm.username" />
        </el-form-item>
        <el-form-item label="Email">
          <el-input v-model="createForm.email" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="createForm.password" type="password" />
        </el-form-item>
        <el-form-item label="Role">
          <el-select v-model="createForm.role">
            <el-option label="Admin" value="ADMIN" />
            <el-option label="User" value="USER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">Cancel</el-button>
        <el-button type="primary" @click="createUser">Create</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showRole" width="360px" title="Update Role">
      <el-select v-model="roleValue">
        <el-option label="Admin" value="ADMIN" />
        <el-option label="User" value="USER" />
      </el-select>
      <template #footer>
        <el-button @click="showRole = false">Cancel</el-button>
        <el-button type="primary" @click="updateRole">Save</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showStatus" width="360px" title="Update Status">
      <el-select v-model="statusValue">
        <el-option label="Active" value="ACTIVE" />
        <el-option label="Disabled" value="DISABLED" />
      </el-select>
      <template #footer>
        <el-button @click="showStatus = false">Cancel</el-button>
        <el-button type="primary" @click="updateStatus">Save</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showReset" width="360px" title="Reset Password">
      <el-input v-model="resetPassword" type="password" placeholder="New password" />
      <template #footer>
        <el-button @click="showReset = false">Cancel</el-button>
        <el-button type="primary" @click="resetUserPassword">Save</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api/client'
import type { User } from '@/api/types'

const users = ref<User[]>([])
const filters = reactive({
  keyword: '',
  role: '',
  status: '',
})

const showCreate = ref(false)
const createForm = reactive({
  username: '',
  email: '',
  password: '',
  role: 'USER',
})

const showRole = ref(false)
const showStatus = ref(false)
const showReset = ref(false)
const selectedUserId = ref<number | null>(null)
const roleValue = ref('USER')
const statusValue = ref('ACTIVE')
const resetPassword = ref('')

const loadUsers = async () => {
  users.value = await api.get<User[]>('/api/admin/users', {
    params: {
      keyword: filters.keyword || undefined,
      role: filters.role || undefined,
      status: filters.status || undefined,
    },
  })
}

const openCreate = () => {
  createForm.username = ''
  createForm.email = ''
  createForm.password = ''
  createForm.role = 'USER'
  showCreate.value = true
}

const createUser = async () => {
  try {
    await api.post('/api/admin/users', createForm)
    ElMessage.success('User created')
    showCreate.value = false
    await loadUsers()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Create failed')
  }
}

const openRole = (user: User) => {
  selectedUserId.value = user.id
  roleValue.value = user.role
  showRole.value = true
}

const updateRole = async () => {
  if (!selectedUserId.value) {
    return
  }
  try {
    await api.patch(`/api/admin/users/${selectedUserId.value}/role`, { role: roleValue.value })
    ElMessage.success('Updated')
    showRole.value = false
    await loadUsers()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Update failed')
  }
}

const openStatus = (user: User) => {
  selectedUserId.value = user.id
  statusValue.value = user.status
  showStatus.value = true
}

const updateStatus = async () => {
  if (!selectedUserId.value) {
    return
  }
  try {
    await api.patch(`/api/admin/users/${selectedUserId.value}/status`, { status: statusValue.value })
    ElMessage.success('Updated')
    showStatus.value = false
    await loadUsers()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Update failed')
  }
}

const openReset = (user: User) => {
  selectedUserId.value = user.id
  resetPassword.value = ''
  showReset.value = true
}

const resetUserPassword = async () => {
  if (!selectedUserId.value) {
    return
  }
  try {
    await api.post(`/api/admin/users/${selectedUserId.value}/reset-password`, {
      newPassword: resetPassword.value,
    })
    ElMessage.success('Password updated')
    showReset.value = false
  } catch (err: any) {
    ElMessage.error(err?.message || 'Reset failed')
  }
}

const deleteUser = async (user: User) => {
  await ElMessageBox.confirm('Delete this user?', 'Confirm', { type: 'warning' })
  try {
    await api.delete(`/api/admin/users/${user.id}`)
    ElMessage.success('Deleted')
    await loadUsers()
  } catch (err: any) {
    ElMessage.error(err?.message || 'Delete failed')
  }
}

onMounted(loadUsers)
</script>

<style scoped>
.admin-section {
  display: grid;
  gap: 16px;
}

.toolbar {
  padding: 16px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
