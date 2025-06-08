package com.arthur.urlshortener.link.repository;

import com.arthur.urlshortener.link.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    List<AccessLog> findAllByLinkShortCode(String shortCode);
}
