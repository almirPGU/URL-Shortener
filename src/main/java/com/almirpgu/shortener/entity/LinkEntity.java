package com.almirpgu.shortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "links")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkEntity {

    @Id
    @Column(nullable = false, updatable = false, length = 36)
    private String id;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(nullable = false, unique = true, length = 32)
    private String shortCode;

    @Column(nullable = false)
    private long clicks;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastAccessedAt;
}
