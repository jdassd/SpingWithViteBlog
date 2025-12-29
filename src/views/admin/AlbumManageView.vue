<template>
  <div>
    <div class="section-title">相册管理</div>
    <el-button type="primary" @click="openAlbumDialog()">新建相册</el-button>
    <el-table :data="albums" stripe style="margin-top: 16px">
      <el-table-column prop="title" label="标题" min-width="160" />
      <el-table-column prop="visibility" label="可见性" width="120" />
      <el-table-column prop="updatedAt" label="更新时间" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.updatedAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button link type="primary" @click="openPhotoDrawer(scope.row)">进入管理</el-button>
          <el-button link @click="openAlbumDialog(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="removeAlbum(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="albumDialogVisible" title="相册配置" width="520px">
      <el-form :model="albumForm" label-position="top">
        <el-form-item label="标题" required>
          <el-input v-model="albumForm.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="albumForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="可见性">
          <el-select v-model="albumForm.visibility">
            <el-option v-for="item in visibilities" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="albumDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAlbum">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="photoDrawerVisible" title="相册管理" size="60%">
      <template #default>
        <div v-if="activeAlbum">
          <div class="drawer-header">
            <div>
              <div class="section-title">{{ activeAlbum.title }}</div>
              <p>{{ activeAlbum.description }}</p>
            </div>
            <el-upload
              :auto-upload="false"
              :show-file-list="false"
              accept="image/*"
              @change="handleUpload"
            >
              <el-button type="primary">上传照片</el-button>
            </el-upload>
          </div>
          <div class="photo-grid">
            <el-card v-for="photo in albumPhotos" :key="photo.id" shadow="hover">
              <img :src="photo.thumbUrl" class="photo" />
              <div class="photo-actions">
                <el-tag size="small">{{ photo.status }}</el-tag>
                <el-button link size="small" @click="setCover(photo.url)">设为封面</el-button>
                <el-button link size="small" type="danger" @click="removePhoto(photo.id)">删除</el-button>
              </div>
            </el-card>
          </div>
          <el-empty v-if="albumPhotos.length === 0" description="暂无照片" />
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import dayjs from 'dayjs';
import type { Album, Visibility } from '../../types';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const site = useSiteStore();
const auth = useAuthStore();

const albums = computed(() => site.albums);
const visibilities: Visibility[] = ['PUBLIC', 'LOGIN_ONLY', 'WHITELIST', 'PRIVATE', 'ADMIN_ONLY'];

const albumDialogVisible = ref(false);
const editingAlbumId = ref<string | null>(null);
const albumForm = reactive({
  title: '',
  description: '',
  visibility: 'PUBLIC',
});

const photoDrawerVisible = ref(false);
const activeAlbum = ref<Album | null>(null);

const albumPhotos = computed(() =>
  activeAlbum.value ? site.photos.filter((item) => item.albumId === activeAlbum.value?.id) : []
);

const openAlbumDialog = (album?: Album) => {
  if (album) {
    editingAlbumId.value = album.id;
    Object.assign(albumForm, {
      title: album.title,
      description: album.description ?? '',
      visibility: album.visibility,
    });
  } else {
    editingAlbumId.value = null;
    Object.assign(albumForm, { title: '', description: '', visibility: 'PUBLIC' });
  }
  albumDialogVisible.value = true;
};

const saveAlbum = () => {
  if (!albumForm.title.trim()) {
    ElMessage.error('标题不能为空');
    return;
  }
  if (editingAlbumId.value) {
    site.updateAlbum(editingAlbumId.value, { ...albumForm });
    site.log(auth.currentUser, '更新相册', albumForm.title, 'SUCCESS');
  } else if (auth.currentUser) {
    site.addAlbum({ ...albumForm }, auth.currentUser);
    site.log(auth.currentUser, '新建相册', albumForm.title, 'SUCCESS');
  }
  albumDialogVisible.value = false;
};

const removeAlbum = async (id: string) => {
  await ElMessageBox.confirm('确认删除相册及其照片？', '危险操作', { type: 'warning' });
  const title = site.albums.find((item) => item.id === id)?.title ?? '';
  site.removeAlbum(id);
  site.log(auth.currentUser, '删除相册', title, 'SUCCESS');
};

const openPhotoDrawer = (album: Album) => {
  activeAlbum.value = album;
  photoDrawerVisible.value = true;
};

const handleUpload = async (file: any) => {
  if (!activeAlbum.value) return;
  const raw = file.raw as File;
  const reader = new FileReader();
  reader.onload = () => {
    const url = reader.result as string;
    site.addPhoto({
      albumId: activeAlbum.value?.id,
      url,
      thumbUrl: url,
      status: 'LOCAL',
      createdAt: new Date().toISOString(),
    });
    site.log(auth.currentUser, '上传照片', activeAlbum.value?.title ?? '', 'SUCCESS');
  };
  reader.readAsDataURL(raw);
};

const setCover = (url: string) => {
  if (!activeAlbum.value) return;
  site.updateAlbum(activeAlbum.value.id, { coverUrl: url });
};

const removePhoto = (photoId: string) => {
  site.removePhoto(photoId);
};

const formatTime = (value: string) => dayjs(value).format('YYYY-MM-DD HH:mm');
</script>

<style scoped>
.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.photo {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 10px;
}

.photo-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
</style>
