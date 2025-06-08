package com.arthur.urlshortener.link.service;

import com.arthur.urlshortener.acesslog.dto.AccessLogResponse;
import com.arthur.urlshortener.exception.LinkNotFoundException;
import com.arthur.urlshortener.link.dto.LinkListResponseDto;
import com.arthur.urlshortener.link.dto.LinkLogsResponse;
import com.arthur.urlshortener.link.dto.ShortenResponseDto;
import com.arthur.urlshortener.acesslog.entity.AccessLog;
import com.arthur.urlshortener.link.entity.Link;
import com.arthur.urlshortener.acesslog.repository.AccessLogRepository;
import com.arthur.urlshortener.link.repository.LinkRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class LinkService {

    private static final String APPLICATION_URL = "https://short.local/";

    private final LinkRepository linkRepository;

    private final AccessLogRepository accessLogRepository;

    public LinkService(LinkRepository linkRepository, AccessLogRepository accessLogRepository) {
        this.linkRepository = linkRepository;
        this.accessLogRepository = accessLogRepository;
    }

    public ShortenResponseDto createShortLink(String originalUrl) {
        Link link = Link.create(originalUrl);
        linkRepository.save(link);
        String shortUrl = APPLICATION_URL + link.getShortCode();

        return new ShortenResponseDto(originalUrl, shortUrl);
    }

    public String getOriginalUrlAndRegisterClick(String shortCode, HttpServletRequest request) {
        Link link = getLink(shortCode);
        link.registerClick();

        createAccessLog(link, request);

        linkRepository.save(link);

        return link.getOriginalUrl();
    }

    public List<LinkListResponseDto> getAllLinks() {
        return linkRepository.findAll().stream()
                .map(link -> new LinkListResponseDto(
                        link.getShortCode(),
                        link.getOriginalUrl(),
                        link.getClicks()))
                .toList();
    }

    public void deleteLink(String shortCode) {
        Link link = getLink(shortCode);
        linkRepository.delete(link);
    }

    public LinkLogsResponse getLinkWithLogs(String shortCode) {
        Link link = getLink(shortCode);

        return new LinkLogsResponse(
                link.getShortCode(),
                link.getOriginalUrl(),
                link.getClicks(),
                link.getAccessLogs()
                        .stream()
                        .map(AccessLogResponse::from)
                        .toList()
        );
    }

    private Link getLink(String shortCode) {
        return linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException("Short code not found: " + shortCode));
    }

    private void createAccessLog(Link link, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);

        AccessLog accessLog = AccessLog.create(link, ip, userAgent);
        accessLogRepository.save(accessLog);
    }
}
