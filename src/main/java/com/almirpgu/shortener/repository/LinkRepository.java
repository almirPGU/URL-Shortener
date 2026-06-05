package com.almirpgu.shortener.repository;

import com.almirpgu.shortener.entity.LinkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<LinkEntity, String> {
    Optional<LinkEntity> findByShortCode(String shortCode);
    Optional<LinkEntity> findByOriginalUrl(String originalUrl);
    boolean existsByShortCode(String shortCode);
    boolean existsByOriginalUrl(String originalUrl);
    Page<LinkEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
