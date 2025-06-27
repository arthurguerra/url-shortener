package com.arthur.urlshortener.link.controller;

import com.arthur.urlshortener.common.PaginationRequest;
import com.arthur.urlshortener.link.dto.DeleteLinkResponse;
import com.arthur.urlshortener.link.dto.LinkListResponseDto;
import com.arthur.urlshortener.link.dto.LinkLogsResponse;
import com.arthur.urlshortener.link.dto.ShortenCustomRequestDto;
import com.arthur.urlshortener.link.dto.ShortenRequestDto;
import com.arthur.urlshortener.link.dto.ShortenResponseDto;
import com.arthur.urlshortener.link.service.LinkService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Links", description = "Operations related to shortened links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponseDto> createShortLink(@Valid @RequestBody ShortenRequestDto request) {
        ShortenResponseDto response = linkService.createShortLink(request.url());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/shorten/custom")
    public ResponseEntity<ShortenResponseDto> createCustomShortLink(@Valid @RequestBody ShortenCustomRequestDto request) {
        ShortenResponseDto response = linkService.createCustomShortLink(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode, HttpServletRequest request) {
        String originalUrl = linkService.getOriginalUrlAndRegisterClick(shortCode, request);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }

    @GetMapping("/api/links")
    public ResponseEntity<Page<LinkListResponseDto>> getAllLinks(@Valid PaginationRequest paginationRequest) {
        return ResponseEntity.ok(linkService.getAllLinks(paginationRequest.toPageable()));
    }

    @DeleteMapping("/api/link/{shortCode}")
    public ResponseEntity<DeleteLinkResponse> deleteLink(@PathVariable String shortCode) {
        linkService.deleteLink(shortCode);
        return ResponseEntity.ok(new DeleteLinkResponse("Link deleted successfully"));
    }

    @GetMapping("/api/log/{shortCode}")
    public ResponseEntity<LinkLogsResponse> getLinkWithLogs(@PathVariable String shortCode) {
        return ResponseEntity.ok(linkService.getLinkWithLogs(shortCode));
    }
}
