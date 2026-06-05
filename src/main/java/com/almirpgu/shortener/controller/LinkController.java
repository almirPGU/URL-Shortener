package com.almirpgu.shortener.controller;

import com.almirpgu.shortener.dto.CreateLinkRequest;
import com.almirpgu.shortener.dto.LinkResponse;
import com.almirpgu.shortener.dto.LinkStatsResponse;
import com.almirpgu.shortener.service.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @PostMapping
    public ResponseEntity<LinkResponse> create(@Valid @RequestBody CreateLinkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(linkService.createLink(request));
    }

    @GetMapping
    public ResponseEntity<Page<LinkResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(linkService.getLinks(pageable));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<LinkStatsResponse> stats(@PathVariable String id) {
        return ResponseEntity.ok(linkService.getStats(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        linkService.deleteLink(id);
        return ResponseEntity.noContent().build();
    }
}
