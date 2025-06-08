package com.arthur.urlshortener.link.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "link_id", nullable = false)
    private Link link;

    private String ip;
    private String userAgent;
    private LocalDateTime accessedAt;

    protected AccessLog() {}

    private AccessLog(Link link, String ip, String userAgent) {
        this.link = link;
        this.ip = ip;
        this.userAgent = userAgent;
        this.accessedAt = LocalDateTime.now();
    }

    public static AccessLog create(Link link, String ip, String userAgent) {
        return new AccessLog(link, ip, userAgent);
    }

    public String getIp() {
        return ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public LocalDateTime getAccessedAt() {
        return accessedAt;
    }
}
