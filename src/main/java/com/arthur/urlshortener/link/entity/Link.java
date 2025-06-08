package com.arthur.urlshortener.link.entity;

import com.arthur.urlshortener.acesslog.entity.AccessLog;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "links")
public class Link {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String shortCode;

    @Column(nullable = false)
    private String originalUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Long clicks;

    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL)
    private List<AccessLog> accessLogs = new ArrayList<>();

    protected Link() {
        this.shortCode = null;
        this.originalUrl = null;
        this.createdAt = null;
    }

    private Link(String originalUrl) {
        this.shortCode = generateShortCode();
        this.originalUrl = originalUrl;
        this.createdAt = LocalDateTime.now();
        this.clicks = 0L;
    }

    public static Link create(String originalUrl) {
        Objects.requireNonNull(originalUrl, "URL must not be null");
        return new Link(originalUrl);
    }

    public void registerClick() {
        this.clicks++;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public Long getClicks() {
        return clicks;
    }

    public List<AccessLog> getAccessLogs() {
        return accessLogs;
    }

    private String generateShortCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}