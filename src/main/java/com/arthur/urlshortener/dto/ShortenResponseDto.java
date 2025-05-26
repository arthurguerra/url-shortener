package com.arthur.urlshortener.dto;

public record ShortenResponseDto(
        String shortUrl,
        String originalUrl
) {}