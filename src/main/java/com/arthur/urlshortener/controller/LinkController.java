package com.arthur.urlshortener.controller;

import com.arthur.urlshortener.dto.ShortenRequestDto;
import com.arthur.urlshortener.dto.ShortenResponseDto;
import com.arthur.urlshortener.service.LinkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LinkController {

    @Autowired
    private LinkService linkService;

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponseDto> createShortLink(@Valid @RequestBody ShortenRequestDto request) {
        ShortenResponseDto response = linkService.createShortLink(request.url());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = linkService.getOriginalUrlAndRegisterClick(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}
