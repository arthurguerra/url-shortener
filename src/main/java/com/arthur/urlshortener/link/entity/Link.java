package com.arthur.urlshortener.link.entity;

import com.arthur.urlshortener.acesslog.entity.AccessLog;
import com.arthur.urlshortener.exception.InvalidUrlException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.apache.commons.validator.routines.UrlValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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

    private static final UrlValidator urlValidator =
            new UrlValidator(new String[]{"http", "https"}, UrlValidator.NO_FRAGMENTS);

    private static final Pattern DISALLOWED_SCHEMES = Pattern.compile("^(?i)(javascript|data):");

    protected Link() {
    }

    private Link(String originalUrl) {
        this.shortCode = generateShortCode();
        this.originalUrl = originalUrl;
        this.createdAt = LocalDateTime.now();
        this.clicks = 0L;
    }

    public static Link create(String originalUrl) {
        isValidUrl(originalUrl);
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

    private static void isValidUrl(String originalUrl) {
        if (!urlValidator.isValid(originalUrl) || DISALLOWED_SCHEMES.matcher(originalUrl).find()) {
            throw new InvalidUrlException("Invalid URL: " + originalUrl);
        }
    }
}