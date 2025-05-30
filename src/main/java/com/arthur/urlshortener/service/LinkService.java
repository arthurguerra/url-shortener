package com.arthur.urlshortener.service;

import com.arthur.urlshortener.dto.LinkListResponseDto;
import com.arthur.urlshortener.dto.ShortenResponseDto;
import com.arthur.urlshortener.entity.Link;
import com.arthur.urlshortener.exception.LinkNotFoundException;
import com.arthur.urlshortener.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class LinkService {

    private static final String APPLICATION_URL = "https://short.local/";

    @Autowired
    private LinkRepository linkRepository;

    public ShortenResponseDto createShortLink(String originalUrl) {
        Link link = Link.create(originalUrl);
        linkRepository.save(link);
        String shortUrl = APPLICATION_URL + link.getShortCode();

        return new ShortenResponseDto(originalUrl, shortUrl);
    }

    public String getOriginalUrlAndRegisterClick(String shortCode) {
        Link link = getLink(shortCode);

        link.registerClick();
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

    private Link getLink(String shortCode) {
        return linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException("Short code not found: " + shortCode));
    }
}
