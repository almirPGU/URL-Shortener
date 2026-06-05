package com.almirpgu.shortener.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class LinkStatsResponse {
    String id;
    String originalUrl;
    String shortCode;
    long clicks;
    LocalDateTime createdAt;
    LocalDateTime lastAccessedAt;
}
