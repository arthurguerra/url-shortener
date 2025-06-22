package com.arthur.urlshortener.acesslog.entity;

import com.arthur.urlshortener.link.entity.Link;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "link_id", nullable = false)
    private Link link;

    @Getter
    private String ip;
    @Getter
    private String userAgent;
    @Getter
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
}
