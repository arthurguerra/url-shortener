package com.arthur.urlshortener.dto;

public record LinkListResponseDto(
        String shortUrl,
        String originalUrl,
        Long clicks
) {}
