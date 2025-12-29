<template>
  <div>
    <div class="section-title">站点设置</div>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="基础设置" name="basic">
        <el-form :model="form" label-position="top">
          <el-form-item label="站点名称">
            <el-input v-model="form.siteName" />
          </el-form-item>
          <el-form-item label="站点描述">
            <el-input v-model="form.description" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="首页模式">
            <el-radio-group v-model="form.homeMode">
              <el-radio label="BLOG">博客流</el-radio>
              <el-radio label="PORTAL">导航门户</el-radio>
              <el-radio label="THEME">主题自定义</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="安全与风控" name="security">
        <el-form :model="form" label-position="top">
          <el-form-item label="游客评论">
            <el-switch v-model="form.allowGuestComment" />
          </el-form-item>
          <el-form-item label="评论审核模式">
            <el-select v-model="form.commentReviewMode">
              <el-option label="免审" value="NONE" />
              <el-option label="首评审核" value="FIRST" />
              <el-option label="全部审核" value="ALL" />
            </el-select>
          </el-form-item>
          <el-form-item label="允许普通用户发文">
            <el-switch v-model="form.allowUserPost" />
          </el-form-item>
          <el-form-item label="管理员可访问私密内容">
            <el-switch v-model="form.adminCanViewPrivate" />
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="RSS 设置" name="rss">
        <el-form :model="form" label-position="top">
          <el-form-item label="启用公共 RSS">
            <el-switch v-model="form.rssEnabled" />
          </el-form-item>
          <el-form-item label="输出全文">
            <el-switch v-model="form.rssFullContent" />
          </el-form-item>
          <el-form-item label="启用私有 RSS">
            <el-switch v-model="form.rssPrivateEnabled" />
          </el-form-item>
          <el-form-item label="Token 过期天数">
            <el-input-number v-model="form.rssTokenExpireDays" :min="1" />
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="自定义 CSS/JS" name="custom">
        <el-form :model="form" label-position="top">
          <el-form-item label="自定义 CSS">
            <el-input v-model="form.customCss" type="textarea" :rows="6" />
          </el-form-item>
          <el-form-item label="启用自定义 JS">
            <el-switch v-model="form.enableCustomJs" />
          </el-form-item>
          <el-form-item label="自定义 JS">
            <el-input v-model="form.customJs" type="textarea" :rows="6" />
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="导航门户" name="nav">
        <div class="nav-config">
          <div>
            <div class="section-title">搜索引擎</div>
            <el-button type="primary" @click="addEngine">新增引擎</el-button>
            <el-table :data="form.engines" stripe style="margin-top: 12px">
              <el-table-column prop="name" label="名称" width="140" />
              <el-table-column prop="queryTemplate" label="URL 模板" min-width="220" />
              <el-table-column prop="enabled" label="启用" width="100">
                <template #default="scope">
                  <el-switch v-model="scope.row.enabled" />
                </template>
              </el-table-column>
              <el-table-column prop="isDefault" label="默认" width="100">
                <template #default="scope">
                  <el-radio v-model="defaultEngineId" :label="scope.row.id">默认</el-radio>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button link type="danger" @click="removeEngine(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div style="margin-top: 24px">
            <div class="section-title">导航分组</div>
            <el-button type="primary" @click="addGroup">新增分组</el-button>
            <el-table :data="form.navigationGroups" stripe style="margin-top: 12px">
              <el-table-column prop="name" label="分组" width="160" />
              <el-table-column label="站点卡片" min-width="220">
                <template #default="scope">
                  {{ scope.row.items.map((item) => item.name).join('、') }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="160">
                <template #default="scope">
                  <el-button link @click="editGroup(scope.row)">编辑</el-button>
                  <el-button link type="danger" @click="removeGroup(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-space style="margin-top: 16px">
      <el-button type="primary" @click="save">保存设置</el-button>
    </el-space>

    <el-dialog v-model="groupDialogVisible" title="导航分组" width="560px">
      <el-form :model="groupForm" label-position="top">
        <el-form-item label="分组名称">
          <el-input v-model="groupForm.name" />
        </el-form-item>
        <el-form-item label="站点卡片">
          <el-button type="primary" @click="addGroupItem">新增卡片</el-button>
          <div v-for="(item, index) in groupForm.items" :key="item.id" class="group-item">
            <el-input v-model="item.name" placeholder="名称" />
            <el-input v-model="item.url" placeholder="URL" />
            <el-input v-model="item.description" placeholder="描述" />
            <el-switch v-model="item.openInNew" active-text="新窗口" />
            <el-button link type="danger" @click="removeGroupItem(index)">删除</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="groupDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveGroup">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { v4 as uuid } from 'uuid';
import { ElMessageBox } from 'element-plus';
import type { SiteSettings, NavigationGroup } from '../../types';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const site = useSiteStore();
const auth = useAuthStore();

const activeTab = ref('basic');
const form = reactive<SiteSettings>({ ...site.settings });
const defaultEngineId = ref(form.engines.find((item) => item.isDefault)?.id);

watch(defaultEngineId, (value) => {
  form.engines = form.engines.map((engine) => ({
    ...engine,
    isDefault: engine.id === value,
  }));
});

const save = () => {
  site.updateSettings({ ...form });
  site.updateEngines(form.engines);
  site.updateNavigationGroups(form.navigationGroups);
  site.log(auth.currentUser, '更新站点设置', '站点设置', 'SUCCESS');
};

const addEngine = () => {
  form.engines.push({
    id: uuid(),
    name: '新引擎',
    queryTemplate: 'https://example.com/search?q={q}',
    enabled: true,
    isDefault: false,
  });
};

const removeEngine = (id: string) => {
  form.engines = form.engines.filter((engine) => engine.id !== id);
};

const groupDialogVisible = ref(false);
const editingGroupId = ref<string | null>(null);
const groupForm = reactive<NavigationGroup>({
  id: '',
  name: '',
  order: 0,
  items: [],
});

const addGroup = () => {
  editingGroupId.value = null;
  Object.assign(groupForm, { id: uuid(), name: '', order: 0, items: [] });
  groupDialogVisible.value = true;
};

const editGroup = (group: NavigationGroup) => {
  editingGroupId.value = group.id;
  Object.assign(groupForm, JSON.parse(JSON.stringify(group)));
  groupDialogVisible.value = true;
};

const saveGroup = () => {
  if (editingGroupId.value) {
    form.navigationGroups = form.navigationGroups.map((group) =>
      group.id === editingGroupId.value ? { ...groupForm } : group
    );
  } else {
    form.navigationGroups.push({ ...groupForm });
  }
  groupDialogVisible.value = false;
};

const removeGroup = async (id: string) => {
  await ElMessageBox.confirm('确认删除该分组？', '危险操作', { type: 'warning' });
  form.navigationGroups = form.navigationGroups.filter((group) => group.id !== id);
};

const addGroupItem = () => {
  groupForm.items.push({
    id: uuid(),
    name: '新站点',
    url: '',
    description: '',
    openInNew: true,
  });
};

const removeGroupItem = (index: number) => {
  groupForm.items.splice(index, 1);
};
</script>

<style scoped>
.nav-config {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.group-item {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr auto auto;
  gap: 8px;
  align-items: center;
  margin-top: 8px;
}
</style>
