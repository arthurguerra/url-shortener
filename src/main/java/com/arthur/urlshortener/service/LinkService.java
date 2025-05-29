package com.arthur.urlshortener.service;

import com.arthur.urlshortener.dto.ShortenResponseDto;
import com.arthur.urlshortener.entity.Link;
import com.arthur.urlshortener.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


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
        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Short code not found"));

        link.registerClick();
        linkRepository.save(link);

        return link.getOriginalUrl();
    }
}
