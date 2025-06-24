package com.arthur.urlshortener.link.service;

import com.arthur.urlshortener.acesslog.dto.AccessLogResponse;
import com.arthur.urlshortener.acesslog.entity.AccessLog;
import com.arthur.urlshortener.acesslog.repository.AccessLogRepository;
import com.arthur.urlshortener.exception.InvalidUrlException;
import com.arthur.urlshortener.exception.LinkNotFoundException;
import com.arthur.urlshortener.link.dto.LinkListResponseDto;
import com.arthur.urlshortener.link.dto.LinkLogsResponse;
import com.arthur.urlshortener.link.dto.ShortenResponseDto;
import com.arthur.urlshortener.link.entity.Link;
import com.arthur.urlshortener.link.repository.LinkRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LinkService Tests")
class LinkServiceTest {

    public static final String IP = "127.0.0.1";
    public static final String USER_AGENT = "Mozilla/5.0";
    @Mock
    private LinkRepository linkRepository;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private AccessLogRepository accessLogRepository;
    @Captor
    private ArgumentCaptor<Link> linkCaptor;
    @Captor
    private ArgumentCaptor<AccessLog> accessLogCaptor;
    @InjectMocks
    private LinkService linkService;

    private final String validOriginalUrl = "https://www.google.com/";

    @Test
    @DisplayName("Should create short link with valid url successfully")
    void createShortLink_withValidUrl_successfully() {
        ShortenResponseDto response = linkService.createShortLink(validOriginalUrl);

        assertNotNull(response);
        assertEquals(validOriginalUrl, response.originalUrl());

        verify(linkRepository, times(1)).save(linkCaptor.capture());
        Link link = linkCaptor.getValue();
        assertEquals(0, link.getClicks());
        assertEquals(0, link.getAccessLogs().size());
        assertEquals(validOriginalUrl, link.getOriginalUrl());

        verifyNoMoreInteractions(linkRepository);
        verifyNoInteractions(accessLogRepository);
    }

    @ParameterizedTest(name = "[{index} - {0}]")
    @MethodSource("invalidUrls")
    @DisplayName("Should throw exception when create short link with invalid url")
    void createShortLink_withInvalidUrl_shouldThrowException(String url) {
        InvalidUrlException exception = assertThrows(InvalidUrlException.class,
                () -> linkService.createShortLink(url));

        assertEquals("Invalid URL: " + url, exception.getMessage());
        verifyNoInteractions(linkRepository);
        verifyNoInteractions(accessLogRepository);
    }

    @Test
    @DisplayName("Should get original url and register click with existing short code successfully")
    void getOriginalUrlAndRegisterClick_withExistingShortCode_successfully() {
        Link link = Link.create(validOriginalUrl);

        when(linkRepository.findByShortCode(link.getShortCode())).thenReturn(Optional.of(link));
        when(httpServletRequest.getRemoteAddr()).thenReturn(IP);
        when(httpServletRequest.getHeader(HttpHeaders.USER_AGENT)).thenReturn(USER_AGENT);

        String originalUrl = linkService.getOriginalUrlAndRegisterClick(link.getShortCode(), httpServletRequest);

        assertEquals(link.getOriginalUrl(), originalUrl);
        assertEquals(1, link.getClicks());

        verify(accessLogRepository).save(accessLogCaptor.capture());
        verify(linkRepository).save(linkCaptor.capture());
        Link savedLink = linkCaptor.getValue();
        AccessLog accessLog = accessLogCaptor.getValue();

        assertEquals(1, savedLink.getAccessLogs().size());
        assertNotNull(accessLog.getAccessedAt());

        verifyNoMoreInteractions(linkRepository);
        verifyNoMoreInteractions(accessLogRepository);
    }

    @Test
    @DisplayName("Should throw exception when does not exists a Link with the informed short code")
    void getOriginalUrlAndRegisterClick_withNonExistingShortCode_shouldThrowException() {
        LinkNotFoundException exception = assertThrows(LinkNotFoundException.class,
                () -> linkService.getOriginalUrlAndRegisterClick("non-existing", httpServletRequest));

        assertEquals("Short code not found: non-existing", exception.getMessage());
        verify(linkRepository).findByShortCode("non-existing");
        verifyNoInteractions(accessLogRepository);
    }

    @Test
    void getAllLinks_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Link> linkPage = new PageImpl<>(List.of(
                Link.create("https://www.google.com/"),
                Link.create("https://www.yahoo.com/"),
                Link.create("https://www.bing.com/")
        ), pageable, 3);

        when(linkRepository.findAll(pageable)).thenReturn(linkPage);

        Page<LinkListResponseDto> result = linkService.getAllLinks(pageable);

        assertNotNull(result);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).extracting(
                        LinkListResponseDto::originalUrl,
                        LinkListResponseDto::clicks)
                .containsExactly(tuple("https://www.google.com/", 0L)
                        , tuple("https://www.yahoo.com/", 0L)
                        , tuple("https://www.bing.com/", 0L));
    }

    @Test
    @DisplayName("Should find and delete successfully")
    void deleteLink_shouldFindAndDeleteSuccessfully() {
        Link link = Link.create(validOriginalUrl);

        when(linkRepository.findByShortCode(link.getShortCode())).thenReturn(Optional.of(link));

        linkService.deleteLink(link.getShortCode());

        verify(linkRepository).findByShortCode(link.getShortCode());
        verify(linkRepository).delete(link);
        verifyNoMoreInteractions(linkRepository);
    }

    @Test
    @DisplayName("Should throw exception when does not exists a Link with the informed short code")
    void deleteLink_withNonExistingShortCode_shouldThrowException() {
        LinkNotFoundException exception = assertThrows(LinkNotFoundException.class,
                () -> linkService.deleteLink("non-existing"));

        assertEquals("Short code not found: non-existing", exception.getMessage());
        verify(linkRepository).findByShortCode("non-existing");
        verifyNoInteractions(accessLogRepository);
    }

    @Test
    @DisplayName("Should get link with logs successfully")
    void getLinkWithLogs_shouldReturnLinkLogsResponse() {
        Link link = Link.create(validOriginalUrl);
        link.addAccessLog(AccessLog.create(link, IP, USER_AGENT));

        when(linkRepository.findByShortCode(link.getShortCode())).thenReturn(Optional.of(link));

        LinkLogsResponse result = linkService.getLinkWithLogs(link.getShortCode());

        assertNotNull(result);
        assertEquals(link.getAccessLogs().size(), result.logs().size());
        assertThat(result.logs()).extracting(
                AccessLogResponse::ip,
                AccessLogResponse::userAgent
        ).containsExactly(tuple(IP, USER_AGENT));
    }

    @Test
    @DisplayName("Should throw exception when does not exists a Link with the informed short code")
    void getLinkWithLogs_withNonExistingShortCode_shouldThrowException() {
        LinkNotFoundException exception = assertThrows(LinkNotFoundException.class,
                () -> linkService.getLinkWithLogs("non-existing"));

        assertEquals("Short code not found: non-existing", exception.getMessage());
        verify(linkRepository).findByShortCode("non-existing");
        verifyNoInteractions(accessLogRepository);
    }

    private static Stream<String> invalidUrls() {
        return Stream.of(" ",
                "https://",
                "https://www.",
                "htttps://www",
                "www.google.com/",
                "javascript:https://www.google.com/",
                "data:https://www.google.com/");
    }
}