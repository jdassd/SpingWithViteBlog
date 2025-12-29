package com.springwithviteblog.service;

import com.springwithviteblog.domain.RateLimitRecord;
import com.springwithviteblog.mapper.RateLimitMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {
  private final RateLimitMapper rateLimitMapper;

  public RateLimitService(RateLimitMapper rateLimitMapper) {
    this.rateLimitMapper = rateLimitMapper;
  }

  public boolean tryConsume(String key, int limit, int windowSeconds) {
    RateLimitRecord record = rateLimitMapper.findByKey(key);
    LocalDateTime now = LocalDateTime.now();
    if (record == null) {
      RateLimitRecord created = new RateLimitRecord();
      created.setRateKey(key);
      created.setWindowStart(now);
      created.setCount(1);
      rateLimitMapper.insert(created);
      return true;
    }

    long elapsed = Duration.between(record.getWindowStart(), now).getSeconds();
    if (elapsed >= windowSeconds) {
      rateLimitMapper.update(record.getId(), now, 1);
      return true;
    }

    if (record.getCount() >= limit) {
      return false;
    }

    rateLimitMapper.update(record.getId(), record.getWindowStart(), record.getCount() + 1);
    return true;
  }
}
