package com.animesh.urlshortener.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.animesh.urlshortener.dto.UrlRequestDto;
import com.animesh.urlshortener.dto.UrlResponseDto;
import com.animesh.urlshortener.model.Url;
import com.animesh.urlshortener.model.UrlClick;
import com.animesh.urlshortener.repository.ClickRepository;
import com.animesh.urlshortener.repository.UrlRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private ClickRepository clickRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int MAX_REQUESTS_PER_MINUTE = 5;

    // ==============================
    // CREATE SHORT URL
    // ==============================
    public UrlResponseDto createShortUrl(UrlRequestDto request) {

        String shortCode = UUID.randomUUID().toString().substring(0, 6);

        Url url = new Url();
        url.setLongUrl(request.getLongUrl());
        url.setShortCode(shortCode);
        url.setCreatedAt(LocalDateTime.now());
        url.setIsActive(true);

        if (request.getExpiryMinutes() != null) {
            url.setExpiresAt(
                    LocalDateTime.now().plusMinutes(request.getExpiryMinutes()));
        }

        Url saved = urlRepository.save(url);

        if (request.getExpiryMinutes() != null) {

            redisTemplate.opsForValue().set(
                    shortCode,
                    saved.getLongUrl(),
                    request.getExpiryMinutes(),
                    TimeUnit.MINUTES);

        } else {

            redisTemplate.opsForValue().set(shortCode, saved.getLongUrl());
        }

        return new UrlResponseDto(
                saved.getId(),
                saved.getLongUrl(),
                saved.getShortCode());
    }

    // ==============================
    // REDIRECT
    // ==============================
    public ResponseEntity<Void> handleRedirect(
            String shortCode,
            HttpServletRequest request) {

        // 🔥 RATE LIMIT
        String clientIp = request.getRemoteAddr();
        String rateLimitKey = "rate_limit:" + clientIp;

        Long requestCount = redisTemplate.opsForValue().increment(rateLimitKey);

if (requestCount == null) {
    requestCount = 1L;
}

if (requestCount == 1) {
    redisTemplate.expire(rateLimitKey, 60, TimeUnit.SECONDS);
}

if (requestCount > MAX_REQUESTS_PER_MINUTE) {
    return ResponseEntity.status(429).build();
}

        // 🔥 CACHE-ASIDE
        String longUrl = redisTemplate.opsForValue().get(shortCode);
        Url url = null;

        if (longUrl == null) {

            url = urlRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new RuntimeException("URL not found"));

            if (url.getExpiresAt() != null &&
                    url.getExpiresAt().isBefore(LocalDateTime.now())) {

                return ResponseEntity.status(410).build();
            }

            longUrl = url.getLongUrl();
            redisTemplate.opsForValue().set(shortCode, longUrl);
        }

        // 🔥 ANALYTICS
        UrlClick click = new UrlClick();
        click.setShortCode(shortCode);
        click.setIpAddress(clientIp);
        click.setUserAgent(request.getHeader("User-Agent"));
        click.setClickedAt(LocalDateTime.now());

        clickRepository.save(click);

        return ResponseEntity.status(302)
                .header("Location", longUrl)
                .build();
    }

    public long getAnalytics(String shortCode) {
        return clickRepository.countByShortCode(shortCode);
    }
}