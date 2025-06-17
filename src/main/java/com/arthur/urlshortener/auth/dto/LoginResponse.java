package com.arthur.urlshortener.auth.dto;

public record LoginResponse(
        String token,
        String tokenType,
        Long expiresIn
) {}
