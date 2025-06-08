package com.arthur.urlshortener.link.controller;

import com.arthur.urlshortener.link.dto.DeleteLinkResponse;
import com.arthur.urlshortener.link.dto.LinkListResponseDto;
import com.arthur.urlshortener.link.dto.ShortenRequestDto;
import com.arthur.urlshortener.link.dto.ShortenResponseDto;
import com.arthur.urlshortener.link.service.LinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponseDto> createShortLink(@Valid @RequestBody ShortenRequestDto request) {
        ShortenResponseDto response = linkService.createShortLink(request.url());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode, HttpServletRequest request) {
        String originalUrl = linkService.getOriginalUrlAndRegisterClick(shortCode, request);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }

    @GetMapping("/api/links")
    public ResponseEntity<List<LinkListResponseDto>> getAllLinks() {
        return ResponseEntity.ok(linkService.getAllLinks());
    }

    @DeleteMapping("/api/links/{shortCode}")
    public ResponseEntity<DeleteLinkResponse> deleteLink(@PathVariable String shortCode) {
        linkService.deleteLink(shortCode);
        return ResponseEntity.ok(new DeleteLinkResponse("Link deleted successfully"));
    }
}
