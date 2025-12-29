<template>
  <div>
    <div class="section-title">评论管理</div>
    <div class="filters">
      <el-input v-model="filters.keyword" placeholder="评论内容关键字" clearable />
      <el-select v-model="filters.status" placeholder="状态" clearable>
        <el-option label="待审" value="PENDING" />
        <el-option label="已发布" value="PUBLISHED" />
        <el-option label="已屏蔽" value="BLOCKED" />
      </el-select>
    </div>
    <el-table :data="filteredComments" stripe>
      <el-table-column prop="content" label="评论内容" min-width="200" />
      <el-table-column prop="authorName" label="评论者" width="120" />
      <el-table-column prop="articleId" label="文章" min-width="180">
        <template #default="scope">
          {{ articleTitle(scope.row.articleId) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag>{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 'PENDING'"
            link
            type="primary"
            @click="approve(scope.row.id)"
          >
            通过
          </el-button>
          <el-button link type="warning" @click="block(scope.row.id)">屏蔽</el-button>
          <el-button link type="danger" @click="remove(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue';
import { ElMessageBox } from 'element-plus';
import { useSiteStore } from '../../stores/site';
import { useAuthStore } from '../../stores/auth';

const site = useSiteStore();
const auth = useAuthStore();

const filters = reactive({
  keyword: '',
  status: '',
});

const filteredComments = computed(() =>
  site.comments.filter((comment) => {
    if (filters.keyword && !comment.content.includes(filters.keyword)) return false;
    if (filters.status && comment.status !== filters.status) return false;
    return true;
  })
);

const articleTitle = (articleId: string) => site.articles.find((item) => item.id === articleId)?.title ?? '-';

const approve = (id: string) => {
  site.updateCommentStatus(id, 'PUBLISHED');
  site.log(auth.currentUser, '审核评论', id, 'SUCCESS');
};

const block = (id: string) => {
  site.updateCommentStatus(id, 'BLOCKED');
  site.log(auth.currentUser, '屏蔽评论', id, 'SUCCESS');
};

const remove = async (id: string) => {
  await ElMessageBox.confirm('确认删除该评论？', '危险操作', { type: 'warning' });
  site.removeComment(id);
  site.log(auth.currentUser, '删除评论', id, 'SUCCESS');
};
</script>

<style scoped>
.filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}
</style>
