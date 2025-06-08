package com.arthur.urlshortener.acesslog.service;

import com.arthur.urlshortener.acesslog.dto.AccessLogResponse;
import com.arthur.urlshortener.acesslog.repository.AccessLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;

    public AccessLogService(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    public List<AccessLogResponse> getLinkAccessLogs(String shortCode) {
        return accessLogRepository.findAllByLinkShortCode(shortCode)
                .stream()
                .map(AccessLogResponse::from)
                .toList();
    }
}
