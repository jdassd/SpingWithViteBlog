package com.springwithviteblog.service;

import com.springwithviteblog.domain.Article;
import com.springwithviteblog.domain.ArticleVersion;
import com.springwithviteblog.mapper.ArticleVersionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleVersionService {

    private final ArticleVersionMapper versionMapper;

    @Value("${app.article.version.keep-count:20}")
    private int keepCount;

    @Transactional
    public ArticleVersion createVersion(Article article) {
        int nextVersion = versionMapper.getMaxVersionNumber(article.getId()) + 1;

        ArticleVersion version = new ArticleVersion();
        version.setArticleId(article.getId());
        version.setVersionNumber(nextVersion);
        version.setTitle(article.getTitle());
        version.setContentRaw(article.getContentRaw());
        version.setContentHtml(article.getContentHtml());
        versionMapper.insert(version);

        // Clean up old versions
        versionMapper.deleteOldVersions(article.getId(), keepCount);

        return version;
    }

    public List<ArticleVersion> getVersions(Long articleId) {
        return versionMapper.findByArticle(articleId);
    }

    public ArticleVersion getVersion(Long versionId) {
        return versionMapper.findById(versionId);
    }
}
