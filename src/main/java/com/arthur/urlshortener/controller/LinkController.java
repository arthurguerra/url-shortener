package com.arthur.urlshortener.controller;

import com.arthur.urlshortener.dto.ShortenRequestDto;
import com.arthur.urlshortener.dto.ShortenResponseDto;
import com.arthur.urlshortener.service.LinkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponseDto> createShortLink(@Valid @RequestBody ShortenRequestDto request) {
        ShortenResponseDto response = linkService.createShortLink(request.url());
        return ResponseEntity.ok(response);
    }
}
