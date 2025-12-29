package com.springwithviteblog.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RateLimitRecord {
  private Long id;
  private String rateKey;
  private LocalDateTime windowStart;
  private Integer count;
}
