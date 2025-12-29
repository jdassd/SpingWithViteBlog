<template>
  <div>
    <div class="section-title">主题管理</div>
    <div class="toolbar">
      <el-button type="primary" @click="openImportDialog">导入主题</el-button>
    </div>
    <div class="card-grid">
      <el-card v-for="theme in themes" :key="theme.id" shadow="hover" class="theme-card">
        <img v-if="theme.preview" :src="theme.preview" class="theme-preview" />
        <div class="theme-info">
          <h3>{{ theme.name }}</h3>
          <p>{{ theme.description }}</p>
          <div class="meta">版本 {{ theme.version }} · {{ theme.author }}</div>
          <el-space>
            <el-button type="primary" @click="applyTheme(theme)">
              {{ isActive(theme) ? '已启用' : '启用' }}
            </el-button>
            <el-button @click="openConfig(theme)">配置</el-button>
            <el-button @click="exportTheme(theme)">导出</el-button>
            <el-button type="danger" @click="removeTheme(theme.id)">删除</el-button>
          </el-space>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="configDialogVisible" title="主题配置" width="520px">
      <el-form :model="themeConfig" label-position="top">
        <el-form-item label="主色">
          <el-color-picker v-model="themeConfig.primaryColor" />
        </el-form-item>
        <el-form-item label="字体">
          <el-input v-model="themeConfig.fontFamily" />
        </el-form-item>
        <el-form-item label="圆角">
          <el-input v-model="themeConfig.radius" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveConfig">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="导入主题" width="500px">
      <el-form :model="importForm" label-position="top">
        <el-form-item label="主题名称">
          <el-input v-model="importForm.name" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="importForm.author" />
        </el-form-item>
        <el-form-item label="版本">
          <el-input v-model="importForm.version" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="importForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="预览图 URL">
          <el-input v-model="importForm.preview" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="importTheme">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { ElMessageBox } from 'element-plus';
import type { Theme } from '../../types';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const site = useSiteStore();
const auth = useAuthStore();

const themes = computed(() => site.themes);

const configDialogVisible = ref(false);
const importDialogVisible = ref(false);
const editingThemeId = ref<string | null>(null);
const themeConfig = reactive({
  primaryColor: '#2563eb',
  fontFamily: '"Segoe UI", "PingFang SC", sans-serif',
  radius: '12px',
});

const importForm = reactive({
  name: '',
  author: '',
  version: '1.0.0',
  description: '',
  preview: '',
});

const isActive = (theme: Theme) => site.settings.currentThemeId === theme.id;

const applyTheme = (theme: Theme) => {
  site.updateSettings({ currentThemeId: theme.id });
  site.log(auth.currentUser, '切换主题', theme.name, 'SUCCESS');
};

const openConfig = (theme: Theme) => {
  editingThemeId.value = theme.id;
  Object.assign(themeConfig, theme.config);
  configDialogVisible.value = true;
};

const saveConfig = () => {
  if (!editingThemeId.value) return;
  site.updateTheme(editingThemeId.value, { config: { ...themeConfig } });
  site.log(auth.currentUser, '更新主题配置', editingThemeId.value, 'SUCCESS');
  configDialogVisible.value = false;
};

const exportTheme = (theme: Theme) => {
  const blob = new Blob([JSON.stringify(theme, null, 2)], { type: 'application/json' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = `${theme.name}.json`;
  link.click();
};

const openImportDialog = () => {
  Object.assign(importForm, { name: '', author: '', version: '1.0.0', description: '', preview: '' });
  importDialogVisible.value = true;
};

const importTheme = () => {
  site.addTheme({
    name: importForm.name || '导入主题',
    author: importForm.author || '未知',
    version: importForm.version,
    description: importForm.description,
    preview: importForm.preview,
    source: 'IMPORTED',
  });
  site.log(auth.currentUser, '导入主题', importForm.name, 'SUCCESS');
  importDialogVisible.value = false;
};

const removeTheme = async (id: string) => {
  if (site.settings.currentThemeId === id) {
    await ElMessageBox.alert('当前启用主题无法删除，请先切换主题。');
    return;
  }
  await ElMessageBox.confirm('确认删除该主题？', '危险操作', { type: 'warning' });
  site.removeTheme(id);
  site.log(auth.currentUser, '删除主题', id, 'SUCCESS');
};
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}

.theme-card {
  overflow: hidden;
}

.theme-preview {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 12px;
}

.theme-info {
  margin-top: 12px;
}

.meta {
  color: #64748b;
  font-size: 13px;
  margin: 8px 0 12px;
}
</style>
