package com.almirpgu.shortener.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String baseUrl;
    private int shortCodeLength = 8;
    private long cacheTtlSeconds = 86400;
}
