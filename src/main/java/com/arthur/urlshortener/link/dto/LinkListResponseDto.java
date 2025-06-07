package com.arthur.urlshortener.link.dto;

public record LinkListResponseDto(
        String shortUrl,
        String originalUrl,
        Long clicks
) {}
