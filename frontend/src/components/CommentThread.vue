<template>
  <div class="comment-item">
    <div class="comment-head">
      <span class="comment-author">{{ comment.guestName || `User #${comment.userId || 'guest'}` }}</span>
      <span class="muted">{{ formatDate(comment.createdAt) }}</span>
      <span v-if="comment.status !== 'PUBLISHED'" class="status-pill">{{ comment.status }}</span>
    </div>
    <p class="comment-content">{{ comment.content }}</p>
    <div class="comment-actions">
      <el-button text size="small" @click="$emit('reply', comment)">Reply</el-button>
      <el-button
        v-if="canDelete"
        text
        type="danger"
        size="small"
        @click="$emit('delete', comment)"
      >
        Delete
      </el-button>
    </div>
    <div v-if="comment.replies?.length" class="comment-replies">
      <CommentThread
        v-for="reply in comment.replies"
        :key="reply.id"
        :comment="reply"
        :current-user-id="currentUserId"
        @reply="$emit('reply', $event)"
        @delete="$emit('delete', $event)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Comment } from '@/api/types'
import dayjs from 'dayjs'

const props = defineProps<{
  comment: Comment
  currentUserId?: number
}>()

defineEmits<{
  (e: 'reply', comment: Comment): void
  (e: 'delete', comment: Comment): void
}>()

const formatDate = (value?: string) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '')

const canDelete = computed(() => {
  if (!props.currentUserId || !props.comment.userId) {
    return false
  }
  return props.comment.userId === props.currentUserId
})
</script>

<style scoped>
.comment-item {
  padding: 12px 0;
  border-bottom: 1px dashed rgba(30, 27, 22, 0.1);
}

.comment-head {
  display: flex;
  gap: 10px;
  align-items: center;
  font-size: 13px;
}

.comment-author {
  font-weight: 600;
}

.status-pill {
  padding: 2px 6px;
  border-radius: 8px;
  background: rgba(214, 106, 45, 0.2);
  font-size: 11px;
}

.comment-content {
  margin: 8px 0;
  line-height: 1.6;
}

.comment-replies {
  margin-left: 20px;
  border-left: 2px solid rgba(30, 27, 22, 0.08);
  padding-left: 16px;
}
</style>
