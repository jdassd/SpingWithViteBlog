-- V2.0 功能数据库迁移
-- 点赞、收藏、系列、版本历史、访问统计、友链等

-- 文章点赞表
CREATE TABLE article_likes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  article_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_article_likes_article FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
  CONSTRAINT fk_article_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT uk_article_likes UNIQUE (article_id, user_id)
);

-- 用户收藏夹表
CREATE TABLE favorites (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  is_public BOOLEAN NOT NULL DEFAULT FALSE,
  is_default BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 收藏夹文章关联表
CREATE TABLE favorite_articles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  favorite_id BIGINT NOT NULL,
  article_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_favorite_articles_favorite FOREIGN KEY (favorite_id) REFERENCES favorites(id) ON DELETE CASCADE,
  CONSTRAINT fk_favorite_articles_article FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
  CONSTRAINT uk_favorite_articles UNIQUE (favorite_id, article_id)
);

-- 文章系列表
CREATE TABLE series (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  description CLOB,
  cover_url VARCHAR(512),
  status VARCHAR(16) NOT NULL DEFAULT 'ONGOING',
  visibility VARCHAR(16) NOT NULL DEFAULT 'PUBLIC',
  sort_order INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 文章系列白名单表
CREATE TABLE series_whitelist (
  series_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  PRIMARY KEY (series_id, user_id),
  CONSTRAINT fk_series_whitelist_series FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE CASCADE,
  CONSTRAINT fk_series_whitelist_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 文章表新增字段: series_id, series_order, scheduled_at
ALTER TABLE articles ADD COLUMN series_id BIGINT;
ALTER TABLE articles ADD COLUMN series_order INT DEFAULT 0;
ALTER TABLE articles ADD COLUMN scheduled_at TIMESTAMP;
ALTER TABLE articles ADD CONSTRAINT fk_articles_series FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE SET NULL;

-- 文章版本历史表
CREATE TABLE article_versions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  article_id BIGINT NOT NULL,
  version_number INT NOT NULL,
  title VARCHAR(200) NOT NULL,
  content_raw CLOB NOT NULL,
  content_html CLOB,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_article_versions_article FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE
);

-- 访问统计表
CREATE TABLE page_views (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  article_id BIGINT,
  page_path VARCHAR(512),
  visitor_ip VARCHAR(64),
  user_id BIGINT,
  user_agent VARCHAR(512),
  referer VARCHAR(512),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_page_views_article FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
  CONSTRAINT fk_page_views_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 文章统计缓存表
CREATE TABLE article_stats (
  article_id BIGINT PRIMARY KEY,
  view_count BIGINT NOT NULL DEFAULT 0,
  like_count BIGINT NOT NULL DEFAULT 0,
  favorite_count BIGINT NOT NULL DEFAULT 0,
  comment_count BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_article_stats_article FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE
);

-- 友情链接表
CREATE TABLE friend_links (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  url VARCHAR(512) NOT NULL,
  description VARCHAR(500),
  logo_url VARCHAR(512),
  category VARCHAR(64),
  sort_order INT NOT NULL DEFAULT 0,
  is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
  status VARCHAR(16) NOT NULL DEFAULT 'UNCHECKED',
  last_check_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- SEO及排行相关配置初始化
INSERT INTO site_settings (setting_key, setting_value) VALUES ('seo.sitemap.enabled', 'true');
INSERT INTO site_settings (setting_key, setting_value) VALUES ('seo.sitemap.frequency', 'daily');
INSERT INTO site_settings (setting_key, setting_value) VALUES ('seo.og.enabled', 'true');
INSERT INTO site_settings (setting_key, setting_value) VALUES ('seo.og.default_image', '');
INSERT INTO site_settings (setting_key, setting_value) VALUES ('ranking.weight.views', '1');
INSERT INTO site_settings (setting_key, setting_value) VALUES ('ranking.weight.likes', '3');
INSERT INTO site_settings (setting_key, setting_value) VALUES ('ranking.weight.comments', '5');
INSERT INTO site_settings (setting_key, setting_value) VALUES ('ranking.weight.favorites', '2');
