<template>
  <section class="admin-section">
    <div class="toolbar card-surface">
      <el-input v-model="filters.keyword" :placeholder="$t('admin.labels.keyword')" clearable />
      <el-select v-model="filters.role" :placeholder="$t('admin.columns.role')" clearable>
        <el-option :label="$t('admin.roles.admin')" value="ADMIN" />
        <el-option :label="$t('admin.roles.user')" value="USER" />
      </el-select>
      <el-select v-model="filters.status" :placeholder="$t('admin.columns.status')" clearable>
        <el-option :label="$t('admin.statusOptions.active')" value="ACTIVE" />
        <el-option :label="$t('admin.statusOptions.disabled')" value="DISABLED" />
      </el-select>
      <el-button type="primary" @click="loadUsers">{{ $t('admin.filter') }}</el-button>
      <el-button @click="openCreate">{{ $t('admin.newUser') }}</el-button>
    </div>
    <el-table :data="users" stripe>
      <el-table-column prop="username" :label="$t('admin.columns.username')" min-width="160" />
      <el-table-column prop="email" :label="$t('admin.columns.email')" min-width="180" />
      <el-table-column prop="role" :label="$t('admin.columns.role')" width="120" />
      <el-table-column prop="status" :label="$t('admin.columns.status')" width="120" />
      <el-table-column prop="isDefaultAdmin" :label="$t('admin.columns.default')" width="120" />
      <el-table-column :label="$t('admin.columns.actions')" width="320">
        <template #default="{ row }">
          <el-button size="small" @click="openRole(row)">{{ $t('admin.actions.role') }}</el-button>
          <el-button size="small" @click="openStatus(row)">{{ $t('admin.actions.status') }}</el-button>
          <el-button size="small" @click="openReset(row)">{{ $t('admin.actions.resetPw') }}</el-button>
          <el-button size="small" type="danger" @click="deleteUser(row)">{{ $t('admin.actions.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showCreate" width="420px" :title="$t('admin.dialogs.createUser')">
      <el-form :model="createForm" label-position="top">
        <el-form-item :label="$t('admin.columns.username')">
          <el-input v-model="createForm.username" />
        </el-form-item>
        <el-form-item :label="$t('admin.columns.email')">
          <el-input v-model="createForm.email" />
        </el-form-item>
        <el-form-item :label="$t('admin.labels.password')">
          <el-input v-model="createForm.password" type="password" />
        </el-form-item>
        <el-form-item :label="$t('admin.columns.role')">
          <el-select v-model="createForm.role">
            <el-option :label="$t('admin.roles.admin')" value="ADMIN" />
            <el-option :label="$t('admin.roles.user')" value="USER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="createUser">{{ $t('admin.actions.create') }}</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showRole" width="360px" :title="$t('admin.dialogs.updateRole')">
      <el-select v-model="roleValue">
        <el-option :label="$t('admin.roles.admin')" value="ADMIN" />
        <el-option :label="$t('admin.roles.user')" value="USER" />
      </el-select>
      <template #footer>
        <el-button @click="showRole = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="updateRole">{{ $t('common.save') }}</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showStatus" width="360px" :title="$t('admin.dialogs.updateStatus')">
      <el-select v-model="statusValue">
        <el-option :label="$t('admin.statusOptions.active')" value="ACTIVE" />
        <el-option :label="$t('admin.statusOptions.disabled')" value="DISABLED" />
      </el-select>
      <template #footer>
        <el-button @click="showStatus = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="updateStatus">{{ $t('common.save') }}</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showReset" width="360px" :title="$t('admin.dialogs.resetPassword')">
      <el-input v-model="resetPassword" type="password" :placeholder="$t('admin.labels.newPassword')" />
      <template #footer>
        <el-button @click="showReset = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="resetUserPassword">{{ $t('common.save') }}</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import api from '@/api/client'
import type { User } from '@/api/types'

const { t } = useI18n()
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
    ElMessage.success(t('admin.messages.userCreated'))
    showCreate.value = false
    await loadUsers()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.createFailed'))
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
    ElMessage.success(t('admin.messages.updated'))
    showRole.value = false
    await loadUsers()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.updateFailed'))
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
    ElMessage.success(t('admin.messages.updated'))
    showStatus.value = false
    await loadUsers()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.updateFailed'))
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
    ElMessage.success(t('admin.messages.passwordUpdated'))
    showReset.value = false
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.resetFailed'))
  }
}

const deleteUser = async (user: User) => {
  await ElMessageBox.confirm(t('admin.confirms.deleteUser'), t('admin.confirms.confirm'), { type: 'warning' })
  try {
    await api.delete(`/api/admin/users/${user.id}`)
    ElMessage.success(t('admin.messages.deleted'))
    await loadUsers()
  } catch (err: any) {
    ElMessage.error(err?.message || t('admin.messages.deleteFailed'))
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
