package com.arthur.urlshortener.acesslog.repository;

import com.arthur.urlshortener.acesslog.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    List<AccessLog> findAllByLinkShortCode(String shortCode);
}
