package com.arthur.urlshortener.service;

import com.arthur.urlshortener.dto.ShortenResponseDto;
import com.arthur.urlshortener.entity.Link;
import com.arthur.urlshortener.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LinkService {

    private static final String APPLICATION_URL = "https://short.local/";

    @Autowired
    private LinkRepository linkRepository;

    public ShortenResponseDto createShortLink(String originalUrl) {
        Link link = Link.create(originalUrl);
        String shortUrl = APPLICATION_URL + link.getShortCode();

        return new ShortenResponseDto(originalUrl, shortUrl);
    }
}
