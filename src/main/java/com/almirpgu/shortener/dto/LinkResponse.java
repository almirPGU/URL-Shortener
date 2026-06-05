package com.almirpgu.shortener.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class LinkResponse {
    String id;
    String originalUrl;
    String shortCode;
    String shortUrl;
    long clicks;
    LocalDateTime createdAt;
}
