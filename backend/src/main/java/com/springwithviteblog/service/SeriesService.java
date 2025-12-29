package com.springwithviteblog.service;

import com.springwithviteblog.domain.Series;
import com.springwithviteblog.domain.SeriesStatus;
import com.springwithviteblog.domain.Visibility;
import com.springwithviteblog.dto.SeriesDto;
import com.springwithviteblog.mapper.SeriesMapper;
import com.springwithviteblog.mapper.SeriesWhitelistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesMapper seriesMapper;
    private final SeriesWhitelistMapper whitelistMapper;

    public List<Series> findAll() {
        return seriesMapper.findAll();
    }

    public List<Series> findPublic() {
        return seriesMapper.findPublic();
    }

    public Series findById(Long id) {
        return seriesMapper.findById(id);
    }

    @Transactional
    public Series create(SeriesDto dto) {
        Series series = new Series();
        series.setTitle(dto.getTitle());
        series.setDescription(dto.getDescription());
        series.setCoverUrl(dto.getCoverUrl());
        series.setStatus(dto.getStatus() != null ? SeriesStatus.valueOf(dto.getStatus()) : SeriesStatus.ONGOING);
        series.setVisibility(dto.getVisibility() != null ? Visibility.valueOf(dto.getVisibility()) : Visibility.PUBLIC);
        series.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        seriesMapper.insert(series);

        if (dto.getWhitelistUserIds() != null && !dto.getWhitelistUserIds().isEmpty()) {
            for (Long userId : dto.getWhitelistUserIds()) {
                whitelistMapper.insert(series.getId(), userId);
            }
        }
        return series;
    }

    @Transactional
    public Series update(Long id, SeriesDto dto) {
        Series series = seriesMapper.findById(id);
        if (series == null)
            return null;

        series.setTitle(dto.getTitle());
        series.setDescription(dto.getDescription());
        series.setCoverUrl(dto.getCoverUrl());
        if (dto.getStatus() != null) {
            series.setStatus(SeriesStatus.valueOf(dto.getStatus()));
        }
        if (dto.getVisibility() != null) {
            series.setVisibility(Visibility.valueOf(dto.getVisibility()));
        }
        if (dto.getSortOrder() != null) {
            series.setSortOrder(dto.getSortOrder());
        }
        seriesMapper.update(series);

        // Update whitelist
        whitelistMapper.deleteBySeriesId(id);
        if (dto.getWhitelistUserIds() != null) {
            for (Long userId : dto.getWhitelistUserIds()) {
                whitelistMapper.insert(id, userId);
            }
        }
        return series;
    }

    @Transactional
    public boolean delete(Long id) {
        Series series = seriesMapper.findById(id);
        if (series == null)
            return false;
        whitelistMapper.deleteBySeriesId(id);
        seriesMapper.delete(id);
        return true;
    }

    public int getArticleCount(Long seriesId) {
        return seriesMapper.countArticles(seriesId);
    }

    public List<Long> getWhitelistUserIds(Long seriesId) {
        return whitelistMapper.findUserIdsBySeriesId(seriesId);
    }

    public boolean canUserAccessSeries(Series series, Long userId, boolean isAdmin) {
        if (isAdmin)
            return true;
        if (series.getVisibility() == Visibility.PUBLIC)
            return true;
        if (series.getVisibility() == Visibility.LOGIN_ONLY && userId != null)
            return true;
        if (series.getVisibility() == Visibility.WHITELIST && userId != null) {
            return whitelistMapper.existsBySeriesAndUser(series.getId(), userId) > 0;
        }
        if (series.getVisibility() == Visibility.ADMIN_ONLY)
            return false;
        return false;
    }
}
