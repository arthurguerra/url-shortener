package com.arthur.urlshortener.link.dto;

public record ShortenResponseDto(
        String shortUrl,
        String originalUrl
) {}