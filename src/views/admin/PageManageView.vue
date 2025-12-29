<template>
  <div>
    <div class="section-title">页面与导航</div>
    <el-button type="primary" @click="openDialog()">新建页面</el-button>
    <el-table :data="pages" stripe style="margin-top: 16px">
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="slug" label="路径" width="140" />
      <el-table-column prop="type" label="类型" width="120" />
      <el-table-column prop="visibility" label="可见性" width="120" />
      <el-table-column prop="showInNav" label="导航显示" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.showInNav ? 'success' : 'info'">
            {{ scope.row.showInNav ? '显示' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button link type="primary" @click="openDialog(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="remove(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="页面配置" width="600px">
      <el-form :model="form" label-position="top">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="路径 (slug)" required>
          <el-input v-model="form.slug" placeholder="例如 about" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type">
            <el-option label="Markdown" value="MARKDOWN" />
            <el-option label="富文本" value="RICH_TEXT" />
            <el-option label="外链" value="LINK" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" v-if="form.type !== 'LINK'">
          <el-input v-model="form.contentRaw" type="textarea" :rows="6" />
        </el-form-item>
        <el-form-item label="外链 URL" v-if="form.type === 'LINK'">
          <el-input v-model="form.linkUrl" />
        </el-form-item>
        <el-form-item label="可见性">
          <el-select v-model="form.visibility">
            <el-option v-for="item in visibilities" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="导航显示">
          <el-switch v-model="form.showInNav" />
        </el-form-item>
        <el-form-item label="排序权重">
          <el-input-number v-model="form.order" :min="0" />
        </el-form-item>
        <el-form-item label="白名单用户">
          <el-select v-model="form.whitelist" multiple filterable>
            <el-option v-for="user in users" :key="user.id" :label="user.username" :value="user.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import type { PageEntry, Visibility } from '../../types';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';
import { storage } from '../../services/storage';

const site = useSiteStore();
const auth = useAuthStore();

const pages = computed(() => site.pages);
const users = computed(() => storage.getUsers());
const visibilities: Visibility[] = ['PUBLIC', 'LOGIN_ONLY', 'WHITELIST', 'PRIVATE', 'ADMIN_ONLY'];

const dialogVisible = ref(false);
const editingId = ref<string | null>(null);

const form = reactive({
  title: '',
  slug: '',
  type: 'MARKDOWN',
  contentRaw: '',
  linkUrl: '',
  visibility: 'PUBLIC',
  showInNav: false,
  order: 0,
  whitelist: [] as string[],
});

const openDialog = (page?: PageEntry) => {
  if (page) {
    editingId.value = page.id;
    Object.assign(form, page);
  } else {
    editingId.value = null;
    Object.assign(form, {
      title: '',
      slug: '',
      type: 'MARKDOWN',
      contentRaw: '',
      linkUrl: '',
      visibility: 'PUBLIC',
      showInNav: false,
      order: 0,
      whitelist: [],
    });
  }
  dialogVisible.value = true;
};

const save = () => {
  if (!form.title.trim() || !form.slug.trim()) {
    ElMessage.error('标题和路径不能为空');
    return;
  }
  if (editingId.value) {
    site.updatePage(editingId.value, { ...form });
    site.log(auth.currentUser, '更新页面', form.title, 'SUCCESS');
  } else {
    if (site.pages.some((page) => page.slug === form.slug)) {
      ElMessage.error('路径已存在');
      return;
    }
    site.addPage({ ...form });
    site.log(auth.currentUser, '新建页面', form.title, 'SUCCESS');
  }
  dialogVisible.value = false;
};

const remove = async (id: string) => {
  await ElMessageBox.confirm('确认删除该页面？', '危险操作', { type: 'warning' });
  const title = site.pages.find((item) => item.id === id)?.title ?? '';
  site.removePage(id);
  site.log(auth.currentUser, '删除页面', title, 'SUCCESS');
};
</script>
