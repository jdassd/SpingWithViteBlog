<template>
  <div class="content-body" v-html="html"></div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  contentType?: string
  contentHtml?: string
  renderedContent?: string
  contentRaw?: string
}>()

const html = computed(() => props.renderedContent || props.contentHtml || props.contentRaw || '')
</script>

<style scoped>
.content-body {
  font-size: 16px;
  line-height: 1.8;
}

.content-body :deep(h1),
.content-body :deep(h2),
.content-body :deep(h3) {
  font-family: "Source Serif 4", "Times New Roman", serif;
  margin: 20px 0 12px;
}

.content-body :deep(pre) {
  background: #151310;
  color: #f5f1ea;
  padding: 16px;
  border-radius: 12px;
  overflow: auto;
}

.content-body :deep(code) {
  font-family: "JetBrains Mono", "Courier New", monospace;
  font-size: 14px;
}

.content-body :deep(img) {
  max-width: 100%;
  border-radius: 12px;
  margin: 16px 0;
}
</style>
