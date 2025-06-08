package com.arthur.urlshortener.acesslog.dto;

import com.arthur.urlshortener.acesslog.entity.AccessLog;

import java.time.LocalDateTime;

public record AccessLogResponse(String ip, String userAgent, LocalDateTime accessedAt) {
    public static AccessLogResponse from(AccessLog log) {
        return new AccessLogResponse(log.getIp(), log.getUserAgent(), log.getAccessedAt());
    }
}
