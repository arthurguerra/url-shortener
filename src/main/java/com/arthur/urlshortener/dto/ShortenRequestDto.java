package com.arthur.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;

public record ShortenRequestDto(
        @NotBlank(message = "URL must not be empty")
        String url
) {}
