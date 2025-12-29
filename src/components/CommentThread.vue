<template>
  <div class="comment-thread">
    <div class="comment-header">
      <div class="section-title">评论 ({{ visibleComments.length }})</div>
      <el-select v-model="sortOrder" size="small" style="width: 120px">
        <el-option label="最新" value="desc" />
        <el-option label="最早" value="asc" />
      </el-select>
    </div>
    <div v-if="canPost" class="comment-form">
      <el-input
        v-model="newComment"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论..."
      />
      <div class="comment-actions">
        <el-button type="primary" @click="submitComment">发表评论</el-button>
      </div>
    </div>
    <el-alert v-else type="info" show-icon :closable="false" title="登录后才能发表评论" />
    <div v-if="visibleComments.length" class="comment-list">
      <div v-for="comment in sortedComments" :key="comment.id" class="comment-item">
        <div class="comment-meta">
          <strong>{{ comment.authorName }}</strong>
          <span>{{ formatTime(comment.createdAt) }}</span>
        </div>
        <p>{{ comment.content }}</p>
        <div class="comment-actions">
          <el-button link size="small" @click="toggleReply(comment.id)">回复</el-button>
          <el-button
            v-if="canDelete(comment)"
            link
            size="small"
            type="danger"
            @click="deleteComment(comment.id)"
          >
            删除
          </el-button>
        </div>
        <div v-if="replyingTo === comment.id" class="reply-box">
          <el-input v-model="replyContent" type="textarea" :rows="2" placeholder="回复内容" />
          <div class="comment-actions">
            <el-button type="primary" size="small" @click="submitReply(comment.id)">提交回复</el-button>
            <el-button size="small" @click="cancelReply">取消</el-button>
          </div>
        </div>
        <div v-if="childrenMap[comment.id]?.length" class="reply-list">
          <div v-for="reply in childrenMap[comment.id]" :key="reply.id" class="reply-item">
            <div class="comment-meta">
              <strong>{{ reply.authorName }}</strong>
              <span>{{ formatTime(reply.createdAt) }}</span>
            </div>
            <p>{{ reply.content }}</p>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-else class="empty-state" description="还没有评论" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import dayjs from 'dayjs';
import { useAuthStore } from '../stores/auth';
import { useSiteStore } from '../stores/site';
import type { Comment } from '../types';

const props = defineProps<{ articleId: string }>();

const auth = useAuthStore();
const site = useSiteStore();

const sortOrder = ref<'asc' | 'desc'>('desc');
const newComment = ref('');
const replyContent = ref('');
const replyingTo = ref<string | null>(null);

const visibleComments = computed(() =>
  site.comments.filter((comment) => comment.articleId === props.articleId && comment.status === 'PUBLISHED')
);

const rootComments = computed(() => visibleComments.value.filter((comment) => !comment.parentId));

const childrenMap = computed(() => {
  const map: Record<string, Comment[]> = {};
  visibleComments.value
    .filter((comment) => comment.parentId)
    .forEach((comment) => {
      if (!comment.parentId) return;
      if (!map[comment.parentId]) {
        map[comment.parentId] = [];
      }
      map[comment.parentId].push(comment);
    });
  Object.values(map).forEach((list) => list.sort((a, b) => a.createdAt.localeCompare(b.createdAt)));
  return map;
});

const sortedComments = computed(() => {
  const sorted = [...rootComments.value];
  sorted.sort((a, b) =>
    sortOrder.value === 'desc'
      ? b.createdAt.localeCompare(a.createdAt)
      : a.createdAt.localeCompare(b.createdAt)
  );
  return sorted;
});

const canPost = computed(() => auth.isAuthed || site.settings.allowGuestComment);

const canDelete = (comment: Comment) => {
  if (!auth.isAuthed) return false;
  if (auth.isAdmin) return true;
  return auth.currentUser?.id === comment.authorId;
};

const submitComment = () => {
  if (!newComment.value.trim()) return;
  if (!auth.currentUser && !site.settings.allowGuestComment) return;
  let status: Comment['status'] = 'PUBLISHED';
  if (site.settings.commentReviewMode === 'ALL') {
    status = 'PENDING';
  } else if (site.settings.commentReviewMode === 'FIRST') {
    const hasPublished = site.comments.some(
      (comment) => comment.authorId === auth.currentUser?.id && comment.status === 'PUBLISHED'
    );
    status = hasPublished ? 'PUBLISHED' : 'PENDING';
  }
  site.addComment({
    articleId: props.articleId,
    authorId: auth.currentUser?.id ?? 'guest',
    authorName: auth.currentUser?.username ?? '游客',
    content: newComment.value,
    status,
  });
  newComment.value = '';
};

const submitReply = (parentId: string) => {
  if (!replyContent.value.trim()) return;
  site.addComment({
    articleId: props.articleId,
    parentId,
    authorId: auth.currentUser?.id ?? 'guest',
    authorName: auth.currentUser?.username ?? '游客',
    content: replyContent.value,
    status: 'PUBLISHED',
  });
  replyContent.value = '';
  replyingTo.value = null;
};

const toggleReply = (commentId: string) => {
  replyingTo.value = replyingTo.value === commentId ? null : commentId;
  replyContent.value = '';
};

const cancelReply = () => {
  replyingTo.value = null;
  replyContent.value = '';
};

const deleteComment = (commentId: string) => {
  site.removeComment(commentId);
};

const formatTime = (value: string) => dayjs(value).format('YYYY-MM-DD HH:mm');
</script>

<style scoped>
.comment-thread {
  margin-top: 24px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-form {
  margin-top: 16px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
  gap: 8px;
}

.comment-list {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  padding: 16px;
  border-radius: 12px;
  background: #f8fafc;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  color: #64748b;
  font-size: 13px;
}

.reply-box {
  margin-top: 12px;
  padding-left: 24px;
}

.reply-list {
  margin-top: 12px;
  padding-left: 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.reply-item {
  background: #fff;
  border-radius: 10px;
  padding: 12px;
}
</style>
