package com.almirpgu.shortener.service;

import com.almirpgu.shortener.config.AppProperties;
import com.almirpgu.shortener.dto.CreateLinkRequest;
import com.almirpgu.shortener.dto.LinkResponse;
import com.almirpgu.shortener.dto.LinkStatsResponse;
import com.almirpgu.shortener.entity.LinkEntity;
import com.almirpgu.shortener.exception.InvalidUrlException;
import com.almirpgu.shortener.exception.LinkNotFoundException;
import com.almirpgu.shortener.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final AppProperties appProperties;
    private final StringRedisTemplate stringRedisTemplate;

    @Transactional
    public LinkResponse createLink(CreateLinkRequest request) {
        String originalUrl = normalizeAndValidateUrl(request.getOriginalUrl());

        LinkEntity existing = linkRepository.findByOriginalUrl(originalUrl).orElse(null);
        if (existing != null) {
            return toResponse(existing);
        }

        String shortCode = generateUniqueShortCode();
        LinkEntity entity = LinkEntity.builder()
                .id(UUID.randomUUID().toString())
                .originalUrl(originalUrl)
                .shortCode(shortCode)
                .clicks(0L)
                .createdAt(LocalDateTime.now())
                .build();

        LinkEntity saved = linkRepository.save(entity);
        cacheShortCode(saved.getShortCode(), saved.getOriginalUrl());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<LinkResponse> getLinks(Pageable pageable) {
        return linkRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public LinkStatsResponse getStats(String id) {
        LinkEntity entity = findById(id);
        return LinkStatsResponse.builder()
                .id(entity.getId())
                .originalUrl(entity.getOriginalUrl())
                .shortCode(entity.getShortCode())
                .clicks(entity.getClicks())
                .createdAt(entity.getCreatedAt())
                .lastAccessedAt(entity.getLastAccessedAt())
                .build();
    }

    @Transactional
    public void deleteLink(String id) {
        LinkEntity entity = findById(id);
        linkRepository.delete(entity);
        try {
            stringRedisTemplate.delete(cacheKey(entity.getShortCode()));
        } catch (Exception ignored) {
        }
    }

    @Transactional
    public String resolveOriginalUrl(String shortCode) {
        String cachedUrl = getFromCache(shortCode);
        if (cachedUrl != null) {
            LinkEntity entity = linkRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new LinkNotFoundException("Link not found"));
            increaseClicks(entity);
            return cachedUrl;
        }

        LinkEntity entity = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException("Link not found"));
        cacheShortCode(shortCode, entity.getOriginalUrl());
        increaseClicks(entity);
        return entity.getOriginalUrl();
    }

    private void increaseClicks(LinkEntity entity) {
        entity.setClicks(entity.getClicks() + 1);
        entity.setLastAccessedAt(LocalDateTime.now());
        linkRepository.save(entity);
    }

    private LinkEntity findById(String id) {
        return linkRepository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException("Link not found"));
    }

    private String generateUniqueShortCode() {
        int attempts = 0;
        while (attempts < 20) {
            String candidate = shortCodeGenerator.generate(appProperties.getShortCodeLength());
            if (!linkRepository.existsByShortCode(candidate)) {
                return candidate;
            }
            attempts++;
        }
        throw new IllegalStateException("Could not generate a unique short code");
    }

    private String normalizeAndValidateUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new InvalidUrlException("URL must not be blank");
        }

        String normalized = rawUrl.trim();
        try {
            URI uri = URI.create(normalized);
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                throw new InvalidUrlException("Only http and https URLs are supported");
            }
            if (uri.getHost() == null) {
                throw new InvalidUrlException("URL must contain a valid host");
            }
        } catch (IllegalArgumentException ex) {
            throw new InvalidUrlException("Invalid URL format");
        }

        return normalized;
    }

    private LinkResponse toResponse(LinkEntity entity) {
        return LinkResponse.builder()
                .id(entity.getId())
                .originalUrl(entity.getOriginalUrl())
                .shortCode(entity.getShortCode())
                .shortUrl(appProperties.getBaseUrl().replaceAll("/+$", "") + "/" + entity.getShortCode())
                .clicks(entity.getClicks())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private String getFromCache(String shortCode) {
        try {
            return stringRedisTemplate.opsForValue().get(cacheKey(shortCode));
        } catch (Exception ignored) {
            return null;
        }
    }

    private void cacheShortCode(String shortCode, String originalUrl) {
        try {
            stringRedisTemplate.opsForValue().set(
                    cacheKey(shortCode),
                    originalUrl,
                    Duration.ofSeconds(appProperties.getCacheTtlSeconds())
            );
        } catch (Exception ignored) {
        }
    }

    private String cacheKey(String shortCode) {
        return "shortener:url:" + shortCode;
    }
}
