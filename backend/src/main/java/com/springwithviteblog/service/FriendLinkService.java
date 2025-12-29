package com.springwithviteblog.service;

import com.springwithviteblog.domain.FriendLink;
import com.springwithviteblog.domain.LinkStatus;
import com.springwithviteblog.dto.FriendLinkDto;
import com.springwithviteblog.mapper.FriendLinkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendLinkService {

    private final FriendLinkMapper linkMapper;

    public List<FriendLink> findAll() {
        return linkMapper.findAll();
    }

    public List<FriendLink> findEnabled() {
        return linkMapper.findEnabled();
    }

    public Map<String, List<FriendLink>> findEnabledGroupedByCategory() {
        List<FriendLink> links = linkMapper.findEnabled();
        return links.stream().collect(Collectors.groupingBy(
                link -> link.getCategory() != null ? link.getCategory() : "默认"));
    }

    public FriendLink findById(Long id) {
        return linkMapper.findById(id);
    }

    @Transactional
    public FriendLink create(FriendLinkDto dto) {
        FriendLink link = new FriendLink();
        link.setName(dto.getName());
        link.setUrl(dto.getUrl());
        link.setDescription(dto.getDescription());
        link.setLogoUrl(dto.getLogoUrl());
        link.setCategory(dto.getCategory());
        link.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        link.setIsEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : true);
        link.setStatus(LinkStatus.UNCHECKED);
        linkMapper.insert(link);
        return link;
    }

    @Transactional
    public FriendLink update(Long id, FriendLinkDto dto) {
        FriendLink link = linkMapper.findById(id);
        if (link == null)
            return null;

        link.setName(dto.getName());
        link.setUrl(dto.getUrl());
        link.setDescription(dto.getDescription());
        link.setLogoUrl(dto.getLogoUrl());
        link.setCategory(dto.getCategory());
        if (dto.getSortOrder() != null) {
            link.setSortOrder(dto.getSortOrder());
        }
        if (dto.getIsEnabled() != null) {
            link.setIsEnabled(dto.getIsEnabled());
        }
        linkMapper.update(link);
        return link;
    }

    @Transactional
    public boolean delete(Long id) {
        return linkMapper.delete(id) > 0;
    }

    public LinkStatus checkLink(Long id) {
        FriendLink link = linkMapper.findById(id);
        if (link == null)
            return null;

        LinkStatus status = checkUrl(link.getUrl());
        linkMapper.updateStatus(id, status.name());
        return status;
    }

    public void checkAllLinks() {
        List<FriendLink> links = linkMapper.findAll();
        for (FriendLink link : links) {
            try {
                LinkStatus status = checkUrl(link.getUrl());
                linkMapper.updateStatus(link.getId(), status.name());
            } catch (Exception e) {
                log.error("Error checking link {}: {}", link.getUrl(), e.getMessage());
                linkMapper.updateStatus(link.getId(), LinkStatus.FAILED.name());
            }
        }
    }

    private LinkStatus checkUrl(String urlString) {
        try {
            java.net.URI uri = java.net.URI.create(urlString);
            java.net.URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(true);
            int code = conn.getResponseCode();
            conn.disconnect();
            return code >= 200 && code < 400 ? LinkStatus.OK : LinkStatus.FAILED;
        } catch (Exception e) {
            return LinkStatus.FAILED;
        }
    }

    public List<String> getCategories() {
        return linkMapper.findCategories();
    }
}
