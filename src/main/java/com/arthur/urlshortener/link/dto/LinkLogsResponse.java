package com.arthur.urlshortener.link.dto;

import com.arthur.urlshortener.acesslog.dto.AccessLogResponse;

import java.util.List;

public record LinkLogsResponse(
        String shortUrl,
        String originalUrl,
        Long clicks,
        List<AccessLogResponse> logs
) {}
