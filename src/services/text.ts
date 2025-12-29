import { marked } from 'marked';
import DOMPurify from 'dompurify';

marked.setOptions({
  breaks: true,
});

export const renderMarkdown = (content: string): string => {
  return DOMPurify.sanitize(marked.parse(content) as string);
};

export const sanitizeHtml = (content: string): string => {
  return DOMPurify.sanitize(content);
};

export const truncateText = (content: string, length = 120): string => {
  const plain = content.replace(/<[^>]*>/g, '').replace(/\s+/g, ' ').trim();
  if (plain.length <= length) return plain;
  return `${plain.slice(0, length)}...`;
};
