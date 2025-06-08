package com.arthur.urlshortener.acesslog.controller;

import com.arthur.urlshortener.acesslog.dto.AccessLogResponse;
import com.arthur.urlshortener.acesslog.service.AccessLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Access Logs", description = "Operations to view access logs of shortened links")
public class AccessLogController {

    private final AccessLogService accessLogService;

    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<List<AccessLogResponse>> getLogs(@PathVariable String shortCode) {
        return ResponseEntity.ok(accessLogService.getLinkAccessLogs(shortCode));
    }
}
