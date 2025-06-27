package com.arthur.urlshortener.link.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShortenCustomRequestDto(
        @NotNull(message = "URL must not be null")
        @NotBlank(message = "URL must not be empty")
        String url,

        @NotNull(message = "Custom short code must not be null")
        String shortCode
) {}
