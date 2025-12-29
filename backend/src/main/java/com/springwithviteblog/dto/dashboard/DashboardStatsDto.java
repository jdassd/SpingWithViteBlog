package com.springwithviteblog.dto.dashboard;

import com.springwithviteblog.dto.article.ArticleSummaryDto;
import com.springwithviteblog.dto.audit.AuditLogDto;
import com.springwithviteblog.dto.comment.CommentDto;
import java.util.List;

public class DashboardStatsDto {
  private int articlesTotal;
  private int articlesPublished;
  private int articlesDraft;
  private int articlesArchived;
  private int commentsTotal;
  private int commentsPending;
  private int commentsPublished;
  private int commentsBlocked;
  private int usersTotal;
  private int albumsTotal;
  private int photosTotal;
  private List<ArticleSummaryDto> recentArticles;
  private List<CommentDto> recentComments;
  private List<AuditLogDto> recentAudits;

  public int getArticlesTotal() {
    return articlesTotal;
  }

  public void setArticlesTotal(int articlesTotal) {
    this.articlesTotal = articlesTotal;
  }

  public int getArticlesPublished() {
    return articlesPublished;
  }

  public void setArticlesPublished(int articlesPublished) {
    this.articlesPublished = articlesPublished;
  }

  public int getArticlesDraft() {
    return articlesDraft;
  }

  public void setArticlesDraft(int articlesDraft) {
    this.articlesDraft = articlesDraft;
  }

  public int getArticlesArchived() {
    return articlesArchived;
  }

  public void setArticlesArchived(int articlesArchived) {
    this.articlesArchived = articlesArchived;
  }

  public int getCommentsTotal() {
    return commentsTotal;
  }

  public void setCommentsTotal(int commentsTotal) {
    this.commentsTotal = commentsTotal;
  }

  public int getCommentsPending() {
    return commentsPending;
  }

  public void setCommentsPending(int commentsPending) {
    this.commentsPending = commentsPending;
  }

  public int getCommentsPublished() {
    return commentsPublished;
  }

  public void setCommentsPublished(int commentsPublished) {
    this.commentsPublished = commentsPublished;
  }

  public int getCommentsBlocked() {
    return commentsBlocked;
  }

  public void setCommentsBlocked(int commentsBlocked) {
    this.commentsBlocked = commentsBlocked;
  }

  public int getUsersTotal() {
    return usersTotal;
  }

  public void setUsersTotal(int usersTotal) {
    this.usersTotal = usersTotal;
  }

  public int getAlbumsTotal() {
    return albumsTotal;
  }

  public void setAlbumsTotal(int albumsTotal) {
    this.albumsTotal = albumsTotal;
  }

  public int getPhotosTotal() {
    return photosTotal;
  }

  public void setPhotosTotal(int photosTotal) {
    this.photosTotal = photosTotal;
  }

  public List<ArticleSummaryDto> getRecentArticles() {
    return recentArticles;
  }

  public void setRecentArticles(List<ArticleSummaryDto> recentArticles) {
    this.recentArticles = recentArticles;
  }

  public List<CommentDto> getRecentComments() {
    return recentComments;
  }

  public void setRecentComments(List<CommentDto> recentComments) {
    this.recentComments = recentComments;
  }

  public List<AuditLogDto> getRecentAudits() {
    return recentAudits;
  }

  public void setRecentAudits(List<AuditLogDto> recentAudits) {
    this.recentAudits = recentAudits;
  }
}
