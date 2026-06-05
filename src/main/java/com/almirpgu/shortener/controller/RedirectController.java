package com.almirpgu.shortener.controller;

import com.almirpgu.shortener.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final LinkService linkService;

    @GetMapping("/{shortCode:[A-Za-z0-9]{8}}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = linkService.resolveOriginalUrl(shortCode);
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, URI.create(originalUrl).toString())
                .build();
    }
}
