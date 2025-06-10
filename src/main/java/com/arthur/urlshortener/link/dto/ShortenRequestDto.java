package com.arthur.urlshortener.link.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShortenRequestDto(
        @NotNull(message = "URL must not be null")
        @NotBlank(message = "URL must not be empty")
        String url
) {}
